package 楽天;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONArray;

public class 楽天電子書籍 {
    // --- main関数（エントリーポイント） ---
    // 楽天APIキーをここに記載
    private static final String APPLICATION_ID = "1057854877760086255";

    public static void main(String[] args) {
        // 検索キーワード例
        Scanner scanner = new Scanner(System.in);
        System.out.print("検索キーワードを入力してください: ");
        String keyword = scanner.nextLine().trim();
        String genreId = "001004"; // 例: 小説・エッセイ
        searchEbooks(keyword, genreId);
    }

    // --- 楽天電子書籍APIでジャンルID付き検索する関数 ---
    /**
     * 楽天電子書籍APIでジャンルID付き検索
     * 
     * @param keyword 検索キーワード
     * @param genreId ジャンルID（例: "001004"）
     */
    public static void searchEbooks(String keyword, String genreId) {
        try {
            String apiUrl = "https://app.rakuten.co.jp/services/api/Kobo/EbookSearch/20170426?applicationId="
                    + APPLICATION_ID
                    + "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8")
                    + "&genreId=" + genreId
                    + "&format=json";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray items = json.getJSONArray("Items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i).getJSONObject("Item");
                System.out.println("タイトル: " + item.getString("title"));
                System.out.println("著者: " + item.optString("author", "不明"));
                System.out.println("価格: " + item.optInt("itemPrice", 0) + "円");
                System.out.println("URL: " + item.getString("itemUrl"));
                System.out.println("----------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
