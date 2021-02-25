package com.kinglozzer.silverstripe.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.kinglozzer.silverstripe.psi.SilverstripeLookupStep;
import org.jetbrains.annotations.NotNull;

public class SilverstripeLookupStepImpl extends ASTWrapperPsiElement implements SilverstripeLookupStep {
    public SilverstripeLookupStepImpl(@NotNull ASTNode node) {
        super(node);
    }
}
