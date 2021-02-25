package com.kinglozzer.silverstripe.files;

import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.PsiManager;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import org.jetbrains.annotations.NotNull;

public class SilverstripeFileViewProviderFactory implements FileViewProviderFactory {
    @NotNull
    @Override
    public FileViewProvider createFileViewProvider(@NotNull VirtualFile virtualFile,
                                                   Language language,
                                                   @NotNull PsiManager psiManager,
                                                   boolean eventSystemEnabled) {
        assert language.isKindOf(SilverstripeLanguage.INSTANCE);
        return new SilverstripeFileViewProvider(psiManager, virtualFile, eventSystemEnabled, language);
    }
}
