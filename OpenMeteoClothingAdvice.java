import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import org.json.JSONObject;

public class OpenMeteoClothingAdvice {
    // メイン処理: Open-Meteo APIで天気・気温を取得し服装アドバイスを表示
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("緯度を入力してください（例: 35.6812）: ");
            String lat = scanner.nextLine();
            System.out.print("経度を入力してください（例: 139.7671）: ");
            String lon = scanner.nextLine();
            scanner.close();

            String url = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&daily=temperature_2m_max,temperature_2m_min,weathercode&timezone=Asia%%2FTokyo",
                    lat, lon);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject root = new JSONObject(response.body());
            JSONObject daily = root.getJSONObject("daily");
            double tmax = daily.getJSONArray("temperature_2m_max").getDouble(0);
            double tmin = daily.getJSONArray("temperature_2m_min").getDouble(0);
            int weatherCode = daily.getJSONArray("weathercode").getInt(0);

            String weather = getWeatherDescription(weatherCode);
            String advice = getClothingAdvice(tmax, tmin, weather);

            System.out.println("今日の天気: " + weather + " 最高気温: " + tmax + "℃ 最低気温: " + tmin + "℃");
            System.out.println("服装アドバイス: " + advice);
        } catch (Exception e) {
            System.out.println("天気情報の取得に失敗しました: " + e.getMessage());
        }
    }

    // 天気コードから天気説明文を返す関数
    private static String getWeatherDescription(int code) {
        switch (code) {
            case 0:
                return "快晴";
            case 1:
            case 2:
            case 3:
                return "晴れ";
            case 45:
            case 48:
                return "霧";
            case 51:
            case 53:
            case 55:
            case 56:
            case 57:
                return "霧雨";
            case 61:
            case 63:
            case 65:
            case 66:
            case 67:
                return "雨";
            case 71:
            case 73:
            case 75:
            case 77:
                return "雪";
            case 80:
            case 81:
            case 82:
                return "にわか雨";
            case 85:
            case 86:
                return "にわか雪";
            case 95:
                return "雷雨";
            case 96:
            case 99:
                return "激しい雷雨";
            default:
                return "不明";
        }
    }

    // 気温・天気から服装アドバイスを返す関数
    private static String getClothingAdvice(double tmax, double tmin, String weather) {
        if (tmax >= 30)
            return "今日はとても暑いので、半袖や短パンなど涼しい服装で！";
        if (tmax >= 25)
            return "半袖や薄手の長袖がちょうどいいです。";
        if (tmax >= 20)
            return "長袖シャツやカーディガンがオススメです。";
        if (tmax >= 15)
            return "パーカーや薄手のジャケットを着ていきましょう。";
        if (tmax >= 10)
            return "セーターや厚手の上着が必要です。";
        if (tmax >= 5)
            return "コートやダウンジャケットでしっかり防寒しましょう。";
        return "今日はとても寒いので、厚手のコート・マフラー・手袋でしっかり防寒してください。";
    }
}
