package com.kinglozzer.silverstripe.ide.templates;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTokenType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.ide.highlighting.SilverstripeSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SilverstripeContextType extends TemplateContextType {
    protected SilverstripeContextType() {
        super("Silverstripe", "Silverstripe");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        return SilverstripeLanguage.INSTANCE.is(file.getLanguage());
    }

    @Nullable
    @Override
    public SyntaxHighlighter createHighlighter() {
        return new SilverstripeSyntaxHighlighter();
    }
}
