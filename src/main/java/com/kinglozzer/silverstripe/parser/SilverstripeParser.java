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
    private final ArrayDeque<Pair<String, Marker>> blockStack = new ArrayDeque<>();

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType root, PsiBuilder builder) {
        blockStack.clear();
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

            Marker closedBlockMarker = builder.mark();

            // Parse a block start statement for blocks that are *not* known open blocks. This includes known closed
            // blocks - e.g. if, loop, with - and custom blocks where we can't tell if they're open or closed.
            // As we don't yet know if the statement is an open or closed statement, we mark the start statement here
            // and complete it as either open or closed later based on whether a matching end statement was found
            Marker blockStartStatementMarker = builder.mark();

            Pair<String, IElementType> blockStartStatement = parseBlockStartStatement(builder);
            String blockName = blockStartStatement.getFirst();
            if (blockName != null) {
                // Push the marker and block type to the stack
                blockStack.push(new Pair<>(blockName, closedBlockMarker));

                // Kick off nested statement parsing - for closed blocks this is the "body" of the statement, for open
                // blocks we later drop the closedBlockMarker so this doesn't appear nested to the finished tree
                Marker preNestedStatementsMarker = builder.mark();
                parseStatementsMaybeNestedInClosedBlock(builder);

                // Nested parsing has now ended, so look for a closing statement
                Marker blockEndMarker = builder.mark();
                if (parseClosedBlockEndStatement(builder)) {
                    blockEndMarker.drop();

                    // Mark the open statement marker as done - we know that this is a closed block marker now, so we
                    // just use the element type we parsed from the block start statement
                    blockStartStatementMarker.doneBefore(blockStartStatement.getSecond(), preNestedStatementsMarker);
                    preNestedStatementsMarker.drop();

                    // Pop the last block statement marker from the top of the stack
                    Pair<String, Marker> pair = blockStack.pop();

                    // If the end statement we found matches what's at the top of the stack, we can complete the block
                    // here. Otherwise we encountered an end statement that is probably for a parent block (which is
                    // also a "parent" parsing loop). When this occurs, parseStatementsMaybeNestedInClosedBlock() has
                    // already completed the closedBlockMarker so we don't need to do it again here
                    if (pair.getSecond() == closedBlockMarker) {
                        closedBlockMarker.done(SS_CLOSED_BLOCK_STATEMENT);
                    }
                    return true;
                }

                // We reach this point when we encounter a closed block end statement, after parsing statements that
                // follow an unrecognised open block. E.g. an open block inside a closed block: we parse the open block
                // and everything after it in parseStatementsMaybeNestedInClosedBlock() above, and as soon as we hit
                // the end statement for the parent closed block, we end up here.
                //
                // We can also end up here when an unfinished closed block is parsed - e.g. an if without and end_if.
                // The element type in blockStartStatement will contain either an element type for a known closed block
                // start statement (e.g. SS_IF_STATEMENT for an if) or SS_BLOCK_START_STATEMENT when we're unsure
                // whether the statement is an open block, or an unfinished closed block as it's unrecognised. If the
                // statement is an SS_BLOCK_START_STATEMENT, at this point we have to assume it's an open block and
                // mark it as such instead
                //
                // We can now:
                // - Mark the start statement type
                // - Roll the lexer back to just before the end block
                // - Drop the closed block marker as this is an open block, not a closed block
                // - Pop the marker from the stack now we're finished with this block
                IElementType statementType = blockStartStatement.getSecond();
                if (statementType == SS_BLOCK_START_STATEMENT) {
                    statementType = SS_OPEN_BLOCK_STATEMENT;
                }

                blockStartStatementMarker.doneBefore(statementType, preNestedStatementsMarker);
                preNestedStatementsMarker.drop();
                blockEndMarker.rollbackTo();
                closedBlockMarker.drop();
                blockStack.pop();

                return true;
            }

            blockStartStatementMarker.drop();
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
        boolean nestedStatementsParsed = false;

        // This loop parses statements until either something unknown is encountered, or we intentionally jump out
        while (true) {
            Marker optionalStatementMarker = builder.mark();

            Pair<Boolean, Boolean> result = parseNestedStatement(builder);
            boolean shouldContinue = result.getFirst();
            nestedStatementsParsed = nestedStatementsParsed || result.getSecond();

            if (shouldContinue) {
                optionalStatementMarker.drop();
            } else {
                optionalStatementMarker.rollbackTo();
                break;
            }
        }

        // If the statements parsed are nested (i.e. in a completed closed block), mark them as such
        if (nestedStatementsParsed) {
            statementsMarker.done(SS_NESTED_STATEMENTS);
        } else {
            statementsMarker.drop();
        }
    }

    /**
     * This adds some extra checks to parseStatement() to cater for occasions when we deliberately want to break out
     * of the nested parsing loop (e.g. when we find an end statement for a closed block)
     *
     * @return A pair indicating whether to continue parsing, and whether parsed statements should be marked as nested
     */
    private Pair<Boolean, Boolean> parseNestedStatement(PsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();

        if (tokenType == SS_BLOCK_START) {
            // If we encounter an end block we're expecting, roll back to before it and jump out of the nested parsing
            // loop - parseOpenBlockEndStatement() in the non-nested parseStatement() should take over from here
            Marker closedBlockEndMarker = builder.mark();
            if (parseClosedBlockEndStatement(builder)) {
                closedBlockEndMarker.rollbackTo();
                return new Pair<>(false, true);
            }
            closedBlockEndMarker.drop();

            // If we encounter an else_if or else, roll back to before it and jump out of the nested parsing loop.
            // parseClosedBlockEndStatement() in the un-nested parseStatement() will re-parse it and handle marking
            // the block as complete
            Marker continuationMarker = builder.mark();
            if (parseElseIfOrElseStatement(builder) != null) {
                continuationMarker.rollbackTo();
                if (!blockStack.isEmpty() && blockStack.peek().getFirst().equals("if")) {
                    return new Pair<>(false, true);
                }

                return new Pair<>(false, false);
            }
            continuationMarker.drop();
        }

        boolean statementParsed = parseStatement(builder);
        return new Pair<>(statementParsed, false);
    }

    /**
     * Parse nested statements that may be within a closed block - e.g. the body of an "if" or "loop"
     */
    private void parseStatementsMaybeNestedInClosedBlock(PsiBuilder builder) {
        parseNestedStatements(builder);

        // We may have been kicked out of the nested parsing loop by an else_if / else statement.
        // In which case, we need to re-enter nested parsing again to handle the body of the else_if / else
        if (!blockStack.isEmpty()) {
            Pair<String, Marker> pair = blockStack.peek();
            if (pair.getFirst().equals("if")) {
                Marker elseIfOrElseMarker = builder.mark();
                IElementType statementType = parseElseIfOrElseStatement(builder);
                if (statementType != null) {
                    elseIfOrElseMarker.done(statementType);
                    parseStatementsMaybeNestedInClosedBlock(builder);
                } else {
                    elseIfOrElseMarker.rollbackTo();
                }
            }
        }
    }

    private @Nullable IElementType parseElseIfOrElseStatement(PsiBuilder builder) {
        Marker elseIfOrElseStatementMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        IElementType statementType = null;
        Marker elseIfStatementMarker = builder.mark();
        if (consumeToken(builder, SS_ELSE_IF_KEYWORD)) {
            elseIfStatementMarker.drop();
            statementType = SS_ELSE_IF_STATEMENT;
        } else {
            elseIfStatementMarker.rollbackTo();
        }

        Marker elseStatementMarker = builder.mark();
        if (consumeToken(builder, SS_ELSE_KEYWORD)) {
            elseStatementMarker.drop();
            statementType = SS_ELSE_STATEMENT;
        } else {
            elseStatementMarker.rollbackTo();
        }

        if (statementType == null) {
            elseIfOrElseStatementMarker.rollbackTo();
            return null;
        }

        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (type == SS_BLOCK_END) {
                consumeToken(builder, type);
                elseIfOrElseStatementMarker.drop();
                return statementType;
            }

            if (!parseStatement(builder)) {
                builder.advanceLexer();
                break;
            }
        }

        elseIfOrElseStatementMarker.rollbackTo();
        return null;
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
            return SS_OPEN_BLOCK_STATEMENT;
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
    private Pair<String, IElementType> parseBlockStartStatement(PsiBuilder builder) {
        Marker blockStartMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        Pair<String, IElementType> blockTypeResult = parseOpenOrClosedBlockType(builder);
        if (blockTypeResult == null) {
            blockStartMarker.rollbackTo();
            return new Pair<>(null, null);
        }

        String blockType = blockTypeResult.getFirst();
        IElementType elementType = blockTypeResult.getSecond();
        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (type == SS_BLOCK_END) {
                consumeToken(builder, type);
                blockStartMarker.drop();
                return new Pair<>(blockType, elementType);
            }

            if (!parseStatement(builder)) {
                builder.advanceLexer();
                break;
            }
        }

        blockStartMarker.rollbackTo();
        return new Pair<>(null, null);
    }

    /**
     * Attempt to parse the type of block this statement represents
     *
     * @return A pair containing the string block name and the matching element type, or null if invalid
     */
    private @Nullable Pair<String, IElementType> parseOpenOrClosedBlockType(PsiBuilder builder) {
        Marker ifStatementMarker = builder.mark();
        String text = builder.getTokenText() != null ? builder.getTokenText().toLowerCase() : "";
        if (consumeToken(builder, SS_IF_KEYWORD)) {
            ifStatementMarker.drop();
            return new Pair<>(text, SS_IF_STATEMENT);
        } else {
            ifStatementMarker.rollbackTo();
        }

        Marker startStatementMarker = builder.mark();
        if (consumeToken(builder, SS_START_KEYWORD)) {
            startStatementMarker.drop();
            return new Pair<>(text, SS_CLOSED_BLOCK_START_STATEMENT);
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
    private boolean parseClosedBlockEndStatement(PsiBuilder builder) {
        Marker blockEndMarker = builder.mark();
        consumeToken(builder, SS_BLOCK_START);

        String text = builder.getTokenText() != null ? builder.getTokenText().toLowerCase() : "";
        if (!consumeToken(builder, SS_END_KEYWORD)) {
            blockEndMarker.rollbackTo();
            return false;
        }

        if (!consumeToken(builder, SS_BLOCK_END)) {
            blockEndMarker.rollbackTo();
            return false;
        }

        if (blockStack.isEmpty()) {
            blockEndMarker.rollbackTo();
            return false;
        }

        String blockName = text.substring(4);
        Pair <String, Marker> pair = blockStack.peek();
        if (blockName.equals(pair.getFirst())) {
            blockEndMarker.done(SS_CLOSED_BLOCK_END_STATEMENT);
            return true;
        }

        blockEndMarker.rollbackTo();
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
