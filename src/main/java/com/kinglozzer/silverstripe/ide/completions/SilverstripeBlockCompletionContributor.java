package com.kinglozzer.silverstripe.ide.completions;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class SilverstripeBlockCompletionContributor extends CompletionContributor {
    public SilverstripeBlockCompletionContributor() {
        extend(CompletionType.BASIC,
            psiElement(SilverstripeTokenTypes.SS_UNFINISHED_BLOCK_STATEMENT),
            new SilverstripeBlockCompletionProvider());

        extend(CompletionType.BASIC,
            psiElement(SilverstripeTokenTypes.SS_BAD_BLOCK_STATEMENT),
            new SilverstripeBlockCompletionProvider());
    }
}
