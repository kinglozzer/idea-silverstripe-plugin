package com.kinglozzer.silverstripe.inspections;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.kinglozzer.silverstripe.SilverstripeBundle;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SilverstripeTranslationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        ASTNode node = element.getNode();
        if (node.getElementType().equals(SilverstripeTokenTypes.SS_TRANSLATION_STATEMENT)) {
            ASTNode idNode = node.findChildByType(SilverstripeTokenTypes.SS_TRANSLATION_IDENTIFIER);
            if (idNode == null) {
                holder.newAnnotation(HighlightSeverity.ERROR, SilverstripeBundle.message("ss.inspection.translation.missing.identifier"))
                    .range(element)
                    .create();
            }

            // We can assume the first string in the tag is the translation string, as the identifier is parsed separately
            ASTNode[] strings = node.getChildren(TokenSet.create(SilverstripeTokenTypes.SS_STRING));
            if (strings.length != 0) {
                // Build a list of all values that have been provided for placeholders
                ASTNode[] valueNodes = node.getChildren(TokenSet.create(SilverstripeTokenTypes.SS_NAMED_ARGUMENT_NAME));
                ArrayList<String> values = new ArrayList<>();
                for (ASTNode valueNode : valueNodes) {
                    values.add(valueNode.getText());
                }

                // Regex out a list of placeholders from the translation string ("foo {placeholder} bar")
                ASTNode string = strings[0];
                Pattern pattern = Pattern.compile("\\{([^{}]+)}");
                Matcher matcher = pattern.matcher(string.getText());
                while (matcher.find()) {
                    String placeholder = matcher.group(1);
                    if (!values.contains(placeholder)) {
                        int start = string.getStartOffset() + matcher.start();
                        int end = string.getStartOffset() + matcher.end();
                        holder.newAnnotation(HighlightSeverity.ERROR,
                            SilverstripeBundle.message("ss.inspection.translation.missing.placeholder.value", placeholder))
                            .range(new TextRange(start, end))
                            .create();
                    }
                }
            }
        }
    }
}
