package 雑;

public class AddressFetcher {
    // 郵便番号から住所を取得して返す
    public static String fetchAddress(String zipcode) {
        try {
            String apiUrl = "https://zipcloud.ibsnet.co.jp/api/search?zipcode=" + zipcode;
            java.net.URL url = new java.net.URL(apiUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            org.json.JSONObject json = new org.json.JSONObject(response.toString());
            org.json.JSONArray results = json.optJSONArray("results");
            if (results != null && results.length() > 0) {
                org.json.JSONObject addr = results.getJSONObject(0);
                String address = addr.getString("address1") + addr.getString("address2") + addr.getString("address3");
                return address;
            } else {
                return "該当する住所が見つかりませんでした。";
            }
        } catch (Exception e) {
            return "住所取得エラー: " + e.getMessage();
        }
    }
}