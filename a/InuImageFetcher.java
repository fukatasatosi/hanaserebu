package a;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 犬画像取得クラス
public class InuImageFetcher {
    // Dog CEO APIから犬画像URLを取得
    public static String fetchDogImageUrl() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://dog.ceo/api/breeds/image/random"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Pattern pattern = Pattern.compile("\\\"message\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                return matcher.group(1).replace("\\", "");
            } else {
                return "画像URLが取得できませんでした";
            }
        } catch (Exception e) {
            return "犬画像取得エラー: " + e.getMessage();
        }
    }
}
