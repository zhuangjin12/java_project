package common.check;

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

public class TelNoCheck {

    private static String EN_MARK = "\\";
    private static String COMMA = ",";
    private static String PLUS = "+";
    private static String HYPHEN = "-";
    private static String HALF_SPACE = " ";
    private static String FULL_SPACE = "　";
    private static String STR_OK = "OK";
    private static String STR_NG = "NG";
    private static String ENCODE_SJIS = "SJIS";
    private static String EXTENSION = ".csv";

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
        String outputFileName = "check_" + inputFileName;


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
              while ( ( line = br.readLine()) != null ) {
                  if (line.endsWith(COMMA)) {
                      line = line + COMMA + STR_OK;
//                      continue;
                  } else {
                      //
                      strArr = line.split(COMMA);
                      if (checkTelNo(strArr[1])) {
                          line = line + COMMA + STR_OK;
//                          continue;
                      } else {

                          line = line + COMMA + STR_NG;
                      }
                  }

                  pw.println(line);
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
