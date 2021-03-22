package com.kinglozzer.silverstripe.ide.actions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;

public class SilverstripeTypedHandler extends TypedHandlerDelegate {
    private static final TokenSet OPEN_BLOCK_START_STATEMENTS = TokenSet.create(
        SilverstripeTokenTypes.SS_IF_STATEMENT,
        SilverstripeTokenTypes.SS_CLOSED_BLOCK_START_STATEMENT
    );

    /**
     * Auto-inserts closing tags. E.g. <% if $Foo %> will automatically append <% end_if %>
     */
    private static void autoInsertCloseTag(Project project, int offset, Editor editor, FileViewProvider provider) {
        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

        PsiElement elementAtCaret = provider.findElementAt(offset - 1, SilverstripeLanguage.class);
        if (elementAtCaret == null || elementAtCaret.getNode().getElementType() != SilverstripeTokenTypes.SS_BLOCK_END) {
            return;
        }

        PsiElement openTag = elementAtCaret.getParent();
        if (openTag != null && OPEN_BLOCK_START_STATEMENTS.contains(openTag.getNode().getElementType())) {
            TokenSet tagType = TokenSet.create(SilverstripeTokenTypes.SS_START_KEYWORD, SilverstripeTokenTypes.SS_IF_KEYWORD);
            ASTNode targetNode = openTag.getNode().findChildByType(tagType);
            if (targetNode != null) {
                editor.getDocument().insertString(offset, "<% end_" + targetNode.getText() + " %>");
            }
        }
    }

    /**
     * Attempts to auto-adjust indentation for blocks to match their "start" block statement
     */
    private static void adjustIndentation(Project project, int offset, Editor editor, PsiFile file, FileViewProvider provider) {
        PsiElement elementAtCaret = provider.findElementAt(offset - 1, SilverstripeLanguage.class);
        PsiElement closeOrSimpleInverseParent = PsiTreeUtil.findFirstParent(elementAtCaret, true, element -> {
            if (element == null || element.getNode() == null) return false;
            IElementType nodeType = element.getNode().getElementType();
            return (nodeType == SilverstripeTokenTypes.SS_ELSE_IF_STATEMENT
                || nodeType == SilverstripeTokenTypes.SS_ELSE_STATEMENT
                || nodeType == SilverstripeTokenTypes.SS_CLOSED_BLOCK_END_STATEMENT);
        });

        // If the user just completed typing a closing / elseif / else block statement auto-adjust indentation for that line
        if (closeOrSimpleInverseParent != null) {
            PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            CaretModel caretModel = editor.getCaretModel();
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
            codeStyleManager.adjustLineIndent(file, editor.getDocument().getLineStartOffset(caretModel.getLogicalPosition().line));
        }
    }

    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (!file.getViewProvider().hasLanguage(SilverstripeLanguage.INSTANCE)) {
            return Result.CONTINUE;
        }

        if (c == '>') {
            int offset = editor.getCaretModel().getOffset();
            if (offset >= 2) {
                String previousChar = editor.getDocument().getText(new TextRange(offset - 2, offset - 1));
                // If the last two characters were "%>", attempt to auto-insert a matching closing block
                if (previousChar.equals("%")) {
                    autoInsertCloseTag(project, offset, editor, file.getViewProvider());
                    adjustIndentation(project, offset, editor, file, file.getViewProvider());
                }
            }
        }

        if (c == '$') {
            int offset = editor.getCaretModel().getOffset();
            if (offset >= 2) {
                String previousChar = editor.getDocument().getText(new TextRange(offset - 2, offset - 1));
                // If the last two characters were "{$", we can close the braces
                if (previousChar.equals("{")) {
                    String nextChar = "";
                    if (editor.getDocument().getTextLength() >= offset + 1) {
                        nextChar = editor.getDocument().getText(new TextRange(offset, offset + 1));
                    }

                    if (!nextChar.equals("}")) {
                        editor.getDocument().insertString(offset, "}");
                    }
                }
            }
        }

        return Result.CONTINUE;
    }
}
