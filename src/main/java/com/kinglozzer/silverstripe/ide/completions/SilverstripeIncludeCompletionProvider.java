package com.kinglozzer.silverstripe.ide.completions;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.kinglozzer.silverstripe.SilverstripeFileType;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;
import com.kinglozzer.silverstripe.util.SilverstripeFileUtil;
import com.kinglozzer.silverstripe.util.SilverstripeVersionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SilverstripeIncludeCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
        Project project = parameters.getOriginalFile().getProject();
        List<SilverstripePsiFile> files = SilverstripeFileUtil.findValidTemplates(project);
        for (SilverstripePsiFile file : files) {
            String templateName;
            String url = file.getVirtualFile().getUrl();
            if (SilverstripeVersionUtil.isSilverstripe4OrMore(project)) {
                String[] templatePathFragments = url.split("/templates/", 2);
                if (templatePathFragments.length < 2) {
                    continue;
                }
                // todo - this is a bit opinionated. Should be configurable: single/double backslash or forward slash
                templateName = templatePathFragments[1].replaceAll("/", "\\\\");
            } else {
                templateName = file.getName();
            }

            // Strip off '.' and file extension
            templateName = templateName.substring(0, templateName.length() - 1 - SilverstripeFileType.DEFAULT_EXTENSION.length());
            LookupElementBuilder element = LookupElementBuilder.create(templateName);

            // Templates not in an "includes" directory might not be actual includes, so flag that in the list
            if (url.toLowerCase().contains("/includes/")) {
                element = element.withTypeText("Include", SilverstripeFileType.FILE_ICON, false);
            } else {
                element = element.withTypeText("Template", SilverstripeFileType.FILE_ICON, true);
            }

            result.addElement(element);
        }
    }
}
