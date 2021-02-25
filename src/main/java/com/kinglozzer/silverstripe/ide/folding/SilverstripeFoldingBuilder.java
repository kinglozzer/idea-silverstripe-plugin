package com.kinglozzer.silverstripe.ide.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.JBIterable;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SilverstripeFoldingBuilder implements FoldingBuilder, DumbAware {
    private static boolean isSingleLine(PsiElement element, Document document) {
        TextRange range = element.getTextRange();
        return document.getLineNumber(range.getStartOffset()) == document.getLineNumber(range.getEndOffset());
    }

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        appendDescriptors(node.getPsi(), descriptors, document);
        int size = descriptors.size();
        return descriptors.toArray(new FoldingDescriptor[size]);
    }

    private void appendDescriptors(PsiElement psi, List<FoldingDescriptor> descriptors, Document document) {
        if (isSingleLine(psi, document)) {
            return;
        }

        ASTNode node = psi.getNode();
        IElementType type = node.getElementType();
        if (type == SilverstripeTokenTypes.SS_BLOCK_STATEMENT || type == SilverstripeTokenTypes.SS_COMMENT_STATEMENT) {
            ASTNode startNode = node.getFirstChildNode();
            ASTNode endNode = node.getLastChildNode();
            if (startNode == null || endNode == null) {
                return;
            }

            int startOffset = startNode.getTextRange().getEndOffset();
            int endOffset = endNode.getTextRange().getStartOffset();

            JBIterable<PsiElement> elseIfs = SyntaxTraverser.psiTraverser().children(psi).filter(element -> {
                ASTNode elementNode = element.getNode();
                if (elementNode == null) {
                    return false;
                }

                IElementType elType = elementNode.getElementType();
                return elType == SilverstripeTokenTypes.SS_ELSE_IF_STATEMENT || elType == SilverstripeTokenTypes.SS_ELSE_STATEMENT;
            });
            for (PsiElement elseIf : elseIfs) {
                node = elseIf.getNode();
                ASTNode firstChild = node.getFirstChildNode();
                ASTNode lastChild = node.getLastChildNode();
                if (firstChild == null || lastChild == null) {
                    continue;
                }

                int thisStartOffset = firstChild.getTextRange().getStartOffset();
                int thisEndOffset = lastChild.getTextRange().getEndOffset();
                if (thisStartOffset > startOffset && thisEndOffset < endOffset) {
                    TextRange range = new TextRange(startOffset, thisStartOffset);
                    descriptors.add(new FoldingDescriptor(node, range));

                    startOffset = thisEndOffset;
                }
            }

            TextRange range = new TextRange(startOffset, endOffset);
            descriptors.add(new FoldingDescriptor(node, range));
        }

        PsiElement child = psi.getFirstChild();
        while (child != null) {
            appendDescriptors(child, descriptors, document);
            child = child.getNextSibling();
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
