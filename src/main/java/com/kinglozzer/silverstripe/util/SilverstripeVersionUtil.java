package com.kinglozzer.silverstripe.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.text.VersionComparatorUtil;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.search.GlobalSearchScope.allScope;

public final class SilverstripeVersionUtil {
    public static final String SILVERSTRIPE_VERSION_4 = "4";

    @NotNull
    public static String getSilverstripeVersion(@NotNull Project project) {
        return CachedValuesManager.getManager(project).getCachedValue(project, () -> {
            final String resultSilverstripeVersion = computeSilverstripeVersion(project);
            return CachedValueProvider.Result.create(resultSilverstripeVersion, ProjectRootManager.getInstance(project));
        });
    }

    public static boolean isSilverstripe4OrMore(@NotNull Project project) {
        return VersionComparatorUtil.compare(getSilverstripeVersion(project), SILVERSTRIPE_VERSION_4) >= 0;
    }

    /**
     * todo - this is pretty naïve, but saves pulling in dependencies to inspect composer lock files...
     */
    @NotNull
    private static String computeSilverstripeVersion(@NotNull Project project) {
        String version = "3";

        PsiFileSystemItem[] candidates = FilenameIndex.getFilesByName(project, "framework", allScope(project), true);
        for (PsiFileSystemItem candidate : candidates) {
            if (!candidate.isDirectory()) {
                continue;
            }

            PsiFileSystemItem parent = candidate.getParent();
            if (parent != null && parent.isDirectory() && parent.getName().equals("silverstripe")) {
                parent = parent.getParent();
                if (parent != null && parent.isDirectory() && parent.getName().equals("vendor")) {
                    version = "4";
                    break;
                }
            }
        }

        return version;
    }
}
