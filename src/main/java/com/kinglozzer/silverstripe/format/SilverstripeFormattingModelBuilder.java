package com.kinglozzer.silverstripe.format;

import com.intellij.formatting.*;
import com.intellij.formatting.templateLanguages.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.DocumentBasedFormattingModel;
import com.intellij.psi.formatter.FormattingDocumentModelImpl;
import com.intellij.psi.formatter.xml.HtmlPolicy;
import com.intellij.psi.formatter.xml.SyntheticBlock;
import com.intellij.psi.templateLanguages.SimpleTemplateLanguageFormattingModelBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTag;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import com.kinglozzer.silverstripe.util.SilverstripePsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SilverstripeFormattingModelBuilder extends TemplateLanguageFormattingModelBuilder {
    @Override
    public TemplateLanguageBlock createTemplateLanguageBlock(@NotNull ASTNode node,
                                                             @Nullable Wrap wrap,
                                                             @Nullable Alignment alignment,
                                                             @Nullable List<DataLanguageBlockWrapper> foreignChildren,
                                                             @NotNull CodeStyleSettings codeStyleSettings) {
        final FormattingDocumentModelImpl documentModel = FormattingDocumentModelImpl.createOn(node.getPsi().getContainingFile());
        HtmlPolicy policy = new HtmlPolicy(codeStyleSettings, documentModel);
        return new SilverstripeBlock(node, wrap, alignment, this, codeStyleSettings, foreignChildren, policy);
    }

    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        ASTNode node = formattingContext.getNode();

        if (node.getElementType() == SilverstripeTokenTypes.SS_FRAGMENT) {
            return new SimpleTemplateLanguageFormattingModelBuilder().createModel(formattingContext);
        }

        final PsiFile file = formattingContext.getContainingFile();
        return new DocumentBasedFormattingModel(
            getRootBlock(file, file.getViewProvider(), formattingContext.getCodeStyleSettings()),
            formattingContext.getProject(),
            formattingContext.getCodeStyleSettings(),
            file.getFileType(),
            file
        );
    }

    @Override
    public boolean dontFormatMyModel() {
        return false;
    }

    /**
     * todo - this is copied near-verbatim from Handlebars, it could do with some love...
     */
    private static class SilverstripeBlock extends TemplateLanguageBlock {
        @NotNull
        protected final HtmlPolicy myHtmlPolicy;

        SilverstripeBlock(@NotNull ASTNode node,
                          Wrap wrap,
                          Alignment alignment,
                          @NotNull TemplateLanguageBlockFactory blockFactory,
                          @NotNull CodeStyleSettings settings,
                          @Nullable List<DataLanguageBlockWrapper> foreignChildren,
                          @NotNull HtmlPolicy htmlPolicy) {
            super(node, wrap, alignment, blockFactory, settings, foreignChildren);
            myHtmlPolicy = htmlPolicy;
        }

        @Override
        public Indent getIndent() {
            if (myNode.getText().trim().length() == 0) {
                return Indent.getNoneIndent();
            }

            if (myNode.getElementType().equals(SilverstripeTokenTypes.SS_NESTED_STATEMENTS)) {
                DataLanguageBlockWrapper foreignBlockParent = getForeignBlockParent(false);
                if (foreignBlockParent == null) {
                    return Indent.getNormalIndent();
                }

                if (foreignBlockParent.getNode() instanceof XmlTag) {
                    XmlTag xmlTag = (XmlTag) foreignBlockParent.getNode();
                    if (!myHtmlPolicy.indentChildrenOf(xmlTag)) {
                        return Indent.getNormalIndent();
                    }
                }

                return Indent.getNoneIndent();
            }

            if (myNode.getTreeParent() != null && myNode.getTreeParent().getElementType().equals(SilverstripeTokenTypes.SS_NESTED_STATEMENTS)) {
                if (getParent() instanceof SilverstripeBlock && ((SilverstripeBlock) getParent()).getIndent() == Indent.getNoneIndent()) {
                    return Indent.getNormalIndent();
                }
            }

            DataLanguageBlockWrapper foreignParent = getForeignBlockParent(true);
            if (foreignParent != null) {
                if (foreignParent.getNode() instanceof XmlTag && !myHtmlPolicy.indentChildrenOf((XmlTag) foreignParent.getNode())) {
                    return Indent.getNoneIndent();
                }
                return Indent.getNormalIndent();
            }

            return Indent.getNoneIndent();
        }

        @Override
        protected IElementType getTemplateTextElementType() {
            return SilverstripeTokenTypes.SS_TEXT;
        }

        @Override
        public boolean isRequiredRange(TextRange range) {
            return false;
        }

        @NotNull
        @Override
        public ChildAttributes getChildAttributes(int newChildIndex) {
            if (
                myNode.getElementType() == SilverstripeTokenTypes.SS_BLOCK_STATEMENT
                    || (
                    getParent() instanceof DataLanguageBlockWrapper
                        && (
                        (myNode.getElementType() != SilverstripeTokenTypes.SS_STATEMENTS && myNode.getElementType() != SilverstripeTokenTypes.SS_NESTED_STATEMENTS)
                            || newChildIndex != 0
                            || myNode.getTreeNext() instanceof PsiErrorElement
                    )
                )
            ) {
                return new ChildAttributes(Indent.getNormalIndent(), null);
            }

            return new ChildAttributes(Indent.getNoneIndent(), null);
        }

        private DataLanguageBlockWrapper getForeignBlockParent(boolean immediate) {
            DataLanguageBlockWrapper foreignBlockParent = null;
            BlockWithParent parent = getParent();

            while (parent != null) {
                if (parent instanceof DataLanguageBlockWrapper && !(((DataLanguageBlockWrapper) parent).getOriginal() instanceof SyntheticBlock)) {
                    foreignBlockParent = (DataLanguageBlockWrapper) parent;
                    break;
                } else if (immediate && parent instanceof SilverstripeBlock) {
                    break;
                }
                parent = parent.getParent();
            }

            return foreignBlockParent;
        }
    }
}
