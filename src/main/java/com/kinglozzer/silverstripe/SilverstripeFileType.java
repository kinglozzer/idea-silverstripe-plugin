package com.kinglozzer.silverstripe;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.TemplateLanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SilverstripeFileType extends LanguageFileType implements TemplateLanguageFileType {
    public static final LanguageFileType INSTANCE = new SilverstripeFileType();
    public static final String DEFAULT_EXTENSION = "ss";
    public static final Icon FILE_ICON = IconLoader.getIcon("/icons/silverstripe.svg", SilverstripeFileType.class);

    private SilverstripeFileType() {
        super(SilverstripeLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Silverstripe";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Silverstripe template file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return FILE_ICON;
    }
}
