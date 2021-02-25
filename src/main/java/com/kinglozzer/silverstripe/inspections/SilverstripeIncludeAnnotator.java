package com.kinglozzer.silverstripe.inspections;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.psi.PsiElement;
import com.kinglozzer.silverstripe.SilverstripeBundle;
import com.kinglozzer.silverstripe.SilverstripeFileType;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;
import com.kinglozzer.silverstripe.psi.impl.SilverstripeIncludeImpl;
import com.kinglozzer.silverstripe.util.SilverstripeFileUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SilverstripeIncludeAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof SilverstripeIncludeImpl) {
            ASTNode includeNameElement = element.getNode().findChildByType(SilverstripeTokenTypes.SS_INCLUDE_FILE);
            if (includeNameElement != null) {
                String fileName = includeNameElement.getText()
                    .replaceAll("\\\\", "/") // Replace \ with /
                    .replaceAll("^/", "");    // Remove leading /
                fileName = fileName + "." + SilverstripeFileType.DEFAULT_EXTENSION;

                List<SilverstripePsiFile> files = SilverstripeFileUtil.findIncludeTemplate(element.getProject(), fileName);
                if (files.size() == 0) {
                    holder.newAnnotation(HighlightSeverity.WARNING,
                        SilverstripeBundle.message("ss.inspection.include.file.not.found", fileName))
                        .range(includeNameElement.getTextRange())
                        .textAttributes(CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES)
                        .create();
                }
            }
        }
    }
}
