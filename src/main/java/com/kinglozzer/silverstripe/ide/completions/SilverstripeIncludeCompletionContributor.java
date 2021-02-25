package com.kinglozzer.silverstripe.ide.completions;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class SilverstripeIncludeCompletionContributor extends CompletionContributor {
    public SilverstripeIncludeCompletionContributor() {
        extend(CompletionType.BASIC,
            psiElement(SilverstripeTokenTypes.SS_INCLUDE_FILE),
            new SilverstripeIncludeCompletionProvider());
    }
}
