package com.kinglozzer.silverstripe.psi.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;

public class SilverstripeReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        ElementPattern<PsiElement> pattern = PlatformPatterns.psiElement(SilverstripeTokenTypes.SS_INCLUDE_STATEMENT)
            .withLanguage(SilverstripeLanguage.INSTANCE);
        registrar.registerReferenceProvider(pattern, new SilverstripeIncludeReferenceProvider());
    }
}
