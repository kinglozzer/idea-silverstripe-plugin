package com.kinglozzer.silverstripe.parser;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergeFunction;
import com.intellij.lexer.MergingLexerAdapterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public class SilverstripeMergingLexer extends MergingLexerAdapterBase {
    private static final TokenSet TOKENS_TO_MERGE = TokenSet.create(SilverstripeTokenTypes.SS_COMMENT);

    public SilverstripeMergingLexer() {
        super(new SilverstripeLexer());
    }

    @Override
    public MergeFunction getMergeFunction() {
        return this::merge;
    }

    protected IElementType merge(IElementType type, Lexer originalLexer) {
        if (!TOKENS_TO_MERGE.contains(type)) {
            return type;
        }
        while (true) {
            final IElementType tokenType = originalLexer.getTokenType();
            if (tokenType != type) {
                break;
            }
            originalLexer.advance();
        }
        return type;
    }
}
