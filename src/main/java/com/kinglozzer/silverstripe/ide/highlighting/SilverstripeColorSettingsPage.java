package com.kinglozzer.silverstripe.ide.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.kinglozzer.silverstripe.SilverstripeFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class SilverstripeColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
        new AttributesDescriptor("String", SilverstripeSyntaxHighlighter.SS_STRING),
        new AttributesDescriptor("Number", SilverstripeSyntaxHighlighter.SS_NUMBER),
        new AttributesDescriptor("Primitive (true/false/null)", SilverstripeSyntaxHighlighter.SS_PRIMITIVE),
        new AttributesDescriptor("Dot", SilverstripeSyntaxHighlighter.SS_DOT),
        new AttributesDescriptor("Comma", SilverstripeSyntaxHighlighter.SS_COMMA),
        new AttributesDescriptor("Brace", SilverstripeSyntaxHighlighter.SS_BRACE),
        new AttributesDescriptor("Parenthesis", SilverstripeSyntaxHighlighter.SS_PARENTHESIS),
        new AttributesDescriptor("Operator (=, <, >)", SilverstripeSyntaxHighlighter.SS_OPERATOR),
        new AttributesDescriptor("Lookup ({$Variable})", SilverstripeSyntaxHighlighter.SS_LOOKUP),
        new AttributesDescriptor("Identifier (e.g. include name)", SilverstripeSyntaxHighlighter.SS_IDENTIFIER),
        new AttributesDescriptor("Block tags (<%, %>)", SilverstripeSyntaxHighlighter.SS_BLOCK),
        new AttributesDescriptor("Keyword (if, else_if, loop etc)", SilverstripeSyntaxHighlighter.SS_KEYWORD),
        new AttributesDescriptor("Comment", SilverstripeSyntaxHighlighter.SS_COMMENT),
        new AttributesDescriptor("Bad character/block", SilverstripeSyntaxHighlighter.SS_BAD_CHARACTER)
    };

    @Override
    public @Nullable Icon getIcon() {
        return SilverstripeFileType.FILE_ICON;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new SilverstripeSyntaxHighlighter();
    }

    @Override
    public @NonNls
    @NotNull String getDemoText() {
        return "{$Variable('string')}\n" +
            "<% if $Condition && $AnotherCondition = 'test' %>\n" +
            "\t<%t App\\Translation.Entity 'Translation {value}' value=$Something is 'An example' %>\n" +
            "\t<%-- Something commented out --%>\n" +
            "<% end_if %>\n" +
            "<% include Something Arg1=$Something, Arg2='Something else' %>\n" +
            "<% loop $List.Limit(123).Offset(456) %>\n" +
            "\t{$Variable(true, null)}\n" +
            "<% end_loop %>\n" +
            "<% bad/unfinished block";
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Silverstripe";
    }
}
