import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class 楽天商品 {
    // メイン処理: 楽天商品APIで商品検索
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("検索キーワードを入力してください: ");
            String keyword = scanner.nextLine();
            scanner.close();

            // 楽天APIアプリIDを入力してください
            String appId = "1057854877760086255"; // ←ご自身の楽天アプリIDに変更
            String url = String.format(
                    "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20220601?format=json&applicationId=%s&keyword=%s",
                    appId, URLEncoder.encode(keyword, "UTF-8"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("APIリクエスト失敗: " + response.statusCode());
                return;
            }
            JSONObject root = new JSONObject(response.body());
            JSONArray items = root.getJSONArray("Items");
            if (items.length() == 0) {
                System.out.println("該当する商品がありませんでした。");
                return;
            }
            System.out.println("--- 検索結果 ---");
            for (int i = 0; i < Math.min(10, items.length()); i++) {
                JSONObject item = items.getJSONObject(i).getJSONObject("Item");
                System.out.println((i + 1) + ". " + item.getString("itemName"));
                System.out.println("  価格: " + item.getInt("itemPrice") + "円");
                System.out.println("  URL: " + item.getString("itemUrl"));
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
