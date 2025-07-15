package a;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 猫画像URL取得クラス
public class NekoImageFetcher {
    // The Cat APIから猫画像URLを取得
    public static String fetchCatImageUrl() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.thecatapi.com/v1/images/search"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            // JSONからurlを抽出
            Pattern pattern = Pattern.compile("\\\"url\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return "画像URLが取得できませんでした";
            }
        } catch (Exception e) {
            return "猫画像取得エラー: " + e.getMessage();
        }
    }
}
