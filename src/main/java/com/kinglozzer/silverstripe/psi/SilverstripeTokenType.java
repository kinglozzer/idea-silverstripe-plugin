package com.kinglozzer.silverstripe.psi;

import com.intellij.psi.tree.IElementType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SilverstripeTokenType extends IElementType {
    public SilverstripeTokenType(@NotNull @NonNls String debugName) {
        super(debugName, SilverstripeLanguage.INSTANCE);
    }
}
