package com.kinglozzer.silverstripe.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.OuterLanguageElementType;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.psi.SilverstripeTokenType;

public final class SilverstripeTokenTypes {
    // Statement wrappers
    public static final IElementType SS_FRAGMENT = new OuterLanguageElementType("SS_FRAGMENT", SilverstripeLanguage.INSTANCE);
    public static final IElementType SS_STATEMENTS = new SilverstripeElementType("SS_STATEMENTS");

    // Lookup statements
    public static final IElementType SS_LOOKUP = new SilverstripeElementType("SS_LOOKUP");
    public static final IElementType SS_LOOKUP_STEP = new SilverstripeElementType("SS_LOOKUP_STEP");
    public static final IElementType SS_LOOKUP_STEP_ARGS = new SilverstripeElementType("SS_LOOKUP_STEP_ARGS");

    // Block wrapper statements
    // todo - rename to closed block statement
    public static final IElementType SS_BLOCK_STATEMENT = new SilverstripeElementType("SS_BLOCK_STATEMENT");
    public static final IElementType SS_BLOCK_START_STATEMENT = new SilverstripeElementType("SS_BLOCK_START_STATEMENT");
    // todo - rename to open block statement, or just block statement?
    public static final IElementType SS_BLOCK_SIMPLE_STATEMENT = new SilverstripeElementType("SS_BLOCK_SIMPLE_STATEMENT");
    public static final IElementType SS_BLOCK_END_STATEMENT = new SilverstripeElementType("SS_BLOCK_END_STATEMENT");
    public static final IElementType SS_UNFINISHED_BLOCK_STATEMENT = new SilverstripeElementType("SS_UNFINISHED_BLOCK_STATEMENT");
    public static final IElementType SS_BAD_BLOCK_STATEMENT = new SilverstripeElementType("SS_BAD_BLOCK_STATEMENT");

    // Block type statements
    public static final IElementType SS_IF_STATEMENT = new SilverstripeElementType("SS_IF_STATEMENT");
    public static final IElementType SS_ELSE_IF_STATEMENT = new SilverstripeElementType("SS_ELSE_IF_STATEMENT");
    public static final IElementType SS_ELSE_STATEMENT = new SilverstripeElementType("SS_ELSE_STATEMENT");
    public static final IElementType SS_COMMENT_STATEMENT = new SilverstripeElementType("SS_COMMENT_STATEMENT");
    public static final IElementType SS_TRANSLATION_STATEMENT = new SilverstripeElementType("SS_TRANSLATION_STATEMENT");
    public static final IElementType SS_INCLUDE_STATEMENT = new SilverstripeElementType("SS_INCLUDE_STATEMENT");
    public static final IElementType SS_CACHED_STATEMENT = new SilverstripeElementType("SS_CACHED_STATEMENT");
    public static final IElementType SS_REQUIRE_STATEMENT = new SilverstripeElementType("SS_REQUIRE_STATEMENT");

    // Block tokens
    public static final IElementType SS_BLOCK_START = new SilverstripeTokenType("SS_BLOCK_START");
    public static final IElementType SS_BLOCK_END = new SilverstripeTokenType("SS_BLOCK_END");

    // Open block keyword tokens
    public static final IElementType SS_START_KEYWORD = new SilverstripeTokenType("SS_START_KEYWORD");
    public static final IElementType SS_CACHED_KEYWORD = new SilverstripeTokenType("SS_CACHED_KEYWORD");
    public static final IElementType SS_IF_KEYWORD = new SilverstripeTokenType("SS_IF_KEYWORD");
    public static final IElementType SS_ELSE_IF_KEYWORD = new SilverstripeTokenType("SS_ELSE_IF_KEYWORD");
    public static final IElementType SS_ELSE_KEYWORD = new SilverstripeTokenType("SS_ELSE_KEYWORD");
    public static final IElementType SS_END_KEYWORD = new SilverstripeTokenType("SS_END_KEYWORD");
    public static final IElementType SS_BLOCK_NAME = new SilverstripeTokenType("SS_BLOCK_NAME");

