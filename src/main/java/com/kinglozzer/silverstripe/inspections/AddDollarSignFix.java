package com.kinglozzer.silverstripe.inspections;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kinglozzer.silverstripe.SilverstripeBundle;
import org.jetbrains.annotations.NotNull;

public class AddDollarSignFix extends BaseIntentionAction {
    @NotNull
    @Override
    public String getText() {
        return SilverstripeBundle.message("ss.inspection.add.dollar.sign.fix");
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return SilverstripeBundle.message("ss.inspection.group");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) {
        final Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        final int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = file.findElementAt(offset);
        if (document == null || psiElement == null) {
            return;
        }

        document.insertString(psiElement.getTextRange().getStartOffset(), "$");
    }
}
