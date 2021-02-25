package com.kinglozzer.silverstripe;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class CreateSilverstripeTemplateAction extends CreateFileFromTemplateAction {
    private final String name = SilverstripeBundle.message("ss.action.create.template.text");

    public CreateSilverstripeTemplateAction() {
        super(SilverstripeBundle.message("ss.action.create.template.text"),
            SilverstripeBundle.message("ss.action.create.template.description"),
            IconLoader.getIcon("/icons/silverstripe.svg", SilverstripeLanguage.class));
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return SilverstripeBundle.message("ss.action.create.template.name", name, newName);
    }

    @Override
    protected void buildDialog(@NotNull Project project,
                               @NotNull PsiDirectory directory,
                               @NotNull CreateFileFromTemplateDialog.Builder builder) {
        builder
            .setTitle(SilverstripeBundle.message("ss.action.create.template.dialog.title", name))
            .addKind(name, IconLoader.getIcon("/icons/silverstripe.svg", SilverstripeLanguage.class), "Silverstripe Template");
    }
}
