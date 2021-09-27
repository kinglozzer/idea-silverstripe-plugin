package com.kinglozzer.silverstripe.inspections;

import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.kinglozzer.silverstripe.SilverstripeBundle;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import com.kinglozzer.silverstripe.psi.SilverstripeLookupStep;
import org.jetbrains.annotations.NotNull;

public class SilverstripeLookupAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof SilverstripeLookupStep) {
            String varValue = element.getText();
            if (varValue != null && !varValue.startsWith("$")) {
                // We only want to warn if this is the first step in the lookup chain
                if (element.getPrevSibling() == null) {
                    PsiElement lookup = element.getParent();
                    if (lookup == null) {
                        return;
                    }

                    // If this is the first lookup inside a require statement, ignore it - "css" in <% require css() %>
                    // is treated as a non-dollar-prefixed lookup
                    PsiElement context = lookup.getParent();
                    if (context != null
                        && context.getNode() != null
                        && context.getNode().getElementType() == SilverstripeTokenTypes.SS_REQUIRE_STATEMENT
                    ) {
                        return;
                    }

                    AnnotationBuilder builder = holder.newAnnotation(HighlightSeverity.WARNING,
                        SilverstripeBundle.message("ss.inspection.unquoted.string"))
                        .withFix(new AddDollarSignFix());

                    boolean hasArgs = false;
                    PsiElement[] children = element.getChildren();
                    for (PsiElement child : children) {
                        if (child.getNode().getElementType() == SilverstripeTokenTypes.SS_LOOKUP_STEP_ARGS) {
                            hasArgs = true;
                            break;
                        }
                    }

                    if (!hasArgs) {
                        builder = builder.withFix(new QuoteStringFix());
                    }

                    builder.create();
                }
            }
        }
    }
}
