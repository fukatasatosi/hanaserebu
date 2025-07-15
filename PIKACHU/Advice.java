package PIKACHU;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Advice {
    public static String getAdvice() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.adviceslip.com/advice"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Pattern pattern = Pattern.compile("\\\"advice\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            String advice = matcher.find() ? matcher.group(1) : "アドバイスの取得に失敗しました";
            return advice;
        } catch (Exception e) {
            return "アドバイスの取得に失敗しました: " + e.getMessage();
        }
    }
}
