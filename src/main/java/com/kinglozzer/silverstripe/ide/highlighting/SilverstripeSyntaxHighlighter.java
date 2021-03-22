package com.kinglozzer.silverstripe.ide.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.kinglozzer.silverstripe.parser.SilverstripeLexer;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class SilverstripeSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey SS_STRING =
        createTextAttributesKey("SS_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey SS_NUMBER =
        createTextAttributesKey("SS_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey SS_PRIMITIVE =
        createTextAttributesKey("SS_PRIMITIVE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey SS_DOT =
        createTextAttributesKey("SS_DOT", DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey SS_COMMA =
        createTextAttributesKey("SS_COMMA", DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey SS_BRACE =
        createTextAttributesKey("SS_BRACE", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey SS_PARENTHESIS =
        createTextAttributesKey("SS_PARENTHESIS", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey SS_OPERATOR =
        createTextAttributesKey("SS_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey SS_LOOKUP =
        createTextAttributesKey("SS_LOOKUP", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    public static final TextAttributesKey SS_IDENTIFIER =
        createTextAttributesKey("SS_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey SS_BLOCK =
        createTextAttributesKey("SS_BLOCK", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey SS_KEYWORD =
        createTextAttributesKey("SS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey SS_COMMENT =
        createTextAttributesKey("SS_COMMENT", XmlHighlighterColors.HTML_COMMENT);
    public static final TextAttributesKey SS_BAD_CHARACTER =
        createTextAttributesKey("SS_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] SS_STRING_KEYS = new TextAttributesKey[]{SS_STRING};
    private static final TextAttributesKey[] SS_NUMBER_KEYS = new TextAttributesKey[]{SS_NUMBER};
    private static final TextAttributesKey[] SS_PRIMITIVE_KEYS = new TextAttributesKey[]{SS_PRIMITIVE};
    private static final TextAttributesKey[] SS_DOT_KEYS = new TextAttributesKey[]{SS_DOT};
    private static final TextAttributesKey[] SS_COMMA_KEYS = new TextAttributesKey[]{SS_COMMA};
    private static final TextAttributesKey[] SS_BRACE_KEYS = new TextAttributesKey[]{SS_BRACE};
    private static final TextAttributesKey[] SS_PARENTHESIS_KEYS = new TextAttributesKey[]{SS_PARENTHESIS};
    private static final TextAttributesKey[] SS_OPERATOR_KEYS = new TextAttributesKey[]{SS_OPERATOR};
    private static final TextAttributesKey[] SS_LOOKUP_KEYS = new TextAttributesKey[]{SS_LOOKUP};
    private static final TextAttributesKey[] SS_BLOCK_KEYS = new TextAttributesKey[]{SS_BLOCK};
    private static final TextAttributesKey[] SS_IDENTIFIER_KEYS = new TextAttributesKey[]{SS_IDENTIFIER};
    private static final TextAttributesKey[] SS_KEYWORD_KEYS = new TextAttributesKey[]{SS_KEYWORD};
    private static final TextAttributesKey[] SS_COMMENT_KEYS = new TextAttributesKey[]{SS_COMMENT};
    private static final TextAttributesKey[] SS_BAD_CHARACTER_KEYS = new TextAttributesKey[]{SS_BAD_CHARACTER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    private static final TokenSet BRACE_TOKENS = TokenSet.create(
        SilverstripeTokenTypes.SS_LEFT_BRACE,
        SilverstripeTokenTypes.SS_RIGHT_BRACE
    );

    private static final TokenSet PARENTHESIS_TOKENS = TokenSet.create(
        SilverstripeTokenTypes.SS_LEFT_PARENTHESIS,
        SilverstripeTokenTypes.SS_RIGHT_PARENTHESIS
    );

    private static final TokenSet OPERATOR_TOKENS = TokenSet.create(
        SilverstripeTokenTypes.SS_COMPARISON_OPERATOR,
        SilverstripeTokenTypes.SS_AND_OR_OPERATOR
    );

    private static final TokenSet BLOCK_TOKENS = TokenSet.create(
        SilverstripeTokenTypes.SS_BLOCK_START,
        SilverstripeTokenTypes.SS_BLOCK_END
    );

    private static final TokenSet IDENTIFIER_TOKENS = TokenSet.create(
        SilverstripeTokenTypes.SS_IDENTIFIER,
        SilverstripeTokenTypes.SS_INCLUDE_FILE,
        SilverstripeTokenTypes.SS_TRANSLATION_IDENTIFIER,
        SilverstripeTokenTypes.SS_REQUIRE_CSS,
        SilverstripeTokenTypes.SS_REQUIRE_THEMED_CSS,
        SilverstripeTokenTypes.SS_REQUIRE_JS,
        SilverstripeTokenTypes.SS_REQUIRE_THEMED_JS
    );

    private static final TokenSet KEYWORD_TOKENS = TokenSet.create(
        SilverstripeTokenTypes.SS_START_KEYWORD,
        SilverstripeTokenTypes.SS_END_KEYWORD,
        SilverstripeTokenTypes.SS_IF_KEYWORD,
        SilverstripeTokenTypes.SS_ELSE_IF_KEYWORD,
        SilverstripeTokenTypes.SS_ELSE_KEYWORD,
        SilverstripeTokenTypes.SS_SIMPLE_KEYWORD,
        SilverstripeTokenTypes.SS_INCLUDE_KEYWORD,
        SilverstripeTokenTypes.SS_CACHED_KEYWORD,
        SilverstripeTokenTypes.SS_REQUIRE_KEYWORD,
        SilverstripeTokenTypes.SS_TRANSLATION_KEYWORD,
        SilverstripeTokenTypes.SS_BLOCK_NAME,
        SilverstripeTokenTypes.SS_IS_KEYWORD
    );

    private static final TokenSet COMMENT_TOKENS = TokenSet.create(
        SilverstripeTokenTypes.SS_COMMENT_START,
        SilverstripeTokenTypes.SS_COMMENT,
        SilverstripeTokenTypes.SS_COMMENT_END
    );

    private static final TokenSet BAD_TOKENS = TokenSet.create(
        SilverstripeTokenTypes.SS_BAD_BLOCK_STATEMENT,
        SilverstripeTokenTypes.SS_UNFINISHED_BLOCK_STATEMENT,
        TokenType.BAD_CHARACTER
    );

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SilverstripeLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(SilverstripeTokenTypes.SS_STRING)) {
            return SS_STRING_KEYS;
        } else if (tokenType.equals(SilverstripeTokenTypes.SS_NUMBER)) {
            return SS_NUMBER_KEYS;
        } else if (tokenType.equals(SilverstripeTokenTypes.SS_PRIMITIVE)) {
            return SS_PRIMITIVE_KEYS;
        } else if (tokenType.equals(SilverstripeTokenTypes.SS_DOT)) {
            return SS_DOT_KEYS;
        } else if (tokenType.equals(SilverstripeTokenTypes.SS_COMMA)) {
            return SS_COMMA_KEYS;
        } else if (BRACE_TOKENS.contains(tokenType)) {
            return SS_BRACE_KEYS;
        } else if (PARENTHESIS_TOKENS.contains(tokenType)) {
            return SS_PARENTHESIS_KEYS;
        } else if (OPERATOR_TOKENS.contains(tokenType)) {
            return SS_OPERATOR_KEYS;
        } else if (tokenType.equals(SilverstripeTokenTypes.SS_LOOKUP)) {
            return SS_LOOKUP_KEYS;
        } else if (BLOCK_TOKENS.contains(tokenType)) {
            return SS_BLOCK_KEYS;
        } else if (IDENTIFIER_TOKENS.contains(tokenType)) {
            return SS_IDENTIFIER_KEYS;
        } else if (KEYWORD_TOKENS.contains(tokenType)) {
            return SS_KEYWORD_KEYS;
        } else if (COMMENT_TOKENS.contains(tokenType)) {
            return SS_COMMENT_KEYS;
        } else if (BAD_TOKENS.contains(tokenType)) {
            return SS_BAD_CHARACTER_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
