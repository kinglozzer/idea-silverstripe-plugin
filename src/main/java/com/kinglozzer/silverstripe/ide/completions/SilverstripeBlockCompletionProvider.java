package com.kinglozzer.silverstripe.ide.completions;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class SilverstripeBlockCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
        result.addElement(LookupElementBuilder.create("if"));
        result.addElement(LookupElementBuilder.create("else_if"));
        result.addElement(LookupElementBuilder.create("end_if"));
        result.addElement(LookupElementBuilder.create("loop"));
        result.addElement(LookupElementBuilder.create("end_loop"));
        result.addElement(LookupElementBuilder.create("with"));
        result.addElement(LookupElementBuilder.create("end_with"));
        result.addElement(LookupElementBuilder.create("include"));
        result.addElement(LookupElementBuilder.create("require"));
        result.addElement(LookupElementBuilder.create("cached"));
        result.addElement(LookupElementBuilder.create("end_cached"));
    }
}
