package com.kinglozzer.silverstripe.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.impl.PsiFileEx;
import com.kinglozzer.silverstripe.SilverstripeFileType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import org.jetbrains.annotations.NotNull;

public class SilverstripePsiFile extends PsiFileBase implements PsiFileEx {
    public SilverstripePsiFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SilverstripeLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SilverstripeFileType.INSTANCE;
    }
}