    // Closed block keyword tokens
    public static final IElementType SS_TRANSLATION_KEYWORD = new SilverstripeTokenType("SS_TRANSLATION_KEYWORD");
    public static final IElementType SS_INCLUDE_KEYWORD = new SilverstripeTokenType("SS_INCLUDE_KEYWORD");
    public static final IElementType SS_SIMPLE_KEYWORD = new SilverstripeTokenType("SS_SIMPLE_KEYWORD");
    public static final IElementType SS_REQUIRE_KEYWORD = new SilverstripeTokenType("SS_REQUIRE_KEYWORD");

    // Comment tokens
    public static final IElementType SS_COMMENT_START = new SilverstripeTokenType("SS_COMMENT_START");
    public static final IElementType SS_COMMENT = new SilverstripeTokenType("SS_COMMENT");
    public static final IElementType SS_COMMENT_END = new SilverstripeTokenType("SS_COMMENT_END");

    // Translation tokens
    public static final IElementType SS_TRANSLATION_IDENTIFIER = new SilverstripeTokenType("SS_TRANSLATION_IDENTIFIER");
    public static final IElementType SS_IS_KEYWORD = new SilverstripeTokenType("SS_IS_KEYWORD");

    // Include tokens
    public static final IElementType SS_INCLUDE_FILE = new SilverstripeTokenType("SS_INCLUDE_FILE");

    // Require tokens
    public static final IElementType SS_REQUIRE_CSS = new SilverstripeTokenType("SS_REQUIRE_CSS");
    public static final IElementType SS_REQUIRE_THEMED_CSS = new SilverstripeTokenType("SS_REQUIRE_THEMED_CSS");
    public static final IElementType SS_REQUIRE_JS = new SilverstripeTokenType("SS_REQUIRE_JS");
    public static final IElementType SS_REQUIRE_THEMED_JS = new SilverstripeTokenType("SS_REQUIRE_THEMED_JS");

    // Special text tokens
    public static final IElementType SS_STRING = new SilverstripeTokenType("SS_STRING");
    public static final IElementType SS_NUMBER = new SilverstripeTokenType("SS_NUMBER");
    public static final IElementType SS_PRIMITIVE = new SilverstripeTokenType("SS_PRIMITIVE");
    public static final IElementType SS_THEME_DIR = new SilverstripeTokenType("SS_THEME_DIR");
    public static final IElementType SS_DOT = new SilverstripeTokenType("SS_DOT");
    public static final IElementType SS_COMMA = new SilverstripeTokenType("SS_COMMA");
    public static final IElementType SS_IDENTIFIER = new SilverstripeTokenType("SS_IDENTIFIER");
    public static final IElementType SS_NAMED_ARGUMENT_NAME = new SilverstripeTokenType("SS_NAMED_ARGUMENT_NAME");
    public static final IElementType SS_LEFT_PARENTHESIS = new SilverstripeTokenType("SS_LEFT_PARENTHESIS");
    public static final IElementType SS_RIGHT_PARENTHESIS = new SilverstripeTokenType("SS_RIGHT_PARENTHESIS");
    public static final IElementType SS_LEFT_BRACE = new SilverstripeTokenType("SS_LEFT_BRACE");
    public static final IElementType SS_RIGHT_BRACE = new SilverstripeTokenType("SS_RIGHT_BRACE");

    // Operator tokens
    public static final IElementType SS_EQUALS = new SilverstripeTokenType("SS_EQUALS");
    public static final IElementType SS_COMPARISON_OPERATOR = new SilverstripeTokenType("SS_COMPARISON_OPERATOR");
    public static final IElementType SS_AND_OR_OPERATOR = new SilverstripeTokenType("SS_AND_OR_OPERATOR");

    // Text tokens
    public static final IElementType SS_TEXT = new SilverstripeTokenType("SS_TEXT");

    private SilverstripeTokenTypes() {
    }
}
