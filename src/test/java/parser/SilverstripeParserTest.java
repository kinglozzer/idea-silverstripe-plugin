package parser;

import com.intellij.lang.ParserDefinition;
import com.intellij.testFramework.ParsingTestCase;
import com.intellij.util.ArrayUtil;
import com.kinglozzer.silverstripe.parser.SilverstripeParserDefinition;
import org.jetbrains.annotations.NotNull;

public class SilverstripeParserTest extends ParsingTestCase {
    public SilverstripeParserTest() {
        super("parser", "ss", new SilverstripeParserDefinition());
    }

    public SilverstripeParserTest(ParserDefinition @NotNull ... additionalDefinitions) {
        super("parser", "ss", ArrayUtil.prepend(new SilverstripeParserDefinition(), additionalDefinitions));
    }

    @Override
    protected boolean checkAllPsiRoots() {
        return false;
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testLookup() {
        doTest(true);
    }

    public void testLookupWithArguments() {
        doTest(true);
    }

    public void testLookupWithSteps() {
        doTest(true);
    }

    public void testIfStatement() {
        doTest(true);
    }

    public void testLoopStatement() {
        doTest(true);
    }

    public void testWithStatement() {
        doTest(true);
    }

    public void testInclude() {
        doTest(true);
    }

    public void testIncludeWithNamespace() {
        doTest(true);
    }

    public void testIncludeWithArguments() {
        doTest(true);
    }

    public void testRequire() {
        doTest(true);
    }

    public void testTranslation() {
        doTest(true);
    }

    public void testComment() {
        doTest(true);
    }

    public void testEmptyTemplateWithDollar() {
        doTest(true);
    }

    public void testSampleTemplate() {
        doTest(true);
    }

    public void testGenericBlocks() {
        doTest(true);
    }

    public void testIssue11() {
        doTest(true);
    }

    public void testIssue12() {
        doTest(true);
    }

    public void testIssue21() {
        doTest(true);
    }
}
