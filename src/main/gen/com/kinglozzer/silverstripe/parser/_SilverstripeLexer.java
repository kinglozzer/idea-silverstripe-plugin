/* The following code was generated by JFlex 1.7.0 tweaked for IntelliJ platform */

package com.kinglozzer.silverstripe.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.Stack;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.7.0
 * from the specification file <tt>Silverstripe.flex</tt>
 */
final class _SilverstripeLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int SS_INJECTION = 2;
  public static final int SS_LOOKUP = 4;
  public static final int SS_LOOKUP_STEP = 6;
  public static final int SS_LOOKUP_ARGUMENTS = 8;
  public static final int SS_BLOCK_START = 10;
  public static final int SS_BLOCK_STATEMENT = 12;
  public static final int SS_BAD_BLOCK_STATEMENT = 14;
  public static final int SS_IF_STATEMENT = 16;
  public static final int SS_INCLUDE_STATEMENT = 18;
  public static final int SS_TRANSLATION_STATEMENT = 20;
  public static final int SS_CACHED_STATEMENT = 22;
  public static final int SS_REQUIRE_STATEMENT = 24;
  public static final int SS_REQUIRE_CONTENT = 26;
  public static final int SS_INCLUDE_VARS = 28;
  public static final int SS_NAMED_VAR = 30;
  public static final int SS_COMMENT = 32;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6,  6,  7,  7, 
     8,  8,  9,  9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 
    16, 16
  };

  /** 
   * Translates characters to character classes
   * Chosen bits are [9, 6, 6]
   * Total runtime size is 1568 bytes
   */
  public static int ZZ_CMAP(int ch) {
    return ZZ_CMAP_A[(ZZ_CMAP_Y[ZZ_CMAP_Z[ch>>12]|((ch>>6)&0x3f)]<<6)|(ch&0x3f)];
  }

  /* The ZZ_CMAP_Z table has 272 entries */
  static final char ZZ_CMAP_Z[] = zzUnpackCMap(
    "\1\0\1\100\1\200\u010d\100");

  /* The ZZ_CMAP_Y table has 192 entries */
  static final char ZZ_CMAP_Y[] = zzUnpackCMap(
    "\1\0\1\1\1\2\175\3\1\4\77\3");

  /* The ZZ_CMAP_A table has 320 entries */
  static final char ZZ_CMAP_A[] = zzUnpackCMap(
    "\11\0\1\2\1\1\2\51\1\1\22\0\1\2\1\25\1\31\1\0\1\4\1\7\1\27\1\32\1\61\1\45"+
    "\2\0\1\52\1\44\1\47\1\42\12\3\2\0\1\6\1\24\1\10\2\0\2\5\1\56\1\64\5\5\1\60"+
    "\10\5\1\57\1\63\6\5\1\0\1\46\2\0\1\23\1\0\1\35\1\36\1\40\1\41\1\21\1\20\1"+
    "\37\1\17\1\15\1\53\1\5\1\11\1\55\1\26\1\12\1\13\1\43\1\33\1\22\1\16\1\34\1"+
    "\54\1\14\3\5\1\50\1\30\1\62\7\0\1\51\242\0\2\51\26\0");

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\20\0\1\1\1\2\1\3\3\4\1\5\1\6\2\5"+
    "\1\7\1\10\5\11\1\12\1\13\1\14\1\6\1\15"+
    "\1\11\4\16\2\11\1\17\1\20\1\21\14\22\1\23"+
    "\1\6\2\23\1\0\1\24\1\11\1\16\2\11\1\25"+
    "\2\11\1\26\4\11\1\27\1\30\3\11\1\31\1\11"+
    "\1\1\3\0\1\32\2\33\1\34\3\16\1\0\1\30"+
    "\1\0\1\35\1\36\2\0\1\37\12\0\1\40\1\41"+
    "\2\0\1\24\1\16\1\42\3\0\1\43\2\0\1\44"+
    "\6\0\1\1\1\33\3\16\1\45\13\0\1\41\3\23"+
    "\1\0\1\23\1\40\1\24\1\46\1\0\1\47\4\0"+
    "\1\1\1\33\1\50\1\51\1\52\1\0\1\53\4\0"+
    "\1\54\1\40\4\0\1\50\1\55\1\33\11\0\1\40"+
    "\2\0\1\33\4\0\1\56\3\0\1\57\2\0\1\33"+
    "\1\60\1\61\3\0\1\62\4\0\1\33\1\0\1\63"+
    "\3\0\1\64\1\0\1\65\3\0\1\66\5\0\1\67";

  private static int [] zzUnpackAction() {
    int [] result = new int[240];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\65\0\152\0\237\0\324\0\u0109\0\u013e\0\u0173"+
    "\0\u01a8\0\u01dd\0\u0212\0\u0247\0\u027c\0\u02b1\0\u02e6\0\u031b"+
    "\0\u0350\0\u0385\0\u03ba\0\u03ef\0\u0424\0\u0459\0\u048e\0\u04c3"+
    "\0\u04f8\0\u052d\0\u048e\0\u048e\0\u048e\0\u0562\0\u04f8\0\u0597"+
    "\0\u05cc\0\u048e\0\u048e\0\u0601\0\u04f8\0\u0636\0\u052d\0\u066b"+
    "\0\u06a0\0\u06d5\0\u070a\0\u073f\0\u0774\0\u048e\0\u048e\0\u048e"+
    "\0\u048e\0\u07a9\0\u07de\0\u0813\0\u0848\0\u087d\0\u08b2\0\u08e7"+
    "\0\u091c\0\u0951\0\u0986\0\u09bb\0\u09f0\0\u0a25\0\u0a5a\0\u0a8f"+
    "\0\u0ac4\0\u0af9\0\u0af9\0\u0b2e\0\u0b63\0\u0b98\0\u0bcd\0\u0c02"+
    "\0\u0c37\0\u0c02\0\u0c6c\0\u0ca1\0\u0cd6\0\u0d0b\0\u048e\0\u0d40"+
    "\0\u0d75\0\u0daa\0\u0ddf\0\u048e\0\u0e14\0\u0e49\0\u03ef\0\u0424"+
    "\0\u0459\0\u048e\0\u0e7e\0\u0eb3\0\u048e\0\u0ee8\0\u0f1d\0\u0f52"+
    "\0\u073f\0\u048e\0\u0774\0\u0f87\0\u048e\0\u0fbc\0\u0ff1\0\u048e"+
    "\0\u1026\0\u105b\0\u1090\0\u10c5\0\u10fa\0\u112f\0\u1164\0\u1199"+
    "\0\u11ce\0\u1203\0\u1238\0\u126d\0\u12a2\0\u12d7\0\u048e\0\u130c"+
    "\0\u048e\0\u1341\0\u0d75\0\u0c02\0\u1376\0\u0c6c\0\u13ab\0\u0c02"+
    "\0\u13e0\0\u1415\0\u144a\0\u147f\0\u14b4\0\u14e9\0\u151e\0\u1553"+
    "\0\u1588\0\u15bd\0\u15f2\0\u048e\0\u1627\0\u165c\0\u1691\0\u16c6"+
    "\0\u16fb\0\u1730\0\u1765\0\u179a\0\u17cf\0\u1804\0\u1839\0\u048e"+
    "\0\u186e\0\u18a3\0\u18d8\0\u190d\0\u126d\0\u1942\0\u066b\0\u13ab"+
    "\0\u1977\0\u048e\0\u19ac\0\u19e1\0\u1a16\0\u1a4b\0\u1a80\0\u1ab5"+
    "\0\u066b\0\u048e\0\u048e\0\u1aea\0\u1b1f\0\u1b54\0\u1b89\0\u1bbe"+
    "\0\u1bf3\0\u048e\0\u18d8\0\u1c28\0\u1c5d\0\u1c92\0\u1cc7\0\u048e"+
    "\0\u048e\0\u1cfc\0\u1d31\0\u1d66\0\u1d9b\0\u1dd0\0\u1e05\0\u1e3a"+
    "\0\u1e6f\0\u1ea4\0\u1ed9\0\u1c5d\0\u1f0e\0\u1f43\0\u1f78\0\u1fad"+
    "\0\u1fe2\0\u2017\0\u204c\0\u048e\0\u2081\0\u20b6\0\u20eb\0\u048e"+
    "\0\u2120\0\u2155\0\u218a\0\u048e\0\u048e\0\u21bf\0\u21f4\0\u2229"+
    "\0\u048e\0\u225e\0\u2293\0\u22c8\0\u22fd\0\u2332\0\u2367\0\u048e"+
    "\0\u239c\0\u23d1\0\u2406\0\u0e7e\0\u243b\0\u048e\0\u2470\0\u24a5"+
    "\0\u24da\0\u048e\0\u250f\0\u2544\0\u2579\0\u25ae\0\u25e3\0\u048e";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[240];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\4\22\1\23\1\22\1\24\37\22\1\25\1\22\1\26"+
    "\14\22\1\27\1\30\1\31\1\27\1\32\43\27\1\33"+
    "\1\0\10\27\1\34\2\27\1\35\1\36\1\37\1\35"+
    "\1\40\2\35\1\41\37\35\1\42\1\35\1\0\7\35"+
    "\1\43\4\35\1\30\1\37\2\35\1\44\3\35\13\44"+
    "\2\35\1\44\4\35\7\44\1\35\1\44\5\35\1\0"+
    "\1\35\6\44\2\35\2\44\1\35\1\36\1\45\1\46"+
    "\1\47\1\50\3\35\5\50\1\51\1\50\1\52\3\50"+
    "\2\35\1\53\2\35\1\54\1\55\7\50\1\35\1\50"+
    "\1\35\1\56\1\35\1\57\1\35\1\0\1\60\6\50"+
    "\2\35\2\50\1\61\1\62\1\45\3\61\1\63\1\64"+
    "\1\61\1\65\2\61\1\66\1\67\3\61\1\70\11\61"+
    "\1\71\2\61\1\72\1\61\1\73\3\61\1\74\4\61"+
    "\1\0\13\61\1\35\1\36\1\45\1\35\1\47\1\50"+
    "\1\35\1\41\1\35\13\50\2\35\1\50\4\35\7\50"+
    "\1\35\1\50\5\35\1\0\1\35\6\50\2\35\2\50"+
    "\1\75\1\76\4\75\1\77\1\100\41\75\1\101\13\75"+
    "\1\35\1\36\1\45\1\46\1\47\1\50\1\102\1\41"+
    "\1\102\13\50\1\102\1\103\1\104\1\105\1\106\1\54"+
    "\1\55\7\50\1\35\1\50\5\35\1\0\1\35\6\50"+
    "\2\35\2\50\1\35\1\36\1\45\2\35\1\107\1\35"+
    "\1\41\1\35\13\107\2\35\1\107\4\35\12\107\1\35"+
    "\1\107\2\35\1\0\1\35\6\107\2\35\2\107\1\35"+
    "\1\36\1\45\2\35\1\110\1\35\1\41\1\35\4\110"+
    "\1\111\1\112\5\110\2\35\1\110\2\35\1\54\1\55"+
    "\7\110\1\35\1\110\2\35\1\113\2\35\1\0\1\35"+
    "\6\110\2\35\2\110\1\35\1\36\1\45\1\35\1\47"+
    "\1\50\1\35\1\41\1\35\13\50\2\35\1\50\2\35"+
    "\1\54\1\55\7\50\1\35\1\50\5\35\1\0\1\60"+
    "\6\50\2\35\2\50\1\35\1\36\1\45\13\35\1\114"+
    "\21\35\1\115\10\35\1\0\1\35\1\116\5\35\1\117"+
    "\3\35\31\120\1\54\1\55\12\120\1\56\17\120\1\35"+
    "\1\36\1\45\2\35\1\121\3\35\13\121\2\35\1\121"+
    "\4\35\7\121\1\35\1\121\5\35\1\0\1\60\6\121"+
    "\2\35\2\121\1\35\1\36\1\37\1\46\1\47\11\35"+
    "\1\122\1\35\1\123\3\35\1\124\1\35\1\125\2\35"+
    "\1\54\1\55\16\35\1\0\13\35\44\21\1\126\20\21"+
    "\4\22\1\23\1\22\1\127\37\22\1\130\1\22\1\131"+
    "\21\22\1\0\3\22\13\0\2\22\1\0\4\22\7\0"+
    "\1\22\1\0\7\22\6\0\2\22\2\0\7\22\1\132"+
    "\56\22\1\0\47\22\1\0\17\22\1\23\60\22\66\0"+
    "\1\30\65\0\1\45\67\0\1\50\3\0\13\50\2\0"+
    "\1\50\4\0\7\50\1\0\1\50\7\0\6\50\2\0"+
    "\2\50\1\0\1\36\70\0\1\133\3\0\13\133\2\0"+
    "\1\133\4\0\7\133\1\0\1\133\7\0\6\133\2\0"+
    "\1\134\1\133\10\0\1\135\57\0\1\44\1\0\1\44"+
    "\3\0\13\44\2\0\1\44\4\0\7\44\1\0\1\44"+
    "\7\0\6\44\2\0\2\44\3\0\1\46\64\0\1\50"+
    "\1\0\1\50\3\0\13\50\2\0\1\50\4\0\7\50"+
    "\1\0\1\50\7\0\6\50\2\0\2\50\3\0\1\50"+
    "\1\0\1\50\3\0\13\50\2\0\1\50\4\0\1\136"+
    "\6\50\1\0\1\50\7\0\6\50\2\0\2\50\3\0"+
    "\1\50\1\0\1\50\3\0\13\50\2\0\1\50\4\0"+
    "\2\50\1\137\4\50\1\0\1\50\7\0\6\50\2\0"+
    "\2\50\3\0\1\50\1\0\1\50\3\0\13\50\2\0"+
    "\1\50\4\0\1\50\1\140\5\50\1\0\1\50\7\0"+
    "\6\50\2\0\2\50\31\141\1\142\33\141\32\143\1\142"+
    "\32\143\1\0\1\62\72\0\1\144\65\0\1\145\66\0"+
    "\1\146\67\0\1\147\67\0\1\150\5\0\1\151\47\0"+
    "\1\152\14\0\1\153\57\0\1\154\100\0\1\155\64\0"+
    "\1\156\73\0\1\157\20\0\1\75\1\160\4\75\1\77"+
    "\1\100\41\75\1\101\13\75\1\160\1\76\4\160\1\161"+
    "\1\162\55\160\1\75\1\160\4\75\1\77\1\163\41\75"+
    "\1\101\14\75\1\160\4\75\1\77\1\100\1\164\40\75"+
    "\1\101\13\75\1\101\1\160\4\101\1\165\1\166\55\101"+
    "\24\0\1\167\43\0\1\50\1\0\1\50\3\0\1\50"+
    "\1\170\11\50\2\0\1\50\4\0\7\50\1\0\1\50"+
    "\7\0\6\50\2\0\2\50\27\0\1\171\65\0\1\171"+
    "\41\0\1\107\3\0\13\107\2\0\1\107\4\0\12\107"+
    "\1\0\1\107\4\0\6\107\2\0\2\107\2\0\1\172"+
    "\1\173\1\0\1\174\3\0\13\174\1\175\1\0\1\174"+
    "\4\0\7\174\1\0\1\174\2\0\1\176\1\177\3\0"+
    "\6\174\2\0\2\174\2\0\1\172\1\173\1\0\1\174"+
    "\3\0\11\174\1\200\1\174\1\175\1\0\1\174\4\0"+
    "\7\174\1\0\1\174\2\0\1\176\1\177\3\0\6\174"+
    "\2\0\2\174\5\0\1\176\3\0\13\176\2\0\1\176"+
    "\4\0\7\176\1\0\1\176\2\0\1\176\1\177\3\0"+
    "\6\176\2\0\2\176\17\0\1\201\67\0\1\202\77\0"+
    "\1\203\27\0\31\120\2\0\12\120\1\0\17\120\2\0"+
    "\1\172\1\173\1\0\1\173\3\0\13\173\1\175\1\0"+
    "\1\173\4\0\7\173\1\0\1\173\7\0\6\173\2\0"+
    "\2\173\33\0\1\204\66\0\1\205\63\0\1\206\30\0"+
    "\44\21\1\207\20\21\3\0\1\133\1\0\1\133\3\0"+
    "\13\133\2\0\1\133\4\0\7\133\1\0\1\133\7\0"+
    "\6\133\2\0\2\133\3\0\1\133\1\0\1\133\3\0"+
    "\6\133\1\210\4\133\2\0\1\133\4\0\7\133\1\0"+
    "\1\133\7\0\6\133\2\0\2\133\3\0\1\50\1\0"+
    "\1\50\3\0\13\50\2\0\1\50\4\0\1\50\1\211"+
    "\5\50\1\0\1\50\7\0\6\50\2\0\2\50\3\0"+
    "\1\50\1\0\1\50\3\0\1\212\12\50\2\0\1\50"+
    "\4\0\7\50\1\0\1\50\7\0\6\50\2\0\2\50"+
    "\3\0\1\50\1\0\1\50\3\0\1\213\12\50\2\0"+
    "\1\50\4\0\7\50\1\0\1\50\7\0\6\50\2\0"+
    "\2\50\16\0\1\214\25\0\1\215\32\0\1\216\70\0"+
    "\1\217\106\0\1\220\46\0\1\221\103\0\1\222\66\0"+
    "\1\223\43\0\1\224\102\0\1\225\33\0\1\226\55\0"+
    "\6\160\1\161\1\162\63\160\1\161\1\227\63\160\1\161"+
    "\1\162\1\230\54\160\1\231\1\0\4\231\1\232\1\233"+
    "\1\164\40\231\1\234\13\231\1\235\1\0\47\235\1\0"+
    "\13\235\1\101\1\160\4\101\1\165\1\236\56\101\1\160"+
    "\4\101\1\165\1\166\1\230\54\101\3\0\1\50\1\0"+
    "\1\50\3\0\5\50\1\237\5\50\2\0\1\50\4\0"+
    "\7\50\1\0\1\50\7\0\6\50\2\0\2\50\2\0"+
    "\1\172\21\0\1\175\42\0\1\175\67\0\1\240\3\0"+
    "\13\240\2\0\1\240\4\0\7\240\1\0\1\240\2\0"+
    "\1\240\4\0\6\240\2\0\2\240\21\0\1\241\65\0"+
    "\1\242\116\0\1\243\44\0\1\244\41\0\1\245\64\0"+
    "\1\246\53\0\7\21\1\247\34\21\1\207\20\21\3\0"+
    "\1\133\1\0\1\133\3\0\10\133\1\250\2\133\2\0"+
    "\1\133\4\0\7\133\1\0\1\133\7\0\6\133\2\0"+
    "\2\133\3\0\1\50\1\0\1\50\3\0\10\50\1\251"+
    "\2\50\2\0\1\50\4\0\7\50\1\0\1\50\7\0"+
    "\6\50\2\0\2\50\3\0\1\50\1\0\1\50\3\0"+
    "\11\50\1\211\1\50\2\0\1\50\4\0\7\50\1\0"+
    "\1\50\7\0\6\50\2\0\2\50\3\0\1\50\1\0"+
    "\1\50\3\0\1\251\12\50\2\0\1\50\4\0\7\50"+
    "\1\0\1\50\7\0\6\50\2\0\2\50\44\0\1\252"+
    "\33\0\1\253\70\0\1\253\56\0\1\254\74\0\1\255"+
    "\66\0\1\256\75\0\1\257\51\0\1\260\62\0\1\261"+
    "\55\0\1\262\64\0\1\230\54\0\1\231\1\0\4\231"+
    "\1\232\1\233\41\231\1\234\14\231\1\0\4\231\1\232"+
    "\1\263\41\231\1\234\14\231\1\0\4\231\1\232\1\233"+
    "\1\235\40\231\1\234\13\231\1\234\1\0\4\234\1\264"+
    "\1\265\56\234\1\0\4\234\1\264\1\265\1\230\54\234"+
    "\55\0\1\266\44\0\1\267\50\0\1\270\65\0\1\244"+
    "\53\0\1\270\53\0\10\21\1\271\33\21\1\126\20\21"+
    "\3\0\1\133\1\0\1\133\3\0\13\133\2\0\1\133"+
    "\4\0\7\133\1\0\1\133\7\0\2\133\1\272\3\133"+
    "\2\0\2\133\34\0\1\273\53\0\1\274\52\0\1\275"+
    "\2\0\1\276\1\277\22\0\1\300\41\0\1\301\72\0"+
    "\1\302\62\0\1\303\43\0\1\234\1\0\4\234\1\264"+
    "\1\304\56\234\1\0\4\234\1\264\1\265\1\0\54\234"+
    "\21\0\1\305\65\0\1\306\45\0\1\133\1\0\1\133"+
    "\3\0\10\133\1\307\2\133\2\0\1\133\4\0\7\133"+
    "\1\0\1\133\7\0\6\133\2\0\2\133\41\0\1\310"+
    "\40\0\1\311\61\0\1\312\67\0\1\313\67\0\1\314"+
    "\101\0\1\315\62\0\1\316\47\0\1\317\107\0\1\320"+
    "\64\0\1\321\63\0\1\322\27\0\1\133\1\0\1\133"+
    "\3\0\13\133\2\0\1\133\4\0\7\133\1\0\1\133"+
    "\7\0\6\133\2\0\1\133\1\323\21\0\1\324\63\0"+
    "\1\325\56\0\1\326\70\0\1\327\106\0\1\330\45\0"+
    "\1\331\100\0\1\332\105\0\1\333\1\0\1\334\37\0"+
    "\1\335\34\0\1\133\1\0\1\133\3\0\4\133\1\336"+
    "\6\133\2\0\1\133\4\0\7\133\1\0\1\133\7\0"+
    "\6\133\2\0\2\133\13\0\1\314\70\0\1\314\64\0"+
    "\1\337\104\0\1\340\104\0\1\341\42\0\1\342\44\0"+
    "\1\343\52\0\1\133\1\0\1\133\3\0\13\133\2\0"+
    "\1\133\4\0\1\344\6\133\1\0\1\133\7\0\6\133"+
    "\2\0\2\133\21\0\1\345\122\0\1\346\61\0\1\347"+
    "\23\0\1\350\112\0\1\314\60\0\1\351\45\0\1\352"+
    "\70\0\1\353\102\0\1\354\57\0\1\355\46\0\1\356"+
    "\62\0\1\357\67\0\1\360\46\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[9752];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\20\0\6\1\1\11\3\1\3\11\4\1\2\11\12\1"+
    "\4\11\17\1\1\0\15\1\1\11\4\1\1\11\2\1"+
    "\3\0\1\11\2\1\1\11\3\1\1\0\1\11\1\0"+
    "\1\1\1\11\2\0\1\11\12\0\2\1\2\0\1\11"+
    "\1\1\1\11\3\0\1\1\2\0\1\1\6\0\5\1"+
    "\1\11\13\0\1\11\3\1\1\0\4\1\1\0\1\11"+
    "\4\0\3\1\2\11\1\0\1\1\4\0\1\11\1\1"+
    "\4\0\2\11\1\1\11\0\1\1\2\0\1\1\4\0"+
    "\1\11\3\0\1\11\2\0\1\1\2\11\3\0\1\11"+
    "\4\0\1\1\1\0\1\11\3\0\1\1\1\0\1\11"+
    "\3\0\1\11\5\0\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[240];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
    public _SilverstripeLexer() {
        this((java.io.Reader)null);
    }

    private void resetAll() {
    }
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


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  _SilverstripeLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    int size = 0;
    for (int i = 0, length = packed.length(); i < length; i += 2) {
      size += packed.charAt(i);
    }
    char[] map = new char[size];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < packed.length()) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      {@code false}, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position {@code pos} from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + ZZ_CMAP(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { return SilverstripeTokenTypes.SS_COMMENT;
            } 
            // fall through
          case 56: break;
          case 2: 
            { // Lex whitespace separately - the formatter needs this... no idea why
    if (yytext().toString().trim().length() == 0) {
        return TokenType.WHITE_SPACE;
    }
    return SilverstripeTokenTypes.SS_TEXT;
            } 
            // fall through
          case 57: break;
          case 3: 
            { // Backtrack over the {$ characters
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
            // fall through
          case 58: break;
          case 4: 
            { return TokenType.BAD_CHARACTER;
            } 
            // fall through
          case 59: break;
          case 5: 
            { yypopstate(); return SilverstripeTokenTypes.SS_TEXT;
            } 
            // fall through
          case 60: break;
          case 6: 
            { return TokenType.WHITE_SPACE;
            } 
            // fall through
          case 61: break;
          case 7: 
            { return SilverstripeTokenTypes.SS_LEFT_BRACE;
            } 
            // fall through
          case 62: break;
          case 8: 
            { yypopstate(); return SilverstripeTokenTypes.SS_RIGHT_BRACE;
            } 
            // fall through
          case 63: break;
          case 9: 
            { yypushback(yylength()); yypopstate();
            } 
            // fall through
          case 64: break;
          case 10: 
            { yypushstate(SS_LOOKUP_STEP); return SilverstripeTokenTypes.SS_DOT;
            } 
            // fall through
          case 65: break;
          case 11: 
            { yypushstate(SS_LOOKUP_ARGUMENTS); return SilverstripeTokenTypes.SS_LEFT_PARENTHESIS;
            } 
            // fall through
          case 66: break;
          case 12: 
            { yypopstate(); return SilverstripeTokenTypes.SS_IDENTIFIER;
            } 
            // fall through
          case 67: break;
          case 13: 
            { return SilverstripeTokenTypes.SS_NUMBER;
            } 
            // fall through
          case 68: break;
          case 14: 
            { yypushstate(SS_LOOKUP); return SilverstripeTokenTypes.SS_LOOKUP;
            } 
            // fall through
          case 69: break;
          case 15: 
            { yypopstate(); return SilverstripeTokenTypes.SS_RIGHT_PARENTHESIS;
            } 
            // fall through
          case 70: break;
          case 16: 
            { return SilverstripeTokenTypes.SS_DOT;
            } 
            // fall through
          case 71: break;
          case 17: 
            { return SilverstripeTokenTypes.SS_COMMA;
            } 
            // fall through
          case 72: break;
          case 18: 
            { yypushback(yylength()); yypushstate(SS_BAD_BLOCK_STATEMENT);
            } 
            // fall through
          case 73: break;
          case 19: 
            { yycleanstates(); return SilverstripeTokenTypes.SS_UNFINISHED_BLOCK_STATEMENT;
            } 
            // fall through
          case 74: break;
          case 20: 
            { return SilverstripeTokenTypes.SS_COMPARISON_OPERATOR;
            } 
            // fall through
          case 75: break;
          case 21: 
            { yypushstate(SS_INCLUDE_VARS); return SilverstripeTokenTypes.SS_INCLUDE_FILE;
            } 
            // fall through
          case 76: break;
          case 22: 
            { return SilverstripeTokenTypes.SS_TRANSLATION_KEYWORD;
            } 
            // fall through
          case 77: break;
          case 23: 
            { yypushstate(SS_REQUIRE_CONTENT); return SilverstripeTokenTypes.SS_LEFT_PARENTHESIS;
            } 
            // fall through
          case 78: break;
          case 24: 
            { return SilverstripeTokenTypes.SS_STRING;
            } 
            // fall through
          case 79: break;
          case 25: 
            { return SilverstripeTokenTypes.SS_EQUALS;
            } 
            // fall through
          case 80: break;
          case 26: 
            { // Backtrack over the <% characters
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
            // fall through
          case 81: break;
          case 27: 
            { return SilverstripeTokenTypes.SS_LOOKUP;
            } 
            // fall through
          case 82: break;
          case 28: 
            { yycleanstates(); return SilverstripeTokenTypes.SS_BLOCK_END;
            } 
            // fall through
          case 83: break;
          case 29: 
            { return SilverstripeTokenTypes.SS_BLOCK_START;
            } 
            // fall through
          case 84: break;
          case 30: 
            { yypopstate(); return SilverstripeTokenTypes.SS_BLOCK_END;
            } 
            // fall through
          case 85: break;
          case 31: 
            { yypushstate(SS_IF_STATEMENT); return SilverstripeTokenTypes.SS_IF_KEYWORD;
            } 
            // fall through
          case 86: break;
          case 32: 
            { // Backtrack until we've passed back over the <% characters of the new block
        while (yylength() > 0 && (
            yytext().subSequence(yylength() - 1, yylength()).toString().equals("%")
            || yytext().subSequence(yylength() - 1, yylength()).toString().equals("<")
        )) {
            yypushback(1);
        }

        yycleanstates(); // Reset state to resume lexing
        return SilverstripeTokenTypes.SS_UNFINISHED_BLOCK_STATEMENT;
            } 
            // fall through
          case 87: break;
          case 33: 
            { yycleanstates(); return SilverstripeTokenTypes.SS_BAD_BLOCK_STATEMENT;
            } 
            // fall through
          case 88: break;
          case 34: 
            { return SilverstripeTokenTypes.SS_AND_OR_OPERATOR;
            } 
            // fall through
          case 89: break;
          case 35: 
            { yypushback(1); yypushstate(SS_NAMED_VAR); return SilverstripeTokenTypes.SS_NAMED_ARGUMENT_NAME;
            } 
            // fall through
          case 90: break;
          case 36: 
            { return SilverstripeTokenTypes.SS_IS_KEYWORD;
            } 
            // fall through
          case 91: break;
          case 37: 
            { yypushstate(SS_TRANSLATION_STATEMENT); yypushback(1); return SilverstripeTokenTypes.SS_BLOCK_START;
            } 
            // fall through
          case 92: break;
          case 38: 
            { return SilverstripeTokenTypes.SS_TRANSLATION_IDENTIFIER;
            } 
            // fall through
          case 93: break;
          case 39: 
            { return SilverstripeTokenTypes.SS_REQUIRE_CSS;
            } 
            // fall through
          case 94: break;
          case 40: 
            { return SilverstripeTokenTypes.SS_PRIMITIVE;
            } 
            // fall through
          case 95: break;
          case 41: 
            { yypushstate(SS_COMMENT); return SilverstripeTokenTypes.SS_COMMENT_START;
            } 
            // fall through
          case 96: break;
          case 42: 
            { yypushstate(SS_BLOCK_STATEMENT); return SilverstripeTokenTypes.SS_START_KEYWORD;
            } 
            // fall through
          case 97: break;
          case 43: 
            { return SilverstripeTokenTypes.SS_ELSE_KEYWORD;
            } 
            // fall through
          case 98: break;
          case 44: 
            { yypopstate(); return SilverstripeTokenTypes.SS_COMMENT_END;
            } 
            // fall through
          case 99: break;
          case 45: 
            { yypopstate(); yypushback(4); return SilverstripeTokenTypes.SS_COMMENT;
            } 
            // fall through
          case 100: break;
          case 46: 
            { return SilverstripeTokenTypes.SS_END_KEYWORD;
            } 
            // fall through
          case 101: break;
          case 47: 
            { yypushstate(SS_CACHED_STATEMENT); return SilverstripeTokenTypes.SS_CACHED_KEYWORD;
            } 
            // fall through
          case 102: break;
          case 48: 
            { yypushstate(SS_INCLUDE_STATEMENT); return SilverstripeTokenTypes.SS_INCLUDE_KEYWORD;
            } 
            // fall through
          case 103: break;
          case 49: 
            { yypushstate(SS_IF_STATEMENT); return SilverstripeTokenTypes.SS_ELSE_IF_KEYWORD;
            } 
            // fall through
          case 104: break;
          case 50: 
            { yypushstate(SS_REQUIRE_STATEMENT); return SilverstripeTokenTypes.SS_REQUIRE_KEYWORD;
            } 
            // fall through
          case 105: break;
          case 51: 
            { yypushstate(SS_BLOCK_STATEMENT); return SilverstripeTokenTypes.SS_SIMPLE_KEYWORD;
            } 
            // fall through
          case 106: break;
          case 52: 
            { return SilverstripeTokenTypes.SS_THEME_DIR;
            } 
            // fall through
          case 107: break;
          case 53: 
            { return SilverstripeTokenTypes.SS_REQUIRE_THEMED_CSS;
            } 
            // fall through
          case 108: break;
          case 54: 
            { return SilverstripeTokenTypes.SS_REQUIRE_JS;
            } 
            // fall through
          case 109: break;
          case 55: 
            { return SilverstripeTokenTypes.SS_REQUIRE_THEMED_JS;
            } 
            // fall through
          case 110: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
