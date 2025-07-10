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
            System.out.print("価格帯検索を行いますか？ (y/n): ");
            String priceSearch = scanner.nextLine().trim().toLowerCase();
            int minPrice = 0, maxPrice = 0;
            if (priceSearch.equals("y")) {
                System.out.print("最低価格を入力してください（円）: ");
                minPrice = Integer.parseInt(scanner.nextLine());
                System.out.print("最高価格を入力してください（円）: ");
                maxPrice = Integer.parseInt(scanner.nextLine());
            }
            scanner.close();

            // 楽天APIアプリIDを入力してください
            String appId = "1057854877760086255"; // ←ご自身の楽天アプリIDに変更

            // 商品検索API（IchibaItem/Search）
            String url = String.format(
                "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20220601?format=json&applicationId=%s&keyword=%s",
                appId, URLEncoder.encode(keyword, "UTF-8")
            );
            if (priceSearch.equals("y")) {
                url += "&minPrice=" + minPrice + "&maxPrice=" + maxPrice;
            }

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
                // カテゴリー取得
                int genreId = item.getInt("genreId");
                String genreName = getGenreName(genreId, appId);
                System.out.println("  カテゴリー: " + genreName);
                // タグ検索
                String tagNames = getTagNames(item.getInt("itemCode"), appId);
                System.out.println("  タグ: " + tagNames);
            }

            // 商品価格帯検索API（Product/Search）
            if (priceSearch.equals("y")) {
                String productUrl = String.format(
                    "https://app.rakuten.co.jp/services/api/Product/Search/20170426?format=json&applicationId=%s&keyword=%s&minPrice=%d&maxPrice=%d",
                    appId, URLEncoder.encode(keyword, "UTF-8"), minPrice, maxPrice
                );
                HttpRequest prodReq = HttpRequest.newBuilder().uri(URI.create(productUrl)).build();
                HttpResponse<String> prodRes = client.send(prodReq, HttpResponse.BodyHandlers.ofString());
                if (prodRes.statusCode() == 200) {
                    JSONObject prodRoot = new JSONObject(prodRes.body());
                    JSONArray prods = prodRoot.optJSONArray("Products");
                    if (prods != null && prods.length() > 0) {
                        System.out.println("\n--- 商品価格帯検索結果（Product API）---");
                        for (int i = 0; i < Math.min(10, prods.length()); i++) {
                            JSONObject prod = prods.getJSONObject(i).getJSONObject("Product");
                            System.out.println((i + 1) + ". " + prod.getString("productName"));
                            System.out.println("  メーカー: " + prod.optString("makerName", "-") );
                            System.out.println("  JANコード: " + prod.optString("jan", "-") );
                            System.out.println("  商品URL: " + prod.optString("productUrl", "-") );
                        }
                    } else {
                        System.out.println("\n商品価格帯検索APIで該当商品がありませんでした。");
                    }
                } else {
                    System.out.println("\n商品価格帯検索APIリクエスト失敗: " + prodRes.statusCode());
                }
            }

            // 楽天ランキングAPI（IchibaItem/Ranking）
            String rankingUrl = String.format(
                "https://app.rakuten.co.jp/services/api/IchibaItem/Ranking/20220601?format=json&applicationId=%s&keyword=%s",
                appId, URLEncoder.encode(keyword, "UTF-8")
            );
            HttpRequest rankingReq = HttpRequest.newBuilder().uri(URI.create(rankingUrl)).build();
            HttpResponse<String> rankingRes = client.send(rankingReq, HttpResponse.BodyHandlers.ofString());
            if (rankingRes.statusCode() == 200) {
                JSONObject rankingRoot = new JSONObject(rankingRes.body());
                JSONArray rankingItems = rankingRoot.optJSONArray("Items");
                if (rankingItems != null && rankingItems.length() > 0) {
                    System.out.println("\n--- 楽天ランキング（購入履歴上位） ---");
                    for (int i = 0; i < Math.min(10, rankingItems.length()); i++) {
                        JSONObject item = rankingItems.getJSONObject(i).getJSONObject("Item");
                        System.out.println((i + 1) + ". " + item.getString("itemName"));
                        System.out.println("  価格: " + item.getInt("itemPrice") + "円");
                        System.out.println("  URL: " + item.getString("itemUrl"));
                    }
                } else {
                    System.out.println("\nランキングAPIで該当商品がありませんでした。");
                }
            } else {
                System.out.println("\nランキングAPIリクエスト失敗: " + rankingRes.statusCode());
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    // 楽天ジャンルAPIでジャンル名を取得
    private static String getGenreName(int genreId, String appId) {
        try {
            String url = String.format(
                "https://app.rakuten.co.jp/services/api/IchibaGenre/Search/20140222?format=json&applicationId=%s&genreId=%d",
                appId, genreId);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return "(取得失敗)";
            JSONObject root = new JSONObject(response.body());
            JSONArray genres = root.getJSONArray("children");
            if (genres.length() > 0) {
                JSONObject genre = genres.getJSONObject(0).getJSONObject("child");
                return genre.getString("genreName");
            } else if (root.has("genreName")) {
                return root.getString("genreName");
            }
            return "(不明)";
        } catch (Exception e) {
            return "(取得失敗)";
        }
    }

    // 楽天タグAPIでタグ名を取得
    private static String getTagNames(int itemCode, String appId) {
        try {
            // itemCodeは本来文字列ですが、APIレスポンスによってはint型で来る場合があるためStringに変換
            String code = String.valueOf(itemCode);
            String url = String.format(
                "https://app.rakuten.co.jp/services/api/IchibaTag/Search/20140222?format=json&applicationId=%s&itemCode=%s",
                appId, code);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return "(取得失敗)";
            JSONObject root = new JSONObject(response.body());
            JSONArray tags = root.optJSONArray("Tags");
            if (tags != null && tags.length() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < tags.length(); i++) {
                    JSONObject tag = tags.getJSONObject(i).getJSONObject("Tag");
                    sb.append(tag.getString("tagName"));
                    if (i < tags.length() - 1) sb.append(", ");
                }
                return sb.toString();
            }
            return "(タグなし)";
        } catch (Exception e) {
            return "(取得失敗)";
        }
    }
}
