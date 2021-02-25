package com.kinglozzer.silverstripe.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.kinglozzer.silverstripe.psi.impl.SilverstripeIncludeImpl;
import org.jetbrains.annotations.NotNull;

public class SilverstripeIncludeReferenceProvider extends PsiReferenceProvider {
    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        SilverstripeIncludeImpl includeElement = (SilverstripeIncludeImpl) element;
        return new PsiReference[]{new SilverstripeIncludeReference(includeElement, includeElement.getReferenceTextRange())};
    }
}
