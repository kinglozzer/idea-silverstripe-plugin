package com.kinglozzer.silverstripe.psi.references;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;
import com.kinglozzer.silverstripe.psi.impl.SilverstripeIncludeImpl;
import com.kinglozzer.silverstripe.util.SilverstripeFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SilverstripeIncludeReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String key;

    public SilverstripeIncludeReference(@NotNull PsiNamedElement element, TextRange textRange) {
        super(element, textRange);
        SilverstripeIncludeImpl includeElement = (SilverstripeIncludeImpl) element;
        String name = includeElement.getName();
        key = name.replaceAll("\\\\", "/")  // Replace \ with /
            .replaceAll("^/", "");          // Remove leading /
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        final List<ResolveResult> results = new ArrayList<>();
        Project project = myElement.getProject();
        final List<SilverstripePsiFile> properties = SilverstripeFileUtil.findIncludeTemplate(project, key + ".ss");
        for (SilverstripePsiFile property : properties) {
            results.add(new PsiElementResolveResult(property));
        }
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        SilverstripeIncludeImpl includeElement = (SilverstripeIncludeImpl) this.getElement();
        ASTNode includeFileNode = includeElement.getIncludeFileNode();
        return includeFileNode.getPsi().getTextRange();
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        return EMPTY_ARRAY;
    }
}
