
// 必要なライブラリのインポート
import java.io.BufferedReader; // APIレスポンスの読み取り用
import java.io.InputStreamReader; // APIレスポンスの読み取り用
import java.net.HttpURLConnection; // HTTP通信のため
import java.net.URL; // URLオブジェクト生成のため
import org.json.JSONObject; // JSONパース用

public class 天気 {
    // 緯度・経度は例: 東京駅 (lat=35.6812, lon=139.7671)
    public static void main(String[] args) {
        double lat = 35.6812; // 緯度
        double lon = 139.7671; // 経度
        try {
            // APIのURLを作成
            String urlStr = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon
                    + "&hourly=temperature_2m,weathercode&current_weather=true&timezone=Asia%2FTokyo";
            URL url = new URL(urlStr); // URLオブジェクト生成
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // HTTP接続開始
            conn.setRequestMethod("GET"); // GETメソッド指定
            int code = conn.getResponseCode(); // レスポンスコード取得
            if (code != 200) {
                System.out.println("APIエラー: " + code); // エラー時の出力
                return;
            }
            // レスポンスを読み込む
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line); // レスポンスを1行ずつ追加
            br.close(); // ストリームを閉じる
            conn.disconnect(); // 接続を切断
            // JSONデータをパース
            JSONObject obj = new JSONObject(sb.toString());
            JSONObject current = obj.getJSONObject("current_weather"); // 現在の天気情報取得
            double temp = current.getDouble("temperature"); // 気温取得
            int weatherCode = current.getInt("weathercode"); // 天気コード取得
            String weather = getWeatherDescription(weatherCode); // 天気コードから説明文取得
            String advice = getClothesAdvice(temp, weatherCode); // 気温と天気から服装アドバイス取得
            // 結果を出力
            System.out.println("今日の天気: " + weather);
            System.out.println("気温: " + temp + "℃");
            System.out.println("服装アドバイス: " + advice);
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage()); // 例外発生時の出力
        }
    }

    // 天気コードから説明文を返す関数
    private static String getWeatherDescription(int code) {
        switch (code) {
            case 0:
                return "快晴";
            case 1:
            case 2:
            case 3:
                return "晴れまたは曇り";
            case 45:
            case 48:
                return "霧";
            case 51:
            case 53:
            case 55:
                return "霧雨";
            case 61:
            case 63:
            case 65:
                return "雨";
            case 71:
            case 73:
            case 75:
                return "雪";
            case 80:
            case 81:
            case 82:
                return "にわか雨";
            case 95:
                return "雷雨";
            default:
                return "不明";
        }
    }

    // 気温と天気から服装アドバイスを返す関数
    private static String getClothesAdvice(double temp, int weatherCode) {
        if (temp >= 28)
            return "半袖・短パンなど涼しい服装";
        if (temp >= 20)
            return "半袖または薄手の長袖";
        if (temp >= 15)
            return "長袖シャツやカーディガン";
        if (temp >= 10)
            return "薄手のジャケットやセーター";
        if (temp >= 5)
            return "コートや厚手の上着";
        return "冬用コート・マフラーなど防寒対策";
    }
}
