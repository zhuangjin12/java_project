package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class Test22 {

    public static String STR_DOT = ".";
    /** タイプ定数 */
    public static List<String> TYPE_LIST;
    static {
        TYPE_LIST = Collections.unmodifiableList( new ArrayList<String>() {
            {
                add("1");
                add("3");
                add("5");
            }
        });
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        int D = sc.nextInt();
        String[] vArr = new String[N];

        String domain = "";
        Set<String> domainSet = new HashSet<>();
        List<String> domainList = new ArrayList<>();
        StringTokenizer token;
        int deleteCnt = 0;

        for (int i = 0; i < N; i++) {
            vArr[i] = sc.next();

            // ドットで分割
            token = new StringTokenizer(vArr[i], STR_DOT);
            while (token.hasMoreElements()) {
                // トーケンの各要素をリストに追加
                domainList.add(token.nextToken());
            }

            // 不要なもの削除
            deleteCnt = domainList.size() - D;
            for (int j = 0; j < deleteCnt ; j++) {
                domainList.remove(0);
            }
            // ドメイン復元
            domain = String.join(STR_DOT, domainList);
            // Setに追加
            domainSet.add(domain);

            domainList.clear();
        }
        sc.close();

        int[] cntDomain = new int[domainSet.size()];
        String[] domainArr = domainSet.toArray(new String[0]);

        // Mapの宣言
        Map<Integer, String> mMap = new HashMap<Integer, String>();

        // 各ドメインのカウントを取得する
        for (int i = 0; i < domainArr.length; i++) {
            cntDomain[i] = 0;
            for (String val : vArr) {
                if (val.contains(domainArr[i])) {
                    cntDomain[i]++;
                }
            }
            // Mapに追加(キー：カウント数、バリュー：ドメイン名)
            mMap.put(cntDomain[i], domainArr[i]);
        }

        // キーで降順ソートする
        Integer[] mapkey = mMap.keySet().toArray(new Integer[0]);
        Arrays.sort(mapkey, Collections.reverseOrder());

        for (Integer nKey : mapkey) {
            System.out.println(mMap.get(nKey) +  "," + nKey);
        }

    }

}
