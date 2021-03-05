package com.kinglozzer.silverstripe.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.kinglozzer.silverstripe.SilverstripeBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

import static com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes.*;

public class SilverstripeParser implements PsiParser {
    // todo -rename to closed block stack
    private static final Stack<String> openBlockStack = new Stack<>();
    private static final Stack<String> blockStack = new Stack<>();

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType root, PsiBuilder builder) {
        builder.setDebugMode(true);

        final Marker rootMarker = builder.mark();

        while (!builder.eof()) {
            parseRoot(builder);

            if (builder.eof()) {
                break;
            }

            // Something bad has happened if we get here - usually something like an unfinished/broken block at EOF -
            // as we've been kicked out of the parsing loop early. Just try to carry on parsing anyway
            builder.advanceLexer();
        }

        rootMarker.done(root);

        return builder.getTreeBuilt();
    }

    private void parseRoot(PsiBuilder builder) {
        parseStatements(builder);
    }

    private void parseStatements(PsiBuilder builder) {
        Marker statementsMarker = builder.mark();
        boolean statementsParsed = false;

        // This loop parses statements until either something unknown is encountered, or we intentionally jump out
        while (true) {
            Marker optionalStatementMarker = builder.mark();

            if (parseStatement(builder)) {
                optionalStatementMarker.drop();
                statementsParsed = true;
            } else {
                optionalStatementMarker.rollbackTo();
                break;
            }
        }

        if (statementsParsed) {
            statementsMarker.done(SS_STATEMENTS);
        } else {
            statementsMarker.drop();
        }
    }

    private boolean parseStatement(PsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();

        if (tokenType == SS_BLOCK_START) {
            // todo - if we parse an end statement at this point, it's unexpected

            // Parse closed blocks (e.g. include / require)
            if (parseClosedBlock(builder)) {
                return true;
            }

            // Parse an open block start statement for blocks that are known open blocks - e.g. if, loop, with
            Marker openBlockMarker = builder.mark();
            if (parseOpenBlockStartStatement(builder)) {
                // Kick off nested statement parsing for the body of the statement
                parseNestedOpenBlockStatements(builder);
                // Nested parsing has now ended, so look for a closing statement
                if (parseOpenBlockEndStatement(builder, true)) {
                    openBlockStack.pop();
                    openBlockMarker.done(SS_BLOCK_STATEMENT);
                } else {
                    openBlockMarker.done(SS_UNFINISHED_BLOCK_STATEMENT);
                }
                return true;
            }
            openBlockMarker.rollbackTo();

            Marker blockMarker = builder.mark();
            if (parseBlockStatement(builder)) {
                // Kick off nested statement parsing as this may be an open block
                parseNestedOpenBlockStatements(builder);
                // Nested parsing has now ended, so look for a closing statement in case this was an open block
                Marker blockEndMarker = builder.mark();
                if (parseBlockEndStatement(builder, true)) {
                    blockEndMarker.drop();
                    blockMarker.done(SS_BLOCK_STATEMENT);
                } else {
                    blockEndMarker.rollbackTo();
                    blockMarker.drop();
                }

                blockStack.pop();
                return true;
            }
            blockMarker.rollbackTo();

            // If we reach this point, we've got a bad block
            return parseBadBlockStatement(builder);
        }

        if (tokenType == SS_LEFT_BRACE || tokenType == SS_RIGHT_BRACE) {
            builder.advanceLexer();
            return true;
        }

        if (tokenType == SS_LOOKUP) {
            parseLookup(builder);
            return true;
        }

        if (tokenType == SS_COMMENT_START) {
            return parseComment(builder);
        }

        if (tokenType == SS_STRING || tokenType == SS_IDENTIFIER) {
            builder.advanceLexer();
            return true;
        }

        if (tokenType == SS_INCLUDE_FILE || tokenType == SS_NAMED_ARGUMENT_NAME || tokenType == SS_EQUALS || tokenType == SS_COMMA) {
            builder.advanceLexer();
            return true;
        }

        if (tokenType == SS_THEME_DIR) {
            builder.advanceLexer();
            return true;
        }

        if (tokenType == SS_TRANSLATION_IDENTIFIER || tokenType == SS_IS_KEYWORD) {
            builder.advanceLexer();
            return true;
        }

        if (tokenType == SS_REQUIRE_CSS || tokenType == SS_REQUIRE_JS || tokenType == SS_REQUIRE_THEMED_CSS || tokenType == SS_REQUIRE_THEMED_JS) {
            consumeToken(builder, tokenType);
            if (builder.getTokenType() == SS_LEFT_PARENTHESIS) {
                parseLookupStepArguments(builder);
            }
            return true;
        }

        if (tokenType == SS_AND_OR_OPERATOR || tokenType == SS_COMPARISON_OPERATOR) {
            builder.advanceLexer();
            return true;
        }

        if (tokenType == SS_NUMBER || tokenType == SS_PRIMITIVE) {
            builder.advanceLexer();
            return true;
        }

        if (tokenType == SS_TEXT) {
            builder.advanceLexer();
            return true;
        }

        return false;
    }

    private void parseNestedStatements(PsiBuilder builder) {
        Marker statementsMarker = builder.mark();
        boolean statementsParsed = false;

        // This loop parses statements until either something unknown is encountered, or we intentionally jump out
        while (true) {
            Marker optionalStatementMarker = builder.mark();

            if (parseNestedStatement(builder)) {
                optionalStatementMarker.drop();
                statementsParsed = true;
            } else {
                optionalStatementMarker.rollbackTo();
                break;
            }
        }

        if (statementsParsed) {
            statementsMarker.done(SS_STATEMENTS);
        } else {
            statementsMarker.drop();
        }
    }

    /**
     * This adds some extra checks to parseStatement() to cater for occasions when we deliberately want to break out
     * of the nested parsing loop (e.g. closing open block statements)
     */
    private boolean parseNestedStatement(PsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();

        if (tokenType == SS_BLOCK_START) {
            //
            // GENERIC CLOSED BLOCK STATEMENTS
            //
            // If we encounter an unexpected end block (i.e. mismatched), ignore it and carry on parsing - we let
            // annotators handle highlighting it as an error
            Marker unexpectedBlockEndMarker = builder.mark();
            if (parseBlockEndStatement(builder, false)) {
                unexpectedBlockEndMarker.rollbackTo();
                return false;
            }
            unexpectedBlockEndMarker.drop();

            // If we encounter an end block we're expecting, roll back to before it and jump out of the nested parsing
            // loop - parseOpenBlockEndStatement() in the non-nested parseStatement() should take over from here
            Marker blockEndMarker = builder.mark();
            if (parseBlockEndStatement(builder, true)) {
                blockEndMarker.rollbackTo();
                return false;
            }
            blockEndMarker.drop();

            //
            // KNOWN CLOSED BLOCK STATEMENTS
            //
            // If we encounter an unexpected end block (i.e. mismatched), ignore it and carry on parsing - we let
            // annotators handle highlighting it as an error
            // todo - rename to closed block
            Marker unexpectedEndBlockMarker = builder.mark();
            if (parseOpenBlockEndStatement(builder, false)) {
                unexpectedEndBlockMarker.drop();
                return true;
            }
            unexpectedEndBlockMarker.rollbackTo();

            // If we encounter an end block we're expecting, roll back to before it and jump out of the nested parsing
            // loop - parseOpenBlockEndStatement() in the non-nested parseStatement() should take over from here
            // todo - rename to closed block
            Marker openBlockEndMarker = builder.mark();
            if (parseOpenBlockEndStatement(builder, true)) {
                openBlockEndMarker.rollbackTo();
                return false;
            }
            openBlockEndMarker.drop();

            // If we encounter an "continuation" statement (i.e. an else_if or else), roll back to before it and jump
            // out of the nested parsing loop - parseOpenBlockEndStatement() in the non-nested parseStatement() should
            // take over from here
            Marker continuationMarker = builder.mark();
            if (parseOpenBlockContinuationStatement(builder)) {
                openBlockStack.pop();
                continuationMarker.rollbackTo();
                return false;
            }
            continuationMarker.drop();
        }

        return parseStatement(builder);
    }

    /**
     * Parse nested statements within an open block - e.g. the body of an "if" or "loop"
     */
    private void parseNestedOpenBlockStatements(PsiBuilder builder) {
        parseNestedStatements(builder);

        // We may have been kicked out of the nested parsing loop by an else_if / else statement.
        // In which case, we need to re-enter nested parsing again to handle the body of the else_if / else
        Marker continuationMarker = builder.mark();
        if (parseOpenBlockStartStatement(builder)) {
            continuationMarker.drop();
            parseNestedOpenBlockStatements(builder);
        } else {
            continuationMarker.rollbackTo();
        }
    }

    private boolean parseOpenBlockContinuationStatement(PsiBuilder builder) {
        Marker statementMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        if (openBlockStack.isEmpty() // todo - intentionally split over 2 lines to aid debugging a very intermittent exception
            || !openBlockStack.peek().equals("if")) {
            statementMarker.rollbackTo();
            return false;
        }

        if (consumeToken(builder, SS_ELSE_IF_KEYWORD)) {
            // We don't bother checking the rest of the else_if statement for validity - just assume the Lexer covers it
            statementMarker.rollbackTo();
            return true;
        }

        if (consumeToken(builder, SS_ELSE_KEYWORD)) {
            // We don't bother checking the rest of the else statement for validity - just assume the Lexer covers it
            statementMarker.rollbackTo();
            return true;
        }

        statementMarker.rollbackTo();
        return false;
    }

    private boolean parseClosedBlock(PsiBuilder builder) {
        Marker blockMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        IElementType blockType = parseClosedBlockType(builder);
        if (blockType == null) {
            blockMarker.rollbackTo();
            return false;
        }

        while (!builder.eof()) {
            if (builder.getTokenType() == SS_UNFINISHED_BLOCK_STATEMENT) {
                builder.advanceLexer();
                blockMarker.drop();
                return false;
            }

            if (builder.getTokenType() == SS_BLOCK_END) {
                consumeToken(builder, SS_BLOCK_END);
                blockMarker.done(blockType);
                return true;
            }

            if (!parseStatement(builder)) {
                builder.advanceLexer();
                break;
            }
        }

        blockMarker.rollbackTo();
        return false;
    }

    private IElementType parseClosedBlockType(PsiBuilder builder) {
        Marker translationMarker = builder.mark();
        if (consumeToken(builder, SS_TRANSLATION_KEYWORD)) {
            translationMarker.drop();
            return SS_TRANSLATION_STATEMENT;
        } else {
            translationMarker.rollbackTo();
        }

        Marker includeMarker = builder.mark();
        if (consumeToken(builder, SS_INCLUDE_KEYWORD)) {
            includeMarker.drop();
            return SS_INCLUDE_STATEMENT;
        } else {
            includeMarker.rollbackTo();
        }

        Marker requireMarker = builder.mark();
        if (consumeToken(builder, SS_REQUIRE_KEYWORD)) {
            requireMarker.drop();
            return SS_REQUIRE_STATEMENT;
        } else {
            requireMarker.rollbackTo();
        }

        Marker simpleKeywordMarker = builder.mark();
        if (consumeToken(builder, SS_SIMPLE_KEYWORD)) {
            simpleKeywordMarker.drop();
            return SS_BLOCK_SIMPLE_STATEMENT;
        } else {
            simpleKeywordMarker.rollbackTo();
        }

        return null;
    }

    private boolean parseOpenBlockStartStatement(PsiBuilder builder) {
        Marker startBlockMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        IElementType blockType = parseOpenBlockType(builder);
        if (blockType == null) {
            startBlockMarker.rollbackTo();
            return false;
        }

        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (type == SS_BLOCK_END) {
                if (builder.getTokenType() == SS_UNFINISHED_BLOCK_STATEMENT) {
                    builder.advanceLexer();
                    startBlockMarker.drop();
                    return false;
                }

                consumeToken(builder, type);
                startBlockMarker.done(blockType);
                return true;
            }

            if (!parseStatement(builder)) {
                builder.advanceLexer();
                break;
            }
        }

        startBlockMarker.rollbackTo();
        return false;
    }

    private @Nullable IElementType parseOpenBlockType(PsiBuilder builder) {
        Marker ifStatementMarker = builder.mark();
        String text = builder.getTokenText() != null ? builder.getTokenText().toLowerCase() : "";
        if (consumeToken(builder, SS_IF_KEYWORD)) {
            ifStatementMarker.drop();
            openBlockStack.push(text);
            return SS_IF_STATEMENT;
        } else {
            ifStatementMarker.rollbackTo();
        }

        Marker elseIfStatementMarker = builder.mark();
        if (consumeToken(builder, SS_ELSE_IF_KEYWORD)) {
            elseIfStatementMarker.drop();
            openBlockStack.push("if");
            return SS_ELSE_IF_STATEMENT;
        } else {
            elseIfStatementMarker.rollbackTo();
        }

        Marker elseStatementMarker = builder.mark();
        if (consumeToken(builder, SS_ELSE_KEYWORD)) {
            elseStatementMarker.drop();
            openBlockStack.push("if");
            return SS_ELSE_STATEMENT;
        } else {
            elseStatementMarker.rollbackTo();
        }

        Marker startStatementMarker = builder.mark();
        if (consumeToken(builder, SS_START_KEYWORD)) {
            startStatementMarker.drop();
            openBlockStack.push(text);
            return SS_BLOCK_START_STATEMENT;
        } else {
            startStatementMarker.rollbackTo();
        }

        Marker cachedStatementMarker = builder.mark();
        if (consumeToken(builder, SS_CACHED_KEYWORD)) {
            cachedStatementMarker.drop();
            openBlockStack.push(text);
            return SS_CACHED_STATEMENT;
        } else {
            cachedStatementMarker.rollbackTo();
        }

        return null;
    }

    private boolean parseOpenBlockEndStatement(PsiBuilder builder, boolean matchStack) {
        Marker endBlockMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);
        String text = builder.getTokenText() != null ? builder.getTokenText().toLowerCase() : "";

        if (!openBlockStack.empty()) {
            String targetText = "end_" + openBlockStack.peek();

            if (matchStack) {
                // We're looking for an end_ statement that matches what's in the stack
                if (!text.equals(targetText)) {
                    endBlockMarker.rollbackTo();
                    return false;
                }
            } else {
                // We're looking for anything *except* an end_ statement that matches what's in the stack
                if (text.equals(targetText)) {
                    endBlockMarker.rollbackTo();
                    return false;
                }
            }
        }

        if (!consumeToken(builder, SS_END_KEYWORD)) {
            endBlockMarker.rollbackTo();
            return false;
        }

        if (!consumeToken(builder, SS_BLOCK_END)) {
            endBlockMarker.rollbackTo();
            return false;
        }

        endBlockMarker.done(SS_BLOCK_END_STATEMENT);
        return true;
    }

    private boolean parseBlockStatement(PsiBuilder builder) {
        Marker startBlockMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        Marker blockTypeMarker = builder.mark();
        String blockType = builder.getTokenText();
        if (!consumeToken(builder, SS_BLOCK_NAME)) {
            blockTypeMarker.rollbackTo();
            return false;
        }

        blockTypeMarker.done(SS_BLOCK_NAME);
        blockStack.push(blockType);

        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (type == SS_BLOCK_END) {
                if (builder.getTokenType() == SS_UNFINISHED_BLOCK_STATEMENT) {
                    builder.advanceLexer();
                    startBlockMarker.drop();
                    return false;
                }

                consumeToken(builder, type);
                startBlockMarker.done(SS_BLOCK_SIMPLE_STATEMENT);
                return true;
            }

            if (!parseStatement(builder)) {
                builder.advanceLexer();
                break;
            }
        }

        startBlockMarker.rollbackTo();
        return false;
    }

    private boolean parseBlockEndStatement(PsiBuilder builder, boolean matchStack) {
        Marker endBlockMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        String text = builder.getTokenText() != null ? builder.getTokenText().toLowerCase() : "";
        if (!consumeToken(builder, SS_END_KEYWORD)) {
            endBlockMarker.rollbackTo();
            return false;
        }

        String blockName = text.substring(4);
        if (!blockStack.empty()) {
            if (matchStack) {
                // We're looking for an end_ statement that matches what's in the stack
                if (!blockName.equals(blockStack.peek())) {
                    endBlockMarker.rollbackTo();
                    return false;
                }
            } else {
                // We're looking for anything *except* an end_ statement that matches what's in the stack
                if (blockName.equals(blockStack.peek())) {
                    endBlockMarker.rollbackTo();
                    return false;
                }
            }

            if (consumeToken(builder, SS_BLOCK_END)) {
                endBlockMarker.done(SS_BLOCK_END_STATEMENT);
                return true;
            }
        }

        endBlockMarker.rollbackTo();
        return false;
    }

    private boolean parseBadBlockStatement(PsiBuilder builder) {
        Marker badBlockMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);
        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (type == SS_BAD_BLOCK_STATEMENT || type == SS_UNFINISHED_BLOCK_STATEMENT) {
                consumeToken(builder, type);
                badBlockMarker.error(SilverstripeBundle.message("ss.parser.bad.block"));
                return true;
            }

            builder.advanceLexer();
        }

        badBlockMarker.rollbackTo();
        return false;
    }

    private void parseLookup(PsiBuilder builder) {
        Marker lookupMarker = builder.mark();
        parseLookupSteps(builder);
        lookupMarker.done(SS_LOOKUP);
    }

    private void parseLookupSteps(PsiBuilder builder) {
        TokenSet lookupTokens = TokenSet.create(SS_LOOKUP, SS_IDENTIFIER, SS_DOT);
        while (!builder.eof() && lookupTokens.contains(builder.getTokenType())) {
            IElementType type = builder.getTokenType();
            if (type == SS_DOT) {
                consumeToken(builder, type);
                continue;
            }

            parseLookupStep(builder);
        }
    }

    private void parseLookupStep(PsiBuilder builder) {
        Marker lookupStep = builder.mark();
        consumeToken(builder, builder.getTokenType());

        if (builder.getTokenType() == SS_LEFT_PARENTHESIS) {
            parseLookupStepArguments(builder);
        }

        lookupStep.done(SS_LOOKUP_STEP);
    }

    private void parseLookupStepArguments(PsiBuilder builder) {
        consumeToken(builder, SS_LEFT_PARENTHESIS);
        Marker lookupStepArgsMarker = builder.mark();

        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (type == SS_RIGHT_PARENTHESIS) {
                lookupStepArgsMarker.done(SS_LOOKUP_STEP_ARGS);
                consumeToken(builder, SS_RIGHT_PARENTHESIS);
                return;
            }

            if (type == SS_COMMA) {
                consumeToken(builder, SS_COMMA);
                continue;
            }

            if (!parseLookupArg(builder)) {
                break;
            }
        }

        lookupStepArgsMarker.drop();
    }

    private boolean parseLookupArg(PsiBuilder builder) {
        return parseStatement(builder);
    }

    private boolean parseComment(PsiBuilder builder) {
        Marker commentMarker = builder.mark();
        while (!builder.eof()) {
            if (builder.getTokenType() == SS_COMMENT_END) {
                consumeToken(builder, SS_COMMENT_END);
                commentMarker.done(SS_COMMENT_STATEMENT);
                return true;
            }
            builder.advanceLexer();
        }

        commentMarker.drop();
        return false;
    }

    private boolean consumeToken(PsiBuilder builder, IElementType token) {
        if (builder.getTokenType() == token) {
            builder.advanceLexer();
            return true;
        }

        return false;
    }
}
