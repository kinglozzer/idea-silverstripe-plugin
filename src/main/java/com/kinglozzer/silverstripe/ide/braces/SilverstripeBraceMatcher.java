package com.kinglozzer.silverstripe.ide.braces;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SilverstripeBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] pairs = {
        new BracePair(SilverstripeTokenTypes.SS_LEFT_BRACE, SilverstripeTokenTypes.SS_RIGHT_BRACE, false),
        new BracePair(SilverstripeTokenTypes.SS_LEFT_PARENTHESIS, SilverstripeTokenTypes.SS_RIGHT_PARENTHESIS, false),
        new BracePair(SilverstripeTokenTypes.SS_COMMENT_START, SilverstripeTokenTypes.SS_COMMENT_END, false),
        new BracePair(SilverstripeTokenTypes.SS_BLOCK_START, SilverstripeTokenTypes.SS_BLOCK_END, false)
    };

    @Override
    public BracePair @NotNull [] getPairs() {
        return pairs;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
