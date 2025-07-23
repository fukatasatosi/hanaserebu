package 雑;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Scanner;

public class 住所検索 {
    // --- main関数（エントリーポイント） ---
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("郵便番号を入力してください（例: 1000001）: ");
        String zipcode = scanner.nextLine().trim();
        searchAddress(zipcode);
        scanner.close();
    }

    // --- 郵便番号から住所を検索する関数 ---
    public static void searchAddress(String zipcode) {
        try {
            String apiUrl = "https://zipcloud.ibsnet.co.jp/api/search?zipcode=" + zipcode;
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
            JSONArray results = json.optJSONArray("results");
            if (results != null && results.length() > 0) {
                JSONObject addr = results.getJSONObject(0);
                String address = addr.getString("address1") + addr.getString("address2") + addr.getString("address3");
                System.out.println("住所: " + address);
            } else {
                System.out.println("該当する住所が見つかりませんでした。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}