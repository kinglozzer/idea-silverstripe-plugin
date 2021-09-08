package com.kinglozzer.silverstripe.ide.templates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.ide.highlighting.SilverstripeSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SilverstripeContextType extends TemplateContextType {
    protected SilverstripeContextType() {
        super("Silverstripe", "Silverstripe");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        return SilverstripeLanguage.INSTANCE.is(templateActionContext.getFile().getLanguage());
    }

    @Nullable
    @Override
    public SyntaxHighlighter createHighlighter() {
        return new SilverstripeSyntaxHighlighter();
    }
}
