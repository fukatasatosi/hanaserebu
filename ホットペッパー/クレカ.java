package ホットペッパー;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

public class クレカ {
    // ホットペッパーAPIキーをここに記載
    private static final String API_KEY = "fd389c1bb315f66b";

    public static void main(String[] args) {
        getCreditCards();
    }

    // クレジットカードAPIで利用可能なカード一覧を取得
    public static void getCreditCards() {
        try {
            String apiUrl = "http://webservice.recruit.co.jp/hotpepper/credit_card/v1/?key=" + API_KEY + "&format=json";
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
            JSONArray cards = json.getJSONObject("results").getJSONArray("credit_card");
            System.out.println("利用可能なクレジットカード一覧:");
            for (int i = 0; i < cards.length(); i++) {
                JSONObject card = cards.getJSONObject(i);
                System.out.println("コード: " + card.getString("code") + " 名称: " + card.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
