package com.kinglozzer.silverstripe.ide.braces;

import com.intellij.codeInsight.highlighting.BraceMatcher;
import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.BidirectionalMap;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SilverstripeBraceMatcher implements BraceMatcher {
    private static final int SILVERSTRIPE_BRACE_GROUP = 1;
    private static final int SILVERSTRIPE_PARENTHESES_GROUP = 2;
    private static final int SILVERSTRIPE_COMMENT_GROUP = 3;
    private static final int SILVERSTRIPE_CLOSED_BLOCK_GROUP = 4;

    private static final BidirectionalMap<IElementType, IElementType> PAIRING_TOKENS = new BidirectionalMap<>();

    static {
        PAIRING_TOKENS.put(SilverstripeTokenTypes.SS_RIGHT_BRACE, SilverstripeTokenTypes.SS_LEFT_BRACE);
        PAIRING_TOKENS.put(SilverstripeTokenTypes.SS_RIGHT_PARENTHESIS, SilverstripeTokenTypes.SS_LEFT_PARENTHESIS);
        PAIRING_TOKENS.put(SilverstripeTokenTypes.SS_COMMENT_END, SilverstripeTokenTypes.SS_COMMENT_START);
        PAIRING_TOKENS.put(SilverstripeTokenTypes.SS_BLOCK_END, SilverstripeTokenTypes.SS_BLOCK_START);
    }

    @Override
    public int getBraceTokenGroupId(@NotNull IElementType tokenType) {
        if (tokenType == SilverstripeTokenTypes.SS_LEFT_BRACE || tokenType == SilverstripeTokenTypes.SS_RIGHT_BRACE) {
            return SILVERSTRIPE_BRACE_GROUP;
        }
        if (tokenType == SilverstripeTokenTypes.SS_LEFT_PARENTHESIS || tokenType == SilverstripeTokenTypes.SS_RIGHT_PARENTHESIS) {
            return SILVERSTRIPE_PARENTHESES_GROUP;
        }
        if (tokenType == SilverstripeTokenTypes.SS_COMMENT_START || tokenType == SilverstripeTokenTypes.SS_COMMENT_END) {
            return SILVERSTRIPE_COMMENT_GROUP;
        }
        if (tokenType == SilverstripeTokenTypes.SS_BLOCK_START || tokenType == SilverstripeTokenTypes.SS_BLOCK_END) {
            return SILVERSTRIPE_CLOSED_BLOCK_GROUP;
        }

        return BraceMatchingUtil.UNDEFINED_TOKEN_GROUP;
    }

    @Override
    public boolean isLBraceToken(@NotNull HighlighterIterator iterator, @NotNull CharSequence fileText, @NotNull FileType fileType) {
        IElementType tokenType = iterator.getTokenType();

        if (
            tokenType == SilverstripeTokenTypes.SS_LEFT_BRACE
                || tokenType == SilverstripeTokenTypes.SS_LEFT_PARENTHESIS
                || tokenType == SilverstripeTokenTypes.SS_COMMENT_START
                || tokenType == SilverstripeTokenTypes.SS_TRANSLATION_KEYWORD
        ) {
            return true;
        }

        if (tokenType == SilverstripeTokenTypes.SS_BLOCK_START) {
            TokenSet startOrInlineTokens = TokenSet.create(
                SilverstripeTokenTypes.SS_START_KEYWORD,
                SilverstripeTokenTypes.SS_IF_KEYWORD,
                SilverstripeTokenTypes.SS_CACHED_KEYWORD,
                SilverstripeTokenTypes.SS_BLOCK_NAME,
                SilverstripeTokenTypes.SS_INCLUDE_KEYWORD,
                SilverstripeTokenTypes.SS_SIMPLE_KEYWORD,
                SilverstripeTokenTypes.SS_REQUIRE_KEYWORD,
                SilverstripeTokenTypes.SS_TRANSLATION_KEYWORD
            );

            boolean result = false;
            int count = 0;
            while(true) {
                iterator.advance();
                count++;
                if (iterator.atEnd()) {
                    break;
                }

                tokenType = iterator.getTokenType();
                if (startOrInlineTokens.contains(tokenType)) {
                    result = true;
                    break;
                }

                if (tokenType == SilverstripeTokenTypes.SS_BLOCK_END) {
                    break;
                }
            }
            while(count-- > 0) iterator.retreat();

            return result;
        }

        return false;
    }

    @Override
    public boolean isRBraceToken(@NotNull HighlighterIterator iterator, @NotNull CharSequence fileText, @NotNull FileType fileType) {
        IElementType tokenType = iterator.getTokenType();

        if (
            tokenType == SilverstripeTokenTypes.SS_RIGHT_BRACE
                || tokenType == SilverstripeTokenTypes.SS_RIGHT_PARENTHESIS
                || tokenType == SilverstripeTokenTypes.SS_COMMENT_END
        ) {
            return true;
        }

        if (tokenType == SilverstripeTokenTypes.SS_BLOCK_END) {
            TokenSet endOrInlineTokens = TokenSet.create(
                SilverstripeTokenTypes.SS_END_KEYWORD,
                SilverstripeTokenTypes.SS_BLOCK_NAME,
                SilverstripeTokenTypes.SS_INCLUDE_KEYWORD,
                SilverstripeTokenTypes.SS_SIMPLE_KEYWORD,
                SilverstripeTokenTypes.SS_REQUIRE_KEYWORD,
                SilverstripeTokenTypes.SS_TRANSLATION_KEYWORD
            );

            boolean result = false;
            int count = 0;
            while(true) {
                iterator.retreat();
                count++;
                if (iterator.atEnd()) {
                    break;
                }

                tokenType = iterator.getTokenType();
                if (endOrInlineTokens.contains(tokenType)) {
                    result = true;
                    break;
                }

                if (tokenType == SilverstripeTokenTypes.SS_BLOCK_START) {
                    break;
                }
            }
            while(count-- > 0) iterator.advance();

            return result;
        }

        return false;
    }

    @Override
    public boolean isPairBraces(@NotNull IElementType tokenType1, @NotNull IElementType tokenType2) {
        if (tokenType2.equals(PAIRING_TOKENS.get(tokenType1))) return true;
        List<IElementType> keys = PAIRING_TOKENS.getKeysByValue(tokenType1);
        return keys != null && keys.contains(tokenType2);
    }

    @Override
    public boolean isStructuralBrace(@NotNull HighlighterIterator iterator, @NotNull CharSequence text, @NotNull FileType fileType) {
        return false;
    }

    @Override
    public @Nullable IElementType getOppositeBraceTokenType(@NotNull IElementType type) {
        if (PAIRING_TOKENS.containsKey(type)) {
            return PAIRING_TOKENS.get(type);
        } else if (PAIRING_TOKENS.containsValue(type)) {
            List<IElementType> keys = PAIRING_TOKENS.getKeysByValue(type);
            return keys != null ? keys.get(0) : null;
        }

        return null;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lBraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(@NotNull PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
