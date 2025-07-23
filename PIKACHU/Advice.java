
package PIKACHU;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class Advice {
    public static String getAdvice() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.adviceslip.com/advice"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            JSONObject root = new JSONObject(body);
            String advice = root.getJSONObject("slip").optString("advice", "アドバイスの取得に失敗しました");
            return advice;
        } catch (Exception e) {
            return "アドバイスの取得に失敗しました: " + e.getMessage();
        }
    }
}
