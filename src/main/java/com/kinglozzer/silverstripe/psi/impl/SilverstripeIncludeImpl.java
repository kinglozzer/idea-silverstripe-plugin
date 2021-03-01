package com.kinglozzer.silverstripe.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.IncorrectOperationException;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import com.kinglozzer.silverstripe.util.SilverstripePsiUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SilverstripeIncludeImpl extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
    public SilverstripeIncludeImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        ASTNode includeFileNode = getNode().findChildByType(SilverstripeTokenTypes.SS_INCLUDE_FILE);
        if (includeFileNode == null) {
            return null;
        }

        return includeFileNode.getPsi();
    }

    @NotNull
    public String getName() {
        ASTNode includeFileNode = getNode().findChildByType(SilverstripeTokenTypes.SS_INCLUDE_FILE);
        if (includeFileNode != null) {
            return includeFileNode.getText();
        }

        return "";
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        ASTNode includeFileNode = getNode().findChildByType(SilverstripeTokenTypes.SS_INCLUDE_FILE);
        if (includeFileNode != null) {
            final PsiElement newElement = SilverstripePsiUtil.createIncludeFileElement(name, getProject());
            includeFileNode.getPsi().replace(newElement);
        }

        return this;
    }

    @Nullable
    public TextRange getReferenceTextRange() {
        return new TextRange(0, this.getTextLength());
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }

    @Override
    public PsiReference getReference() {
        PsiReference[] references = getReferences();
        return references.length == 0 ? null : references[0];
    }

    public ASTNode getIncludeFileNode() {
        return getNode().findChildByType(SilverstripeTokenTypes.SS_INCLUDE_FILE);
    }
}
