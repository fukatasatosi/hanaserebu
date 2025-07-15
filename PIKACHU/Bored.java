package PIKACHU;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bored {
    public static String getBored() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://bored-api.appbrewery.com/random"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Pattern pattern = Pattern.compile("\\\"activity\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            String activity = matcher.find() ? matcher.group(1) : "アクティビティの取得に失敗しました";
            return activity;
        } catch (Exception e) {
            return "アクティビティの取得に失敗しました: " + e.getMessage();
        }
    }
}
