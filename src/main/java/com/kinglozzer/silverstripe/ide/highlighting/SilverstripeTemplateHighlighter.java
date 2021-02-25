package com.kinglozzer.silverstripe.ide.highlighting;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SilverstripeTemplateHighlighter extends LayeredLexerEditorHighlighter {
    public SilverstripeTemplateHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
        super(new SilverstripeSyntaxHighlighter(), colors);

        FileType type = null;
        if (project == null || virtualFile == null) {
            type = FileTypes.PLAIN_TEXT;
        } else {
            Language language = TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);
            if (language != null) type = language.getAssociatedFileType();
            if (type == null) type = SilverstripeLanguage.getDefaultTemplateLang();
        }

        SyntaxHighlighter outerHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(type, project, virtualFile);
        if (outerHighlighter != null) {
            registerLayer(SilverstripeTokenTypes.SS_TEXT, new LayerDescriptor(outerHighlighter, ""));
        }
    }
}
