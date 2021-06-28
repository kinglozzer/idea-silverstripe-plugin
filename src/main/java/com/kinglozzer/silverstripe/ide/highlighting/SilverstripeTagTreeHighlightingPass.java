package com.kinglozzer.silverstripe.ide.highlighting;

import com.intellij.application.options.editor.WebEditorOptions;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.codeInsight.daemon.impl.tagTreeHighlighting.XmlTagTreeHighlightingColors;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ui.UIUtil;
import com.kinglozzer.silverstripe.parser.SilverstripeTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SilverstripeTagTreeHighlightingPass extends TextEditorHighlightingPass {
    private static final Key<List<RangeHighlighter>> TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY = Key.create("TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY");

    public static final TextAttributesKey TAG_TREE_HIGHLIGHTING_KEY = TextAttributesKey.createTextAttributesKey("TAG_TREE_HIGHLIGHTING_KEY");
    private static final HighlightInfoType TYPE = new HighlightInfoType.HighlightInfoTypeImpl(HighlightSeverity.INFORMATION,
        TAG_TREE_HIGHLIGHTING_KEY);

    private final PsiFile myFile;
    private final Editor myEditor;

    private final List<ArrayList<TextRange>> myPairsToHighlight = new ArrayList<>();

    SilverstripeTagTreeHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
        super(file.getProject(), editor.getDocument(), true);
        myFile = file;
        myEditor = editor;
    }

    @Override
    public void doCollectInformation(@NotNull ProgressIndicator progress) {
        int offset = myEditor.getCaretModel().getOffset();
        PsiElement element = myFile.findElementAt(offset);
        if (element == null) {
            return;
        }

        // If we've landed on a whitespace token, go back one in case we're also touching the end of a block start/end
        if (element.getNode().getElementType() == TokenType.WHITE_SPACE) {
            PsiElement tmpElement = myFile.findElementAt(offset - 1);
            if (tmpElement != null && tmpElement.getNode().getElementType() == SilverstripeTokenTypes.SS_BLOCK_END) {
                element = tmpElement;
            }
        }

        if (element.getNode().getElementType() == SilverstripeTokenTypes.SS_CLOSED_BLOCK_STATEMENT) {
            myPairsToHighlight.add(getOpenBlockTagRanges(element));
        } else {
            while (element != null) {
                if (element.getNode() == null) {
                    break;
                }

                IElementType type = element.getNode().getElementType();
                if (type == SilverstripeTokenTypes.SS_CLOSED_BLOCK_START_STATEMENT
                    || type == SilverstripeTokenTypes.SS_BLOCK_START_STATEMENT
                    || type == SilverstripeTokenTypes.SS_CLOSED_BLOCK_END_STATEMENT
                    || type == SilverstripeTokenTypes.SS_IF_STATEMENT
                    || type == SilverstripeTokenTypes.SS_ELSE_IF_STATEMENT
                    || type == SilverstripeTokenTypes.SS_ELSE_STATEMENT
                ) {
                    myPairsToHighlight.add(getOpenBlockTagRanges(element.getParent()));
                }

                if (type == SilverstripeTokenTypes.SS_OPEN_BLOCK_STATEMENT
                    || type == SilverstripeTokenTypes.SS_INCLUDE_STATEMENT
                    || type == SilverstripeTokenTypes.SS_REQUIRE_STATEMENT
                    || type == SilverstripeTokenTypes.SS_TRANSLATION_STATEMENT
                ) {
                    myPairsToHighlight.add(getClosedBlockTagRanges(element));
                }

                element = element.getParent();
            }
        }
    }

    @NotNull
    private static ArrayList<TextRange> getOpenBlockTagRanges(PsiElement blockStatement) {
        ArrayList<TextRange> result = new ArrayList<>();
        PsiElement[] children = blockStatement.getChildren();
        if (children.length == 0) {
            return result;
        }

        // Scan each direct descendant for tokens we're interested in
        for (PsiElement child : children) {
            TokenSet tokens = TokenSet.create(
                SilverstripeTokenTypes.SS_START_KEYWORD,
                SilverstripeTokenTypes.SS_BLOCK_NAME,
                SilverstripeTokenTypes.SS_IF_KEYWORD,
                SilverstripeTokenTypes.SS_ELSE_IF_KEYWORD,
                SilverstripeTokenTypes.SS_ELSE_KEYWORD,
                SilverstripeTokenTypes.SS_CACHED_KEYWORD,
                SilverstripeTokenTypes.SS_END_KEYWORD
            );

            ASTNode[] nodes = child.getNode().getChildren(tokens);
            for (ASTNode node : nodes) {
                result.add(node.getTextRange());
            }
        }

        return result;
    }

    @NotNull
    private static ArrayList<TextRange> getClosedBlockTagRanges(PsiElement blockStatement) {
        ArrayList<TextRange> result = new ArrayList<>();

        TokenSet tokens = TokenSet.create(
            SilverstripeTokenTypes.SS_BLOCK_NAME,
            SilverstripeTokenTypes.SS_INCLUDE_KEYWORD,
            SilverstripeTokenTypes.SS_REQUIRE_KEYWORD,
            SilverstripeTokenTypes.SS_TRANSLATION_KEYWORD,
            SilverstripeTokenTypes.SS_SIMPLE_KEYWORD
        );

        ASTNode[] nodes = blockStatement.getNode().getChildren(tokens);
        for (ASTNode node : nodes) {
            result.add(node.getTextRange());
        }

        return result;
    }

    @Override
    public void doApplyInformationToEditor() {
        List<HighlightInfo> infos = getHighlights();
        UpdateHighlightersUtil.setHighlightersToSingleEditor(myProject, myEditor, 0, myFile.getTextLength(), infos, getColorsScheme(), getId());
    }

    public List<HighlightInfo> getHighlights() {
        clearLineMarkers(myEditor);

        int count = myPairsToHighlight.size();
        List<HighlightInfo> highlightInfos = new ArrayList<>(count * 2);
        MarkupModel markupModel = myEditor.getMarkupModel();

        Color[] baseColors = getBaseColors();
        Color[] colorsForEditor = count > 1 ? toColorsForEditor(baseColors) :
            new Color[] {myEditor.getColorsScheme().getAttributes(XmlHighlighterColors.MATCHED_TAG_NAME).getBackgroundColor()};
        Color[] colorsForLineMarkers = toColorsForLineMarkers(baseColors);

        List<RangeHighlighter> newHighlighters = new ArrayList<>();

        assert colorsForEditor.length > 0;

        for (int i = 0; i < count && i < baseColors.length; i++) {
            ArrayList<TextRange> tagsToHighlight = myPairsToHighlight.get(i);
            if (tagsToHighlight.isEmpty()) {
                continue;
            }

            Color color = colorsForEditor[i];
            if (color == null) {
                continue;
            }

            TextRange first = tagsToHighlight.get(0);
            TextRange last = null;
            for (TextRange tag : tagsToHighlight) {
                highlightInfos.add(createHighlightInfo(color, tag));
                last = tag;
            }

            int start = first.getStartOffset();
            int end = last.getEndOffset();

            Color lineMarkerColor = colorsForLineMarkers[i];
            if (count > 1 && lineMarkerColor != null && start != end) {
                RangeHighlighter highlighter = createHighlighter(markupModel, new TextRange(start, end), lineMarkerColor);
                newHighlighters.add(highlighter);
            }
        }

        myEditor.putUserData(TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY, newHighlighters);

        return highlightInfos;
    }

    private static void clearLineMarkers(Editor editor) {
        List<RangeHighlighter> oldHighlighters = editor.getUserData(TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY);

        if (oldHighlighters != null) {
            MarkupModelEx markupModel = (MarkupModelEx)editor.getMarkupModel();

            for (RangeHighlighter highlighter : oldHighlighters) {
                if (markupModel.containsHighlighter(highlighter)) {
                    highlighter.dispose();
                }
            }
            editor.putUserData(TAG_TREE_HIGHLIGHTERS_IN_EDITOR_KEY, null);
        }
    }

    static Color[] getBaseColors() {
        final ColorKey[] colorKeys = XmlTagTreeHighlightingColors.getColorKeys();
        final Color[] colors = new Color[colorKeys.length];

        final EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();

        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorsScheme.getColor(colorKeys[i]);
        }

        return colors;
    }

    private static Color[] toColorsForLineMarkers(Color[] baseColors) {
        Color[] colors = new Color[baseColors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = toLineMarkerColor(239, baseColors[i]);
        }
        return colors;
    }

    private Color[] toColorsForEditor(Color[] baseColors) {
        Color tagBackground = ((EditorEx)myEditor).getBackgroundColor();

        Color[] resultColors = new Color[baseColors.length];
        // todo: make configurable
        double transparency = WebEditorOptions.getInstance().getTagTreeHighlightingOpacity() * 0.01;

        for (int i = 0; i < resultColors.length; i++) {
            Color color = baseColors[i];

            Color color1 = color != null
                ? UIUtil.makeTransparent(color, tagBackground, transparency)
                : null;
            resultColors[i] = color1;
        }

        return resultColors;
    }

    static Color toLineMarkerColor(int gray, Color color) {
        //noinspection UseJBColor
        return color == null ? null : new Color(
            toLineMarkerColor(gray, color.getRed()),
            toLineMarkerColor(gray, color.getGreen()),
            toLineMarkerColor(gray, color.getBlue()));
    }

    private static int toLineMarkerColor(int gray, int color) {
        int value = (int)(gray * 0.6 + 0.32 * color);
        return value < 0 ? 0 : Math.min(value, 255);
    }

    @NotNull
    private static HighlightInfo createHighlightInfo(Color color, @NotNull TextRange range) {
        TextAttributes attributes = new TextAttributes(null, color, null, null, Font.PLAIN);
        return HighlightInfo.newHighlightInfo(TYPE).range(range).textAttributes(attributes).severity(HighlightSeverity.INFORMATION).createUnconditionally();
    }

    @NotNull
    private static RangeHighlighter createHighlighter(MarkupModel mm, @NotNull TextRange range, Color color) {
        RangeHighlighter highlighter =
            mm.addRangeHighlighter(null, range.getStartOffset(), range.getEndOffset(), 0, HighlighterTargetArea.LINES_IN_RANGE);

        highlighter.setLineMarkerRenderer((__, g, r) -> {
            g.setColor(color);
            g.fillRect(r.x - 1, r.y, 2, r.height);
        });
        return highlighter;
    }
}
