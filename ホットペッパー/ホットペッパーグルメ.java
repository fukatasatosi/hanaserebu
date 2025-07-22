package ホットペッパー;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Scanner;

public class ホットペッパーグルメ {
    // ホットペッパーAPIキーをここに記載
    private static final String API_KEY = "fd389c1bb315f66b";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("検索方法を選択してください（1: 地域コード, 2: 周辺検索）: ");
        String mode = scanner.nextLine().trim();
        if ("2".equals(mode)) {
            System.out.print("緯度を入力してください（例: 34.67）: ");
            double lat = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("経度を入力してください（例: 135.52）: ");
            double lng = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("検索範囲（1:300m,2:500m,3:1000m,4:2000m,5:3000m）: ");
            int range = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("並び順（1:標準,2:人気順,3:口コミ順,4:新着順）: ");
            int order = Integer.parseInt(scanner.nextLine().trim());
            searchRestaurantsByLocation(lat, lng, range, order);
        } else {
            System.out.print("地域コードを入力してください（例: Z011=東京）: ");
            String area = scanner.nextLine().trim();
            searchRestaurants(area);
        }
        scanner.close();
    }

    // 緯度・経度・範囲・並び順で周辺店舗検索
    public static void searchRestaurantsByLocation(double lat, double lng, int range, int order) {
        try {
            String apiUrl = "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key="
                    + API_KEY + "&lat=" + lat + "&lng=" + lng + "&range=" + range + "&order=" + order + "&format=json";
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
            JSONArray shops = json.getJSONObject("results").getJSONArray("shop");
            for (int i = 0; i < shops.length(); i++) {
                JSONObject shop = shops.getJSONObject(i);
                System.out.println("店名: " + shop.getString("name"));
                System.out.println("住所: " + shop.getString("address"));
                System.out.println("アクセス: " + shop.getString("access"));
                JSONObject urlsObj = shop.optJSONObject("urls");
                String pcUrl = urlsObj != null ? urlsObj.optString("pc", "-") : "-";
                System.out.println("URL: " + pcUrl);
                System.out.println("----------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ホットペッパーグルメAPIで地域検索
    public static void searchRestaurants(String areaCode) {
        try {
            String apiUrl = "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key="
                    + API_KEY + "&large_area=" + areaCode + "&format=json";
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
            JSONArray shops = json.getJSONObject("results").getJSONArray("shop");
            for (int i = 0; i < shops.length(); i++) {
                JSONObject shop = shops.getJSONObject(i);
                System.out.println("店名: " + shop.getString("name"));
                System.out.println("住所: " + shop.getString("address"));
                System.out.println("アクセス: " + shop.getString("access"));
                JSONObject urlsObj = shop.optJSONObject("urls");
                String pcUrl = urlsObj != null ? urlsObj.optString("pc", "-") : "-";
                System.out.println("URL: " + pcUrl);
                System.out.println("----------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
