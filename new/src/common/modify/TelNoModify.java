package common.modify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelNoModify {

    private static String EN_MARK = "\\";
    private static String COMMA = ",";
    private static String BLANK = "";
    private static String PLUS = "+";
    private static String HYPHEN = "-";
    private static String SLASH = "/";
    private static String DOT = "\\.";
    private static String KOME = "\\*";
    private static String DOUBLE_QUOT = "\"";
    private static String SINGLE_QUOT = "\'";
    private static String HALF_SPACE = " ";
    private static String FULL_SPACE = "　";
    private static String KAKO_LEFT = "\\(";
    private static String KAKO_RIGHT = "\\)";
    private static String KAKO_LEFT_WITH_SHARP = "\\(#";
    private static String KAKO_LEFT_WITH_SPACE = " \\(";
    private static String KAKO_LEFT_WITH_HYPHEN = "-\\(";
    private static String KAKO_LEFT_WITH_PLUS = "\\(\\+";
    private static String KAKO_RIGHT_WITH_SPACE = "\\) ";
    /* (0)のみ */
    private static String KAKO_ZERO = "\\(0\\)";
    /* (0)前後に半角スペースあり */
    private static String KAKO_ZERO_WITH_DOUBESPACE = " \\(0\\) ";
    /* (0)前に半角スペースあり */
    private static String KAKO_ZERO_WITH_LEFTSPACE = " \\(0\\)";
    /* (0)前にハイフン、後ろに半角スペースあり */
    private static String KAKO_ZERO_WITH_HYPHEN_SPACE = "-\\(0\\) ";
    /* (0)前にハイフンあり */
    private static String KAKO_ZERO_WITH_LEFTHYPHEN = "-\\(0\\)";
    private static String ENCODE_SJIS = "SJIS";
    private static String EXTENSION = ".csv";
    private static String STR_OK = "OK";
    private static String STR_NG = "NG";
    private static String STR_EXT = "ext";
    private static String STR_X = "x";

    private static Set<String> chkSet = new HashSet<String>() {

        {
            add(PLUS);
            add(HYPHEN);
            add("0");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
            add("7");
            add("8");
            add("9");
        }
    };


    public static void main(String[] args) {
        // Scannerクラスのインスタンスを作成
        // 引数で標準入力System.inを指定する
        Scanner scanner = new Scanner(System.in);

        //入力された内容をインスタンスから取得
        String inputPath = scanner.nextLine();
        String outputPath = inputPath.replaceAll("Input", "Output");

        // 読み込むファイルの名前
        String inputFileName = scanner.nextLine();
        inputFileName = inputFileName + EXTENSION;

        // Scannerクラスのインスタンスをクローズ
        scanner.close();

        // 書き込むファイルの名前
        String outputFileName = "modified_" + inputFileName + EXTENSION;


        // ファイルオブジェクトの生成
        File inputFile = new File(inputPath + EN_MARK + inputFileName);
        File outputFile = new File(outputPath + EN_MARK + outputFileName);


          // 出力ストリームの生成
          FileOutputStream fos;
          OutputStreamWriter osw;
          PrintWriter pw = null;
        try {
            fos = new FileOutputStream(outputFile);
            osw = new OutputStreamWriter(fos,ENCODE_SJIS);
            pw = new PrintWriter(osw);

            // ファイルの読み取り
            excuteReadFile(inputFile, pw);
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }


        pw.close();
    }

    private static void excuteReadFile(File inputFile, PrintWriter pw) throws FileNotFoundException {

          // 入力ストリームの生成
          FileInputStream fis = new FileInputStream(inputFile);
          InputStreamReader isr;
          BufferedReader br = null;
          try {
              isr = new InputStreamReader(fis,ENCODE_SJIS);
              br = new BufferedReader(isr);

              // ファイルへの読み書き
              String line ="";
              String[] strArr = new String[0];
              String mStr = "";
              StringBuilder sb = new StringBuilder();

              while ( ( line = br.readLine()) != null ) {
                  //
                  strArr = line.split(COMMA);
//                  sb.append(DOUBLE_QUOT);
                  sb.append(strArr[0]);
//                  sb.append(DOUBLE_QUOT);
                  sb.append(COMMA);
//                  sb.append(DOUBLE_QUOT);
                  sb.append(strArr[1]);
//                  sb.append(DOUBLE_QUOT);
                  sb.append(COMMA);
                  mStr = modify(strArr[1]);
                  sb.append(SINGLE_QUOT);
                  sb.append(mStr);
//                  sb.append(DOUBLE_QUOT);
                  sb.append(COMMA);
                  if (checkTelNo(mStr)) {
                      sb.append(STR_OK);
                  } else {
                      sb.append(STR_NG);
                  }

                  pw.println(sb.toString());
                  sb.delete(0, sb.length());
              }
          } catch (IOException e) {
              // TODO 自動生成された catch ブロック
              e.printStackTrace();
          } finally {
              if (br != null) {
                  try {
                      br.close();
                  } catch (IOException e) {
                      // TODO 自動生成された catch ブロック
                      e.printStackTrace();
                  }
              }
          }
    }

    private static String modify(String value) {
        String result = "";
        // 括弧削除
        if (isExistKako(value)) {
            result = removeKako(value);
        } else {
            result = value;
        }

        // スラッシュ置換
        result = result.replaceAll(SLASH, HYPHEN);

        // 文字ext削除
        result = result.replaceAll(STR_EXT, BLANK);

        // 文字x削除
        result = result.replaceAll(STR_X, BLANK);

        // ※削除
        result = result.replaceAll(KOME, BLANK);

        // 点置換
        result = result.replaceAll(DOT, HYPHEN);

        // 半角スペース置換
        if (result.endsWith(HALF_SPACE)) {
            result = result.substring(0, result.length()-1);
        }
        result = result.replaceAll(HALF_SPACE, HYPHEN);

        return result;
    }

    private static String removeKako(String value) {
        if (isExistKakoWithZero(value)) {
            return removeKakoZero(value);
        }
        return removeKakoLeftRight(value);
    }

    private static String removeKakoLeftRight(String value) {
        String result = value;
        // 「(#」あり
        if (isKakoWithSomething(value, KAKO_LEFT_WITH_SHARP)) {
            // 「-」で置換、末の「)」を削除
            result = result.replaceAll(KAKO_LEFT_WITH_SHARP, HYPHEN);
            result = result.replaceAll(KAKO_RIGHT, BLANK);
        } else if (isKakoWithSomething(value, KAKO_LEFT_WITH_SPACE) && isKakoWithSomething(value, KAKO_RIGHT_WITH_SPACE)) {
            // 「(」の前と「)」の後ろに半角スペースあり
            // 「-」で置換
            result = result.replaceAll(KAKO_LEFT_WITH_SPACE, HYPHEN);
            result = result.replaceAll(KAKO_RIGHT_WITH_SPACE, HYPHEN);
        } else if (isKakoWithSomething(value, KAKO_LEFT_WITH_HYPHEN)) {
            // 「(」の前にハイフンあり
            // 「-」で置換
            result = result.replaceAll(KAKO_LEFT_WITH_HYPHEN, HYPHEN);
            result = result.replaceAll(KAKO_RIGHT, HYPHEN);
        } else if (isStartWithKako(value, KAKO_LEFT_WITH_PLUS, 2) && isKakoWithSomething(value, KAKO_RIGHT_WITH_SPACE)) {
            // 「(+」で始まり、「)」の後ろに半角スペースあり
            // 頭の「(+」を「+」で置換、「) 」を「-」で置換
            result = result.replaceAll(KAKO_LEFT_WITH_PLUS, PLUS);
            result = result.replaceAll(KAKO_RIGHT_WITH_SPACE, HYPHEN);
        } else if (isStartWithKako(value ,KAKO_LEFT_WITH_PLUS, 2)) {
            // 「(+」で始まり
            // 頭の「(+」を「+」で置換、「)」を「-」で置換
            result = result.replaceAll(KAKO_LEFT_WITH_PLUS, PLUS);
            result = result.replaceAll(KAKO_RIGHT, HYPHEN);
        } else if (isStartWithKako(value, KAKO_LEFT, 1)) {
            // 「(」で始まり
            // 頭の「(」をを削除、、「)」を「-」で置換
            result = result.replaceAll(KAKO_LEFT, BLANK);
            result = result.replaceAll(KAKO_RIGHT, HYPHEN);

        } else if (!isStartWithKako(value, KAKO_LEFT, 1)) {
            // 「(」で始まりではない、かつ、「(」の前と「)」の後ろに半角スペースなし
            // 「-」で置換
            result = result.replaceAll(KAKO_LEFT, HYPHEN);
            result = result.replaceAll(KAKO_RIGHT, HYPHEN);
        } else {
            // 処理なし
        }

        return result;
    }

    private static boolean isStartWithKako(String value, String regx, int idx) {
        Pattern p1 = Pattern.compile(regx);
        Matcher m1 = p1.matcher(value);
        if (m1.find()){
            String str = value.substring(0, idx);
            Matcher m2 = p1.matcher(str);
            if (m2.find()){
                return true;
            }
        }
        return false;
    }

    private static boolean isKakoWithSomething(String value, String regx) {
        Pattern p1 = Pattern.compile(regx);
        Matcher m1 = p1.matcher(value);
        if (m1.find()){
            return true;
        }
        return false;
    }

    private static String removeKakoZero(String value) {
        String result = value;
        // (0)前後に半角スペースあり
        result = result.replaceAll(KAKO_ZERO_WITH_DOUBESPACE, HYPHEN);
        // (0)前に半角スペースあり
        result = result.replaceAll(KAKO_ZERO_WITH_LEFTSPACE, HYPHEN);
        // (0)前にハイフン、後ろに半角スペースあり
        result = result.replaceAll(KAKO_ZERO_WITH_HYPHEN_SPACE, HYPHEN);
        // (0)前にハイフンあり
        result = result.replaceAll(KAKO_ZERO_WITH_LEFTHYPHEN, HYPHEN);
        // (0)のみ
        result = result.replaceAll(KAKO_ZERO, HYPHEN);

        return result;
    }

    private static boolean isExistKakoWithZero(String value) {
        // 「(0)」チェック
        Pattern p1 = Pattern.compile(KAKO_ZERO);
        Matcher m1 = p1.matcher(value);
        if (m1.find()){
            return true;
        }
        return false;
    }

    /**
     * 括弧存在チェック
     *
     * @param value
     * @return
     */
    private static boolean isExistKako(String value) {
        if (isKakoWithSomething(value, KAKO_LEFT)&& isKakoWithSomething(value, KAKO_RIGHT)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private static boolean checkTelNo(String value) {
        if (HALF_SPACE.equals(value) || FULL_SPACE.equals(value)) {
            return true;
        }

        int len = value.length();
        String subStr = "";
        for (int i = 0; i < len; i++) {
            subStr = value.substring(i, i+1);
            if (chkSet.contains(subStr)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }


}
