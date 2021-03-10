package com.kinglozzer.silverstripe.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.kinglozzer.silverstripe.SilverstripeLanguage;
import com.kinglozzer.silverstripe.psi.SilverstripePsiFile;
import com.kinglozzer.silverstripe.psi.impl.SilverstripeIncludeImpl;
import com.kinglozzer.silverstripe.psi.impl.SilverstripeLookupStepImpl;
import com.kinglozzer.silverstripe.psi.impl.SilverstripePsiElementImpl;
import org.jetbrains.annotations.NotNull;

public class SilverstripeParserDefinition implements ParserDefinition {
    private static final IFileElementType FILE_NODE_TYPE = new IFileElementType("FILE", SilverstripeLanguage.INSTANCE);

    @Override
    @NotNull
    public Lexer createLexer(Project project) {
        return new SilverstripeLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new SilverstripeParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE_NODE_TYPE;
    }

    @Override
    @NotNull
    public TokenSet getWhitespaceTokens() {
        return TokenSet.create(TokenType.WHITE_SPACE);
    }

    @Override
    @NotNull
    public TokenSet getCommentTokens() {
        return TokenSet.create(SilverstripeTokenTypes.SS_COMMENT);
    }

    @Override
    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @Override
    @NotNull
    public PsiElement createElement(ASTNode node) {
        final IElementType elementType = node.getElementType();

        if (elementType == SilverstripeTokenTypes.SS_INCLUDE_STATEMENT) {
            return new SilverstripeIncludeImpl(node);
        }

        if (elementType == SilverstripeTokenTypes.SS_LOOKUP_STEP) {
            return new SilverstripeLookupStepImpl(node);
        }

        return new SilverstripePsiElementImpl(node);
    }

    @Override
    public @NotNull PsiFile createFile(FileViewProvider viewProvider) {
        return new SilverstripePsiFile(viewProvider);
    }

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
