package com.kinglozzer.silverstripe.ide.actions;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.TokenSet;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;
import org.jetbrains.annotations.NotNull;

public class SilverstripeEnterHandler extends EnterHandlerDelegateAdapter {
    private static final TokenSet OPEN_BLOCK_STATEMENTS = TokenSet.create(
        SilverstripeTokenTypes.SS_IF_STATEMENT,
        SilverstripeTokenTypes.SS_ELSE_IF_STATEMENT,
        SilverstripeTokenTypes.SS_ELSE_STATEMENT,
        SilverstripeTokenTypes.SS_BLOCK_START_STATEMENT,
        SilverstripeTokenTypes.SS_BLOCK_END_STATEMENT
    );

    private static boolean isBetweenSilverstripeBlocks(Editor editor, PsiFile file, int offset) {
        if (offset < 1) {
            return false;
        }

        PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());

        PsiElement elementAtCaret = file.getViewProvider().findElementAt(offset - 1, SilverstripeLanguage.class);
        if (elementAtCaret == null || elementAtCaret.getNode().getElementType() != SilverstripeTokenTypes.SS_BLOCK_END) {
            return false;
        }

        PsiElement parentElement = elementAtCaret.getParent();
        if (parentElement == null || !OPEN_BLOCK_STATEMENTS.contains(parentElement.getNode().getElementType())) {
            return false;
        }

        PsiElement nextElement = parentElement.getNextSibling();
        if (nextElement == null) {
            return false;
        }

        return OPEN_BLOCK_STATEMENTS.contains(nextElement.getNode().getElementType());
    }

    @Override
    public Result preprocessEnter(@NotNull final PsiFile file,
                                  @NotNull final Editor editor,
                                  @NotNull final Ref<Integer> caretOffset,
                                  @NotNull final Ref<Integer> caretAdvance,
                                  @NotNull final DataContext dataContext,
                                  final EditorActionHandler originalHandler) {
        // If we're between opening/closing blocks when the user pressed enter, auto-indent the caret
        if (file instanceof SilverstripePsiFile && isBetweenSilverstripeBlocks(editor, file, caretOffset.get())) {
            originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);
            return Result.Default;
        }
        return Result.Continue;
    }
}
