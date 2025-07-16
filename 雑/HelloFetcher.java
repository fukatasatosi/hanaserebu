package 雑;

public class HelloFetcher {
    // HelloSalut APIから挨拶メッセージを取得
    public static String fetchHello(String lang) {
        try {
            String apiUrl = "https://fourtonfish.com/hellosalut/?lang=" + lang;
            java.net.URL url = new java.net.URL(apiUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            org.json.JSONObject json = new org.json.JSONObject(response.toString());
            String message = json.optString("hello", "取得失敗");
            return message;
        } catch (Exception e) {
            return "挨拶取得エラー: " + e.getMessage();
        }
    }
}
