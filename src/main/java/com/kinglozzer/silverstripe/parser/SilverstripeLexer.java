package com.kinglozzer.silverstripe.parser;

import com.intellij.lexer.FlexAdapter;

public class SilverstripeLexer extends FlexAdapter {
    public SilverstripeLexer() {
        super(new _SilverstripeLexer());
    }
}
