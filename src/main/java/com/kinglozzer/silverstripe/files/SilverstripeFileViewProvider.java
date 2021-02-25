package com.kinglozzer.silverstripe.files;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutors;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.ConfigurableTemplateLanguageFileViewProvider;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.intellij.psi.tree.IElementType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SilverstripeFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider
    implements ConfigurableTemplateLanguageFileViewProvider {

    private static final ConcurrentMap<String, TemplateDataElementType> TEMPLATE_DATA_TO_LANG = new ConcurrentHashMap<>();
    private final Language myBaseLanguage;
    private final Language myTemplateLanguage;

    public SilverstripeFileViewProvider(PsiManager manager, VirtualFile file, boolean physical, Language baseLanguage) {
        this(manager, file, physical, baseLanguage, getTemplateDataLanguage(manager, file));
    }

    public SilverstripeFileViewProvider(PsiManager manager, VirtualFile file, boolean physical, Language baseLanguage, Language templateLanguage) {
        super(manager, file, physical);
        myBaseLanguage = baseLanguage;
        myTemplateLanguage = templateLanguage;
    }

    private static TemplateDataElementType getTemplateDataElementType(Language lang) {
        TemplateDataElementType result = TEMPLATE_DATA_TO_LANG.get(lang.getID());
        if (result != null) {
            return result;
        }

        TemplateDataElementType created = new TemplateDataElementType("SS_TEMPLATE_DATA",
            lang, SilverstripeTokenTypes.SS_TEXT, SilverstripeTokenTypes.SS_FRAGMENT);
        TemplateDataElementType prevValue = TEMPLATE_DATA_TO_LANG.putIfAbsent(lang.getID(), created);

        return prevValue == null ? created : prevValue;
    }

    private static @NotNull Language getTemplateDataLanguage(PsiManager manager, VirtualFile file) {
        Language dataLang = TemplateDataLanguageMappings.getInstance(manager.getProject()).getMapping(file);
        if (dataLang == null) {
            dataLang = SilverstripeLanguage.getDefaultTemplateLang().getLanguage();
        }

        Language substituteLang = LanguageSubstitutors.getInstance().substituteLanguage(dataLang, file, manager.getProject());
        if (TemplateDataLanguageMappings.getTemplateableLanguages().contains(substituteLang)) {
            dataLang = substituteLang;
        }

        return dataLang;
    }

    @Override
    public boolean supportsIncrementalReparse(@NotNull Language rootLanguage) {
        return false;
    }

    @Override
    public @NotNull Language getBaseLanguage() {
        return myBaseLanguage;
    }

    @Override
    public @NotNull Language getTemplateDataLanguage() {
        return myTemplateLanguage;
    }

    @Override
    public @NotNull Set<Language> getLanguages() {
        return Set.of(myBaseLanguage, getTemplateDataLanguage());
    }

    @Override
    protected @NotNull MultiplePsiFilesPerDocumentFileViewProvider cloneInner(@NotNull VirtualFile virtualFile) {
        return new SilverstripeFileViewProvider(getManager(), virtualFile, false, myBaseLanguage, myTemplateLanguage);
    }

    @Override
    protected PsiFile createFile(@NotNull Language lang) {
        ParserDefinition parserDefinition = getDefinition(lang);
        if (parserDefinition == null) {
            return null;
        }

        if (lang.is(getTemplateDataLanguage())) {
            PsiFile file = parserDefinition.createFile(this);
            IElementType type = getContentElementType(lang);
            if (type != null) {
                ((PsiFileImpl) file).setContentElementType(type);
            }
            return file;
        } else if (lang.isKindOf(getBaseLanguage())) {
            return parserDefinition.createFile(this);
        }

        return null;
    }

    @Override
    public @Nullable IElementType getContentElementType(@NotNull Language language) {
        if (language.is(getTemplateDataLanguage())) {
            return getTemplateDataElementType(getBaseLanguage());
        }
        return null;
    }

    private ParserDefinition getDefinition(Language lang) {
        if (lang.isKindOf(getBaseLanguage())) {
            return LanguageParserDefinitions.INSTANCE.forLanguage(lang.is(getBaseLanguage()) ? lang : getBaseLanguage());
        }
        return LanguageParserDefinitions.INSTANCE.forLanguage(lang);
    }
}
