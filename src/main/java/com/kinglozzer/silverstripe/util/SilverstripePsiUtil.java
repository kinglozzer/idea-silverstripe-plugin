package com.kinglozzer.silverstripe.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.kinglozzer.silverstripe.SilverstripeFileType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;
import org.jetbrains.annotations.NotNull;

public class SilverstripePsiUtil {
    /**
     * Tests to see if the given element is not the "root" statements expression of the grammar
     *
     * @param element The element to check
     * @return True if the element is not the root SS_STATEMENTS element
     */
    public static boolean isNonRootStatementsElement(PsiElement element) {
        PsiElement statementsParent = PsiTreeUtil.findFirstParent(element, true, element1 -> element1.getNode() != null
            && element1.getNode().getElementType() == SilverstripeTokenTypes.SS_STATEMENTS);

        // If the element is an SS_STATEMENTS element and it has an SS_STATEMENTS ancestor, it isn't the root element
        return element.getNode().getElementType() == SilverstripeTokenTypes.SS_STATEMENTS && statementsParent != null;
    }

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
