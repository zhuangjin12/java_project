package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcelFiles {

    private static final int HEAD_ROW_1 = 1;
    private static final int HEAD_ROW_2 = 2;
    private static final int HEAD_ROW_3 = 3;
    private static final int HEAD_ROW_6 = 6;
    private static final int DETAIL_ROW_START = 8;
    private static final int DETAIL_ROW_END = 17;
    private static final int HEAD_COLUMN_2 = 2;
    private static final int HEAD_COLUMN_6 = 6;
    private static final int HEAD_COLUMN_7 = 7;
    private static final int DETAIL_COLUMN_0 = 0;
    private static final int DETAIL_COLUMN_1 = 1;
    private static final int DETAIL_COLUMN_2 = 2;
    private static final int DETAIL_COLUMN_3 = 3;
    private static final int DETAIL_COLUMN_4 = 4;
    private static final int DETAIL_COLUMN_6 = 6;
    /** コマ */
    public static final String COMA = ",";
    /** スラッシュ */
    public static final String SLASH = "/";

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        // 入力パス D:\test\excelRead
        String filePath = sc.next();
        sc.close();

        // Excelファイルの読み取り
        ReadExcelFiles re = new ReadExcelFiles();
        List<String> contentList = re.readFolder(new File(filePath));

        // CSVファイルの出力
        re.outputCsv(filePath, contentList);
    }

    /**
     * フォルダの読み込み
     * @param dir フォルダパス
     * @throws IOException IO例外
     */
    private List<String> readFolder(File dir) throws IOException {
        List<String> resultList = new ArrayList<>();

        // フォルダとファイル一覧の取得
        File[] files = dir.listFiles();
        if (files == null) {
            return resultList;
        }

        for (File file : files) {
            if (!file.exists()) {
                continue;
            } else if (file.isDirectory()) {
                readFolder(file);
            } else if (file.isFile()) {
                // .xlsxの以外ファイルを除外する
                if (!file.getName().endsWith(".xlsx")) {
                    continue;
                }
                // ファイル読み取り
                resultList.addAll(readExcelFile(file));
            }
        }
        return resultList;
    }

    /**
     * CSVファイル出力
     *
     * @param outputPath 出力パス
     * @param contentList 出力コンテンツ
     */
    private void outputCsv(String outputPath, List<String> contentList) {
        // 出力ファイル名
        String outputFileName = "salesList_ja.csv";
        File fileOut = null;
        PrintWriter p_writer = null;
        // 出力ファイルの作成
        fileOut = new File(outputPath + SLASH + outputFileName);
        try {
            p_writer = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileOut),
                            "UTF-8")));

            for (String rowContent : contentList) {
                p_writer.println(rowContent);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        p_writer.close();
    }

    /**
     * Excelファイル読み取り
     *
     * @param stream インプットファイルストリーム
     * @return 読み取り後コンテンツリスト
     * @throws IOException IO例外
     */
    private List<String> readExcelFile(File file) throws IOException {

        InputStream is = new FileInputStream(file.getPath());
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        List<String> resultList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        String head = "";
        XSSFSheet xssfSheet = null;
        XSSFRow row = null;
        for (int i = 0; i < xssfWorkbook.getNumberOfSheets(); i++) {
            // シートの取得
            xssfSheet = xssfWorkbook.getSheetAt(i);

            // Head行取得
            head = getHeadContent(xssfSheet);

            // 明晰行取得
            for (int rowIdx = DETAIL_ROW_START; rowIdx <= DETAIL_ROW_END; rowIdx++) {
                // 明晰行
                row = xssfSheet.getRow(rowIdx);
                // 商品编号 = null and 品项 = null,
                if (isEmpty(row.getCell(DETAIL_COLUMN_1).getStringCellValue())
                        && isEmpty(row.getCell(DETAIL_COLUMN_2).getStringCellValue())) {
                    continue;
                }

                sb.append(head);

                // No
                sb.append(row.getCell(DETAIL_COLUMN_0).getRawValue()).append(COMA);
                // 商品编号
                sb.append(row.getCell(DETAIL_COLUMN_1).getStringCellValue()).append(COMA);
                // 品项
                sb.append(row.getCell(DETAIL_COLUMN_2).getStringCellValue()).append(COMA);
                // 数量
                sb.append(row.getCell(DETAIL_COLUMN_3).getRawValue()).append(COMA);
                // 单价
                sb.append(row.getCell(DETAIL_COLUMN_4).getRawValue()).append(COMA);
                // 金额
                sb.append(Long.parseLong(row.getCell(DETAIL_COLUMN_3).getRawValue()) * Long.parseLong(row.getCell(DETAIL_COLUMN_4).getRawValue())).append(COMA);
                // 备注
                sb.append(row.getCell(DETAIL_COLUMN_6).getStringCellValue());

                // List追加
                resultList.add(sb.toString());

                sb.setLength(0);
            }
        }

        xssfWorkbook.close();
        if (is != null) {
            is.close();
        }

        return resultList;
    }

    /**
     * ヘッダ内容取得
     *
     * @param xssfSheet シート
     * @return ヘッダ内容
     */
    private String getHeadContent(XSSFSheet xssfSheet) {
        StringBuilder sb = new StringBuilder();
        // Head2行目
        XSSFRow row = xssfSheet.getRow(HEAD_ROW_1);
        // 发票No
        String saleNo = row.getCell(HEAD_COLUMN_6).getRawValue();
        sb.append(saleNo).append(COMA);

        // Head3行目
        row = xssfSheet.getRow(HEAD_ROW_2);
        // 日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String saleDate = sdf.format(row.getCell(HEAD_COLUMN_6).getDateCellValue());
        sb.append(saleDate).append(COMA);

        // Head4行目
        row = xssfSheet.getRow(HEAD_ROW_3);
        // 客户编号
        String customNo = row.getCell(HEAD_COLUMN_2).getStringCellValue();
        sb.append(customNo).append(COMA);

        // Head7行目
        row = xssfSheet.getRow(HEAD_ROW_6);
        // 负责人番号
        String tantoNo = row.getCell(HEAD_COLUMN_7).getRawValue();
        sb.append(tantoNo).append(COMA);

        return sb.toString();
    }

    /**
     * 文字列がNullや空白か判定
     *
     * @param value 文字列
     * @return 判定結果
     */
    private boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        }
        return false;
    }
}
