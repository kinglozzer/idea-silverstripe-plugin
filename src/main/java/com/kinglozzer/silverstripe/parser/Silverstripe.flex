package com.kinglozzer.silverstripe.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.Stack;

%%

%{
    public _SilverstripeLexer() {
        this((java.io.Reader)null);
    }

    private void resetAll() {
    }
%}

%class _SilverstripeLexer
%implements FlexLexer
%final
%unicode
%function advance
%type IElementType

%{
    private Stack<Integer> stack = new Stack<Integer>();

    public void yypushstate(int newState) {
        stack.push(yystate());
        yybegin(newState);
    }

    public void yypopstate() {
        yybegin(stack.pop());
    }

    public void yycleanstates() {
        while(!stack.isEmpty()) {
            yybegin(stack.pop());
        }
    }
%}

CRLF= (\n|\r|\r\n)
WHITE_SPACE= [\ \t]
NUMBER=[0-9]+
SS_LOOKUP= \$[a-zA-Z_]+([a-zA-Z0-9_])*
SS_IDENTIFIER= [a-zA-Z_]+([a-zA-Z0-9_])*
SS_BLOCK_START= <%
SS_BLOCK_END= %>
SS_START_KEYWORD= loop | with
SS_IF_KEYWORD= if
SS_ELSE_IF_KEYWORD= else_if
SS_ELSE_KEYWORD= else
SS_IS_KEYWORD= is
SS_COMPARISON_OPERATOR= "==" | "!=" | "=" | "not" | ">" | "<" | ">=" | "<="
SS_AND_OR_OPERATOR= "&&" | "||"
SS_STRING= \"[^\"]*\" | \'[^\']*\'
SS_PRIMITIVE= true | false | null
SS_SIMPLE_KEYWORD= base_tag
SS_INCLUDE_KEYWORD= include
SS_INCLUDE_FILE= [a-zA-Z_\\\/]+([a-zA-Z0-9_\\\/])*
SS_REQUIRE_KEYWORD= require
SS_CACHED_KEYWORD= cached
SS_END_KEYWORD= end_loop | end_if | end_with | end_cached
SS_COMMENT_START= <%--
SS_COMMENT_END= --%>
SS_TRANSLATION_START= <%t
SS_STRING_NO_QUOTES=[^\"\'\)]+
SS_TRANSLATION_IDENTIFIER= [a-zA-Z_\\]+\.[a-zA-Z_\\]+

SS_TEXT= (([^<${\\]+) | (\\.) | (<[^%]) | (\$[^A-Za-z_]) | (\{[^\$]) | (\{\$[^A-Za-z_]))+

%state SS_INJECTION
%state SS_LOOKUP
%state SS_LOOKUP_STEP
%state SS_LOOKUP_ARGUMENTS
%state SS_BLOCK_START
%state SS_BLOCK_STATEMENT
%state SS_BAD_BLOCK_STATEMENT
%state SS_IF_STATEMENT
%state SS_INCLUDE_STATEMENT
%state SS_TRANSLATION_STATEMENT
%state SS_CACHED_STATEMENT
%state SS_REQUIRE_STATEMENT
%state SS_REQUIRE_CONTENT
%state SS_INCLUDE_VARS
%state SS_NAMED_VAR
%state SS_COMMENT
%%

<YYINITIAL> {
{SS_TEXT}? "<%"  {
    // Backtrack over the <% characters
    while (yylength() > 0 && (
        yytext().subSequence(yylength() - 1, yylength()).toString().equals("%")
        || yytext().subSequence(yylength() - 1, yylength()).toString().equals("<")
    )) {
        yypushback(1);
    }

    yypushstate(SS_BLOCK_START);
    // Lex whitespace separately - the formatter needs this... no idea why
    if (yytext().toString().trim().length() == 0) {
        return TokenType.WHITE_SPACE;
    }
    return SilverstripeTokenTypes.SS_TEXT;
}

{SS_TEXT}? (\{?\$) {
    // Backtrack over the {$ characters
    while (yylength() > 0 && (
        yytext().subSequence(yylength() - 1, yylength()).toString().equals("$")
        || yytext().subSequence(yylength() - 1, yylength()).toString().equals("{")
    )) {
        yypushback(1);
    }

    yypushstate(SS_INJECTION);
    // Lex whitespace separately - the formatter needs this... no idea why
    if (yytext().toString().trim().length() == 0) {
        return TokenType.WHITE_SPACE;
    }
    return SilverstripeTokenTypes.SS_TEXT;
}

{SS_TEXT} {
    // Lex whitespace separately - the formatter needs this... no idea why
    if (yytext().toString().trim().length() == 0) {
        return TokenType.WHITE_SPACE;
    }
    return SilverstripeTokenTypes.SS_TEXT;
}
}

<SS_BLOCK_START> {
    {SS_BLOCK_START}                    { return SilverstripeTokenTypes.SS_BLOCK_START; }
    {SS_START_KEYWORD}                  { yypushstate(SS_BLOCK_STATEMENT); return SilverstripeTokenTypes.SS_START_KEYWORD; }
    {SS_SIMPLE_KEYWORD}                 { yypushstate(SS_BLOCK_STATEMENT); return SilverstripeTokenTypes.SS_SIMPLE_KEYWORD; }
    {SS_INCLUDE_KEYWORD}                { yypushstate(SS_INCLUDE_STATEMENT); return SilverstripeTokenTypes.SS_INCLUDE_KEYWORD; }
    {SS_REQUIRE_KEYWORD}                { yypushstate(SS_REQUIRE_STATEMENT); return SilverstripeTokenTypes.SS_REQUIRE_KEYWORD; }
    {SS_CACHED_KEYWORD}                 { yypushstate(SS_CACHED_STATEMENT); return SilverstripeTokenTypes.SS_CACHED_KEYWORD; }
    {SS_TRANSLATION_START}              { yypushstate(SS_TRANSLATION_STATEMENT); yypushback(1); return SilverstripeTokenTypes.SS_BLOCK_START; }
    {SS_COMMENT_START}                  { yypushstate(SS_COMMENT); return SilverstripeTokenTypes.SS_COMMENT_START; }
    {SS_IF_KEYWORD}                     { yypushstate(SS_IF_STATEMENT); return SilverstripeTokenTypes.SS_IF_KEYWORD; }
    {SS_ELSE_IF_KEYWORD}                { yypushstate(SS_IF_STATEMENT); return SilverstripeTokenTypes.SS_ELSE_IF_KEYWORD; }
    {SS_ELSE_KEYWORD}                   { return SilverstripeTokenTypes.SS_ELSE_KEYWORD; }
    {SS_END_KEYWORD}                    { return SilverstripeTokenTypes.SS_END_KEYWORD; }
    {SS_COMMENT_END}                    { yypopstate(); return SilverstripeTokenTypes.SS_COMMENT_END; }
    {SS_BLOCK_END}                      { yypopstate(); return SilverstripeTokenTypes.SS_BLOCK_END; }
    {WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
    {CRLF}+                             { yypushback(yylength()); yypushstate(SS_BAD_BLOCK_STATEMENT); }
    .                                   { yypushback(yylength()); yypushstate(SS_BAD_BLOCK_STATEMENT); }
}

<SS_INCLUDE_STATEMENT> {
    {SS_INCLUDE_FILE}                   { yypushstate(SS_INCLUDE_VARS); return SilverstripeTokenTypes.SS_INCLUDE_FILE; }
    {SS_BLOCK_END}                      { yycleanstates(); return SilverstripeTokenTypes.SS_BLOCK_END; }
    {WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_INCLUDE_VARS> {
    ","                                             { return SilverstripeTokenTypes.SS_COMMA; }
    {SS_IDENTIFIER}{WHITE_SPACE}*"="{WHITE_SPACE}*  { yypushback(1); yypushstate(SS_NAMED_VAR); return SilverstripeTokenTypes.SS_NAMED_ARGUMENT_NAME; }
    {WHITE_SPACE}+                                  { return TokenType.WHITE_SPACE; }
    {CRLF}+                                         { yypushback(yylength()); yypopstate(); }
    .                                               { yypushback(yylength()); yypopstate(); }
}

<SS_NAMED_VAR> {
    "="                                 { return SilverstripeTokenTypes.SS_EQUALS; }
    {NUMBER}                            { return SilverstripeTokenTypes.SS_NUMBER; }
    {SS_PRIMITIVE}                      { return SilverstripeTokenTypes.SS_PRIMITIVE; }
    {SS_STRING}                         { return SilverstripeTokenTypes.SS_STRING; }
    {SS_LOOKUP}                         { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_REQUIRE_STATEMENT> {
    "css"                               { return SilverstripeTokenTypes.SS_REQUIRE_CSS; }
    "javascript"                        { return SilverstripeTokenTypes.SS_REQUIRE_JS; }
    "themedCSS"                         { return SilverstripeTokenTypes.SS_REQUIRE_THEMED_CSS; }
    "themedJavascript"                  { return SilverstripeTokenTypes.SS_REQUIRE_THEMED_JS; }
    "("                                 { yypushstate(SS_REQUIRE_CONTENT); return SilverstripeTokenTypes.SS_LEFT_PARENTHESIS; }
    {WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_REQUIRE_CONTENT> {
    ")"                                 { yypopstate(); return SilverstripeTokenTypes.SS_RIGHT_PARENTHESIS; }
    {SS_STRING}                         { return SilverstripeTokenTypes.SS_STRING; }
    {SS_STRING_NO_QUOTES}               { return SilverstripeTokenTypes.SS_STRING; }
    {WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_IF_STATEMENT> {
    {SS_COMPARISON_OPERATOR}            { return SilverstripeTokenTypes.SS_COMPARISON_OPERATOR; }
    {NUMBER}                            { return SilverstripeTokenTypes.SS_NUMBER; }
    {SS_AND_OR_OPERATOR}                { return SilverstripeTokenTypes.SS_AND_OR_OPERATOR; }
    {SS_STRING}                         { return SilverstripeTokenTypes.SS_STRING; }
    {SS_IDENTIFIER}                     { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_LOOKUP}                         { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_BLOCK_END}                      { yycleanstates(); return SilverstripeTokenTypes.SS_BLOCK_END; }
    {WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_TRANSLATION_STATEMENT> {
    "t"                                             { return SilverstripeTokenTypes.SS_TRANSLATION_KEYWORD; }
    {SS_IS_KEYWORD}                                 { return SilverstripeTokenTypes.SS_IS_KEYWORD; }
    {SS_TRANSLATION_IDENTIFIER}                     { return SilverstripeTokenTypes.SS_TRANSLATION_IDENTIFIER; }
    {SS_STRING}                                     { return SilverstripeTokenTypes.SS_STRING; }
    {SS_IDENTIFIER}{WHITE_SPACE}*"="{WHITE_SPACE}*  { yypushback(1); yypushstate(SS_NAMED_VAR); return SilverstripeTokenTypes.SS_NAMED_ARGUMENT_NAME; }
    {SS_BLOCK_END}                                  { yycleanstates(); return SilverstripeTokenTypes.SS_BLOCK_END; }
    {WHITE_SPACE}+                                  { return TokenType.WHITE_SPACE; }
    {CRLF}+                                         { yypushback(yylength()); yypopstate(); }
    .                                               { yypushback(yylength()); yypopstate(); }
}

<SS_BLOCK_STATEMENT> {
    {SS_IDENTIFIER}                     { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_LOOKUP}                         { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_BLOCK_END}                      { yycleanstates(); return SilverstripeTokenTypes.SS_BLOCK_END; }
    {WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_BAD_BLOCK_STATEMENT> {
    // If we encounter an unfinished block followed by a new block start, we want to resume lexing at the new block
    // {CRLF} is included so matching stops at a newline - we only want to highlight the current line, not *everything* after this point
    !([^]*("%>"|{CRLF})[^]*) "<%" {
        // Backtrack until we've passed back over the <% characters of the new block
        while (yylength() > 0 && (
            yytext().subSequence(yylength() - 1, yylength()).toString().equals("%")
            || yytext().subSequence(yylength() - 1, yylength()).toString().equals("<")
        )) {
            yypushback(1);
        }

        yycleanstates(); // Reset state to resume lexing
        return SilverstripeTokenTypes.SS_UNFINISHED_BLOCK_STATEMENT;
    }

    // A "bad block" (finished, but still invalid). We look for <% in here too so that this rule matches shorter
    // strings than the above rule - jflex always prioritises whichever rule matches the longest string
    !([^]*("<%"|"%>")[^]*) "%>"         { yycleanstates(); return SilverstripeTokenTypes.SS_BAD_BLOCK_STATEMENT; }

    // Anything else here is an unfinished block at the end of a file
    .+                                  { yycleanstates(); return SilverstripeTokenTypes.SS_UNFINISHED_BLOCK_STATEMENT; }
}

<SS_COMMENT> {
    ~"--%>"                             { yypopstate(); yypushback(4); return SilverstripeTokenTypes.SS_COMMENT; }
    !([^]*"--%>"[^]*)                   { return SilverstripeTokenTypes.SS_COMMENT; }
}

<SS_CACHED_STATEMENT> {
    ","                                 { return SilverstripeTokenTypes.SS_COMMA; }
    {SS_IDENTIFIER}                     { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_LOOKUP}                         { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_STRING}                         { return SilverstripeTokenTypes.SS_STRING; }
    {SS_BLOCK_END}                      { yycleanstates(); return SilverstripeTokenTypes.SS_BLOCK_END; }
    {WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_INJECTION> {
    {SS_LOOKUP}                         { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    "{"                                 { return SilverstripeTokenTypes.SS_LEFT_BRACE; }
    "}"                                 { yycleanstates(); return SilverstripeTokenTypes.SS_RIGHT_BRACE; }
    {WHITE_SPACE}+                      { yycleanstates(); return TokenType.WHITE_SPACE; }
    .                                   { yycleanstates(); return SilverstripeTokenTypes.SS_TEXT; }
}

<SS_LOOKUP> {
    "$ThemeDir"                         { return SilverstripeTokenTypes.SS_THEME_DIR; }
    "."                                 { yypushstate(SS_LOOKUP_STEP); return SilverstripeTokenTypes.SS_DOT; }
    "("                                 { yypushstate(SS_LOOKUP_ARGUMENTS); return SilverstripeTokenTypes.SS_LEFT_PARENTHESIS; }
    {SS_LOOKUP}                         { return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_BLOCK_END}                      { yycleanstates(); return SilverstripeTokenTypes.SS_BLOCK_END; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_LOOKUP_ARGUMENTS> {
    ","                                 { return SilverstripeTokenTypes.SS_COMMA; }
    "."                                 { return SilverstripeTokenTypes.SS_DOT; }
    ")"                                 { yypopstate(); return SilverstripeTokenTypes.SS_RIGHT_PARENTHESIS; }
    {SS_PRIMITIVE}                      { return SilverstripeTokenTypes.SS_PRIMITIVE; }
    {SS_IDENTIFIER}                     { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_LOOKUP}                         { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP; }
    {SS_STRING}                         { return SilverstripeTokenTypes.SS_STRING; }
    {NUMBER}                            { return SilverstripeTokenTypes.SS_NUMBER; }
    {WHITE_SPACE}+                      { return TokenType.WHITE_SPACE; }
    {CRLF}+                             { yypushback(yylength()); yypopstate(); }
    .                                   { yypushback(yylength()); yypopstate(); }
}

<SS_LOOKUP_STEP> {
    [a-zA-Z_]+([a-zA-Z0-9_])*           { yypopstate(); return SilverstripeTokenTypes.SS_IDENTIFIER; }
    .                                   { yypushback(yylength()); yypopstate(); }
}

{WHITE_SPACE}+                          { return TokenType.WHITE_SPACE; }
{CRLF}+                                 { return TokenType.WHITE_SPACE; }
.                                       { return TokenType.BAD_CHARACTER; }
