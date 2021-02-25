package com.kinglozzer.silverstripe;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.lang.InjectableLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.templateLanguages.TemplateLanguage;

public class SilverstripeLanguage extends Language implements TemplateLanguage, InjectableLanguage {
    public static final SilverstripeLanguage INSTANCE = new SilverstripeLanguage();

    private SilverstripeLanguage() {
        super("Silverstripe");
    }

    public static LanguageFileType getDefaultTemplateLang() {
        return HtmlFileType.INSTANCE;
    }
}
