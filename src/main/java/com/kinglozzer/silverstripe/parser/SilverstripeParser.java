package com.kinglozzer.silverstripe.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.kinglozzer.silverstripe.SilverstripeBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;

import static com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes.*;

public class SilverstripeParser implements PsiParser {
    private static final ArrayDeque<Pair<String, Marker>> blockStack = new ArrayDeque<>();

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

            // Something bad has happened if we get here (usually something like an unfinished/broken block at EOF)
            // as we've been kicked out of the parsing loop early. Just try to carry on parsing anyway
            builder.advanceLexer();
        }

        rootMarker.done(root);

        return builder.getTreeBuilt();
    }

    private void parseRoot(PsiBuilder builder) {
        parseStatements(builder);

        // Drop any leftover block markers - these are either unfinished closed blocks, or open blocks at the top-level
        while (!blockStack.isEmpty()) {
            Marker marker = blockStack.pop().getSecond();
            marker.drop();
        }
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
            // Parse known open blocks (e.g. include / require)
            if (parseKnownOpenBlock(builder)) {
                return true;
            }

            // Parse a block start statement for blocks that are *not* known open blocks. This includes known closed
            // blocks - e.g. if, loop, with - and custom blocks where we can't tell if they're open or closed
            Marker closedBlockMarker = builder.mark();
            String blockName = parseBlockStartStatement(builder);
            if (blockName != null) {
                // Push the marker and block type to the stack
                blockStack.push(new Pair<>(blockName, closedBlockMarker));

                // Kick off nested statement parsing - for closed blocks this is the "body" of the statement, for open
                // blocks we later drop the closedBlockMarker so this doesn't appear nested to the finished tree
                parseStatementsMaybeNestedInClosedBlock(builder);

                // Nested parsing has now ended, so look for a closing statement
                // Note that the block stack indexes start at 1, not 0
                int blockPositionInStack = parseClosedBlockEndStatement(builder);
                if (blockPositionInStack != 0) {
                    // Pop the last block statement marker from the top of the stack
                    Pair<String, Marker> pair = blockStack.pop();

                    // If the end statement we found wasn't for the statement at the top of the stack, we have to
                    // assume that the statement at the top of the stack is an open block and not a closed block.
                    // E.g. we have an open block inside a closed block, and we just encountered the end statement
                    // for the outer closed block. So we drop the marker and jump out of the parsing loop. If we're
                    // in a nested state, the parent parsing loop will parse the statement again and re-check against
                    // the "new" top of the stack (in the example above, it will re-parse the end statement for the
                    // outer closed block)
                    if (blockPositionInStack != 1) {
                        pair.getSecond().drop();
                        return false;
                    }

                    // The end statement we found matches what's at the top of the stack so we can complete the block
                    closedBlockMarker.done(SS_BLOCK_STATEMENT);
                    return true;
                }

                // The end statement we found doesn't match anything in the stack, so something is wrong
                closedBlockMarker.done(SS_UNFINISHED_BLOCK_STATEMENT);
                return true;
            }
            closedBlockMarker.rollbackTo();

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
     * of the nested parsing loop (e.g. when we find an end statement for a closed block)
     */
    private boolean parseNestedStatement(PsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();

        if (tokenType == SS_BLOCK_START) {
            // If we encounter an end block we're expecting, roll back to before it and jump out of the nested parsing
            // loop - parseOpenBlockEndStatement() in the non-nested parseStatement() should take over from here
            Marker closedBlockEndMarker = builder.mark();
            int blockPositionInStack = parseClosedBlockEndStatement(builder);
            if (blockPositionInStack != 0) {
                closedBlockEndMarker.rollbackTo();
                return false;
            }
            closedBlockEndMarker.drop();

            // If we encounter an else_if or else, roll back to before it and jump out of the nested parsing loop.
            // parseClosedBlockEndStatement() in the un-nested parseStatement() will re-parse it and handle marking
            // the block as complete
            Marker continuationMarker = builder.mark();
            if (parseElseIfOrElseStatement(builder)) {
                while (!blockStack.isEmpty()) {
                    Pair<String, Marker> pair = blockStack.peek();
                    if (pair.getFirst().equals("if")) {
                        continuationMarker.rollbackTo();
                        return false;
                    }

                    blockStack.pop();
                }
            }
            continuationMarker.drop();
        }

        return parseStatement(builder);
    }

    /**
     * Parse nested statements that may be within a closed block - e.g. the body of an "if" or "loop"
     */
    private void parseStatementsMaybeNestedInClosedBlock(PsiBuilder builder) {
        parseNestedStatements(builder);

        // We may have been kicked out of the nested parsing loop by an else_if / else statement.
        // In which case, we need to re-enter nested parsing again to handle the body of the else_if / else
        Marker continuationMarker = builder.mark();
        if (parseBlockStartStatement(builder) != null) {
            continuationMarker.drop();
            parseStatementsMaybeNestedInClosedBlock(builder);
        } else {
            continuationMarker.rollbackTo();
        }
    }

    /**
     * Loose checks for keyword tokens - the lexer and parseBlockStartStatement() will ensure the rest of the
     * statement is valid
     */
    private boolean parseElseIfOrElseStatement(PsiBuilder builder) {
        Marker statementMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        if (consumeToken(builder, SS_ELSE_IF_KEYWORD)) {
            statementMarker.rollbackTo();
            return true;
        }

        if (consumeToken(builder, SS_ELSE_KEYWORD)) {
            statementMarker.rollbackTo();
            return true;
        }

        statementMarker.rollbackTo();
        return false;
    }

    /**
     * Parse known open blocks, e.g. include / base_tag / require
     */
    private boolean parseKnownOpenBlock(PsiBuilder builder) {
        Marker blockMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        IElementType blockType = parseKnownOpenBlockType(builder);
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

    private IElementType parseKnownOpenBlockType(PsiBuilder builder) {
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

    /**
     * Try to parse a block start statement - this may be a start statement for a closed block, or an open block
     * that the parser isn't aware of (e.g. thirdparty modules)
     *
     * @return the name of the block, or null if this doesn't appear to be a block start statement
     */
    private @Nullable String parseBlockStartStatement(PsiBuilder builder) {
        Marker blockStartMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        Pair<String, IElementType> blockTypeResult = parseOpenBlockType(builder);
        if (blockTypeResult == null) {
            blockStartMarker.rollbackTo();
            return null;
        }

        String blockType = blockTypeResult.getFirst();
        IElementType elementType = blockTypeResult.getSecond();
        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (type == SS_BLOCK_END) {
                consumeToken(builder, type);
                blockStartMarker.done(elementType);
                return blockType;
            }

            if (!parseStatement(builder)) {
                builder.advanceLexer();
                break;
            }
        }

        blockStartMarker.rollbackTo();
        return null;
    }

    /**
     * Attempt to parse the type of block this statement represents
     *
     * @return A pair containing the string block name and the matching element type, or null if invalid
     */
    private @Nullable Pair<String, IElementType> parseOpenBlockType(PsiBuilder builder) {
        Marker ifStatementMarker = builder.mark();
        String text = builder.getTokenText() != null ? builder.getTokenText().toLowerCase() : "";
        if (consumeToken(builder, SS_IF_KEYWORD)) {
            ifStatementMarker.drop();
            return new Pair<>(text, SS_IF_STATEMENT);
        } else {
            ifStatementMarker.rollbackTo();
        }

        Marker elseIfStatementMarker = builder.mark();
        if (consumeToken(builder, SS_ELSE_IF_KEYWORD)) {
            elseIfStatementMarker.drop();
            return new Pair<>("if", SS_ELSE_IF_STATEMENT);
        } else {
            elseIfStatementMarker.rollbackTo();
        }

        Marker elseStatementMarker = builder.mark();
        if (consumeToken(builder, SS_ELSE_KEYWORD)) {
            elseStatementMarker.drop();
            return new Pair<>("if", SS_ELSE_STATEMENT);
        } else {
            elseStatementMarker.rollbackTo();
        }

        Marker startStatementMarker = builder.mark();
        if (consumeToken(builder, SS_START_KEYWORD)) {
            startStatementMarker.drop();
            return new Pair<>(text, SS_BLOCK_START_STATEMENT);
        } else {
            startStatementMarker.rollbackTo();
        }

        Marker cachedStatementMarker = builder.mark();
        if (consumeToken(builder, SS_CACHED_KEYWORD)) {
            cachedStatementMarker.drop();
            return new Pair<>(text, SS_CACHED_STATEMENT);
        } else {
            cachedStatementMarker.rollbackTo();
        }

        Marker genericBlockStatementMarker = builder.mark();
        if (consumeToken(builder, SS_BLOCK_NAME)) {
            genericBlockStatementMarker.drop();
            return new Pair<>(text, SS_BLOCK_START_STATEMENT);
        } else {
            genericBlockStatementMarker.rollbackTo();
        }

        return null;
    }

    /**
     * Attempt to find an end statement for a closed block, based on what's in the block stack
     *
     * @return The position in the stack (starting from 1), or 0 if invalid/not found in the stack
     */
    private int parseClosedBlockEndStatement(PsiBuilder builder) {
        Marker blockEndMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        String text = builder.getTokenText() != null ? builder.getTokenText().toLowerCase() : "";
        if (!consumeToken(builder, SS_END_KEYWORD)) {
            blockEndMarker.rollbackTo();
            return 0;
        }

        if (!consumeToken(builder, SS_BLOCK_END)) {
            blockEndMarker.rollbackTo();
            return 0;
        }

        blockEndMarker.done(SS_BLOCK_END_STATEMENT);

        // Look through the stack for a matching block
        String blockName = text.substring(4);
        int positionInStack = 1;
        for (Pair<String, Marker> pair : blockStack) {
            if (blockName.equals(pair.getFirst())) {
                return positionInStack;
            }

            positionInStack++;
        }

        return 0;
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
