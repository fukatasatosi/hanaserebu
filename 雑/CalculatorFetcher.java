package 雑;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class CalculatorFetcher {
    // Rurihabachi Calculator APIでvalue.calculatedvalueを取得
    public static String fetchCalcResult(String expr) {
        try {
            String encodedExpr = URLEncoder.encode(expr, StandardCharsets.UTF_8);
            String url = "http://www.rurihabachi.com/web/webapi/calculator/json?exp=" + encodedExpr;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            // JSONからvalue.calculatedvalueを抽出
            JSONObject json = new JSONObject(body);
            return body;
        } catch (Exception e) {
            return "計算エラー: " + e.getMessage();
        }
    }
}