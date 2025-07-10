import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Buzz {
    public static String getBuzz() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://corporatebs-generator.sameerkumar.website/"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Pattern pattern = Pattern.compile("\\\"phrase\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            String buzz = matcher.find() ? matcher.group(1) : "バズワードの取得に失敗しました";
            return buzz;
        } catch (Exception e) {
            return "バズワードの取得に失敗しました: " + e.getMessage();
        }
    }
}
