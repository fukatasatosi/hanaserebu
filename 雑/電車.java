package 雑;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class 電車 {
    /**
     * 駅データ.jpのapi/p/(都道府県コード).jsonを使って都道府県名を検索・表示
     * 
     * @param prefCode 都道府県コード（例: 13→東京都）
     */
    public static void searchPrefName(String prefCode) {
        try {
            String apiUrl = "https://www.ekidata.jp/api/p/" + prefCode + ".json";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                if (json.has("result")) {
                    JSONObject result = json.getJSONObject("result");
                    String prefName = result.optString("pref_name");
                    System.out.println("都道府県名: " + prefName);
                } else {
                    System.out.println("都道府県名が見つかりませんでした。");
                }
            } else {
                System.out.println("APIリクエスト失敗: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.print("都道府県コードを入力してください（例: 13→東京都）: ");
        String code = scanner.nextLine().trim();
        searchPrefName(code);
        scanner.close();
    }
}
