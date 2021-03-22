package com.kinglozzer.silverstripe.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.kinglozzer.silverstripe.SilverstripeFileType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;
import org.jetbrains.annotations.NotNull;

public class SilverstripePsiUtil {
    public static SilverstripePsiFile createDummyFile(Project project, String text) {
        final String fileName = "dummy." + SilverstripeFileType.INSTANCE.getDefaultExtension();
        return (SilverstripePsiFile) PsiFileFactory.getInstance(project).createFileFromText(fileName, SilverstripeLanguage.INSTANCE, text);
    }

    @NotNull
    public static PsiElement createIncludeFileElement(final String text, final Project project) {
        final SilverstripePsiFile dummyFile = createDummyFile(project, "<% include " + text + "%>");
        final PsiElement statements = dummyFile.getFirstChild();
        assert statements != null;
        final PsiElement tag = statements.getFirstChild();
        assert tag != null;
        ASTNode includeNameElement = tag.getNode().findChildByType(SilverstripeTokenTypes.SS_INCLUDE_FILE);
        assert includeNameElement != null;
        return includeNameElement.getPsi();
    }
}
