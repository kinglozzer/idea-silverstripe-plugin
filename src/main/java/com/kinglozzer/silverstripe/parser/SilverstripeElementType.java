package com.kinglozzer.silverstripe.parser;

import com.intellij.psi.tree.IElementType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class SilverstripeElementType extends IElementType {
    SilverstripeElementType(@NotNull @NonNls String debugName) {
        super(debugName, SilverstripeLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "[Ss] " + super.toString();
    }
}
