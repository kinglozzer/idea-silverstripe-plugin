package com.kinglozzer.silverstripe.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.kinglozzer.silverstripe.psi.SilverstripePsiElement;
import org.jetbrains.annotations.NotNull;

public class SilverstripePsiElementImpl extends ASTWrapperPsiElement implements SilverstripePsiElement {
    public SilverstripePsiElementImpl(@NotNull ASTNode astNode) {
        super(astNode);
    }
}
