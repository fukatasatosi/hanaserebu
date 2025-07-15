import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * 天気予報データを取得するクラス
 */
class WeatherDataFetcher {
    public String fetchWeatherData(String targetUrl) throws IOException {
        URI uri = URI.create(targetUrl);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            }
            return responseBody.toString();
        } else {
            throw new IOException("データ取得に失敗しました: HTTP " + responseCode);
        }
    }
}

/**
 * 天気予報データを解析するクラス
 */
class WeatherDataParser {
    public List<WeatherForecast> parseWeatherData(String jsonData) {
        // jsonDataはString型のため、まずJSONObjectにパースします
        JSONObject jsonObject = new JSONObject(jsonData);

        // "hello"キーの値を取得します
        String encodedMessage = jsonObject.getString("hello");
        String decodedMessage = decodeHtmlEntities(encodedMessage);

        // 戻り値の型List<WeatherForecast>に合わせてデータを作成します。
        // APIレスポンスには日付が含まれていないため、現在日時を使用します。
        List<WeatherForecast> forecasts = new ArrayList<>();
        WeatherForecast forecast = new WeatherForecast(LocalDateTime.now(), decodedMessage);
        forecasts.add(forecast);
        return forecasts;
    }

    /**
     * HTMLの数値文字参照（例: &#12371;）をデコードします。
     * 
     * @param text エンコードされた文字列
     * @return デコードされた文字列
     */
    private String decodeHtmlEntities(String text) {
        Pattern pattern = Pattern.compile("&#(\\d+);");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            int charCode = Integer.parseInt(matcher.group(1));
            matcher.appendReplacement(sb, String.valueOf((char) charCode));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

/**
 * 天気予報データを表すクラス
 * 
 * 
 * 
 * /**
 * 天気予報データを表すクラス
 */
class WeatherForecast {
    private final LocalDateTime dateTime;
    private final String weather;

    public WeatherForecast(LocalDateTime dateTime, String weather) {
        this.dateTime = dateTime;
        this.weather = weather;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getWeather() {
        return weather;
    }
}

/**
 * 
 */
class WeatherDataDisplayer {
    public void displayWeatherData(List<WeatherForecast> forecasts) {
        for (WeatherForecast forecast : forecasts) {
            System.out.println(forecast.getDateTime().format(
                    DateTimeFormatter.ofPattern("yyyy/MM/dd")) + " " + forecast.getWeather());
        }
    }
}

/**
 *
 * 
 * @author n.katayama
 * @version 1.0
 */
public class hello {
    /**
     * 
     */
    private static final String TARGET_URL = "https://fourtonfish.com/hellosalut/?lang=kk";

    /**
     * 
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        WeatherDataFetcher fetcher = new WeatherDataFetcher();
        WeatherDataParser parser = new WeatherDataParser();
        WeatherDataDisplayer displayer = new WeatherDataDisplayer();

        try {
            String jsonData = fetcher.fetchWeatherData(TARGET_URL);
            List<WeatherForecast> forecasts = parser.parseWeatherData(jsonData);
            displayer.displayWeatherData(forecasts);
        } catch (IOException e) {
            System.err.println("データ取得エラー: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("予期しないエラー: " + e.getMessage());
        }
    }
}
