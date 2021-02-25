package com.kinglozzer.silverstripe.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;

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
}
