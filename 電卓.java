import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class 電卓 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("計算式を入力してください（例: 1+2*3）: ");
        String expr = scanner.nextLine();
        try {
            String encodedExpr = URLEncoder.encode(expr, StandardCharsets.UTF_8);
            String url = "http://www.rurihabachi.com/web/webapi/calculator/json?exp=" + encodedExpr;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            // JSONからresultを抽出（エスケープ文字対応）
            Pattern pattern = Pattern.compile("\\\"result\\\":\\\"(.*?)\\\"");
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                System.out.println("計算結果: " + matcher.group(1));
            } else {
                System.out.println("計算結果の取得に失敗しました\nAPIレスポンス: " + body);
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
