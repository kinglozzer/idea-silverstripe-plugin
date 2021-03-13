package com.kinglozzer.silverstripe.inspections;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.kinglozzer.silverstripe.SilverstripeBundle;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;

public class SilverstripeBlockAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        IElementType elementType = element.getNode().getElementType();

        // Warn about unclosed blocks (start block statements without a matching end block statement)
        if (elementType.equals(SilverstripeTokenTypes.SS_CLOSED_BLOCK_START_STATEMENT) || elementType.equals(SilverstripeTokenTypes.SS_IF_STATEMENT)) {
            IElementType parentBlockType = element.getParent().getNode().getElementType();
            if (!parentBlockType.equals(SilverstripeTokenTypes.SS_CLOSED_BLOCK_STATEMENT)) {
                // Try to find out the block name, it could be a loop, with or if
                TokenSet keywords = TokenSet.create(SilverstripeTokenTypes.SS_START_KEYWORD, SilverstripeTokenTypes.SS_IF_KEYWORD);
                ASTNode[] blockTypeTokens = element.getNode().getChildren(keywords);
                String message;
                if (blockTypeTokens.length != 0) {
                    message = SilverstripeBundle.message("ss.inspection.unclosed.closed.block.0",
                        blockTypeTokens[blockTypeTokens.length - 1].getText());
                    holder.newAnnotation(HighlightSeverity.ERROR, message)
                        .range(element)
                        .create();
                }
            }
        }

        // Warn about an if/else_if/with/loop block without a lookup provided
        if (
            elementType.equals(SilverstripeTokenTypes.SS_CLOSED_BLOCK_START_STATEMENT)
                || elementType.equals(SilverstripeTokenTypes.SS_IF_STATEMENT)
                || elementType.equals(SilverstripeTokenTypes.SS_ELSE_IF_STATEMENT)
        ) {
            TokenSet keywords = TokenSet.create(SilverstripeTokenTypes.SS_START_KEYWORD,
                SilverstripeTokenTypes.SS_IF_KEYWORD,
                SilverstripeTokenTypes.SS_ELSE_IF_KEYWORD);
            ASTNode[] blockTypeTokens = element.getNode().getChildren(keywords);
            if (blockTypeTokens.length != 0) {
                ASTNode keyword = blockTypeTokens[blockTypeTokens.length - 1];
                // Grab the next non-whitespace token after the loop/with/if keyword
                PsiElement nextSibling = keyword.getPsi().getNextSibling();
                while (nextSibling != null && nextSibling.getNode().getElementType() == TokenType.WHITE_SPACE) {
                    nextSibling = nextSibling.getNextSibling();
                }

                // If the next token is a %>, no variable has been provided
                if (nextSibling != null && nextSibling.getNode().getElementType().equals(SilverstripeTokenTypes.SS_BLOCK_END)) {
                    String message = SilverstripeBundle.message("ss.inspection.block.missing.variable.0", keyword.getText());
                    holder.newAnnotation(HighlightSeverity.ERROR, message)
                        .range(element)
                        .create();
                }
            }
        }

        // Warn about an end block statement that doesn't have a matching start block statement
        if (elementType.equals(SilverstripeTokenTypes.SS_CLOSED_BLOCK_END_STATEMENT)) {
            PsiElement parent = element.getParent();
            if (!parent.getNode().getElementType().equals(SilverstripeTokenTypes.SS_CLOSED_BLOCK_STATEMENT)) {
                // Try to find out the block name, it could be a loop, with or if
                TokenSet keywords = TokenSet.create(SilverstripeTokenTypes.SS_END_KEYWORD);
                ASTNode[] blockTypeTokens = element.getNode().getChildren(keywords);
                String message;
                if (blockTypeTokens.length != 0) {
                    message = SilverstripeBundle.message("ss.inspection.unexpected.end.statement.0", blockTypeTokens[0].getText());
                } else {
                    message = SilverstripeBundle.message("ss.inspection.unexpected.end.statement");
                }

                holder.newAnnotation(HighlightSeverity.ERROR, message)
                    .range(element)
                    .create();
            }
        }
    }
}
