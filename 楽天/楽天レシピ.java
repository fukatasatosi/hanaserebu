package 楽天;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;
import java.net.URLEncoder;

/**
 * 楽天レシピAPIを利用したレシピカテゴリ一覧表示アプリ
 * https://webservice.rakuten.co.jp/api/recipe-categorylist/
 */
public class 楽天レシピ {
    /**
     * メイン処理: 楽天レシピAPIでカテゴリ一覧を取得し、表示
     * 
     * @param args コマンドライン引数（未使用）
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // 楽天APIアプリIDを入力してください
            String appId = "1057854877760086255"; // ←ご自身の楽天アプリIDに変更

            // カテゴリタイプ選択（large, medium, small など）
            System.out.print("カテゴリタイプを入力してください（large,medium,small  、省略可）: ");
            String categoryType = scanner.nextLine().trim();
            String url = String.format(
                    "https://app.rakuten.co.jp/services/api/Recipe/CategoryList/20170426?format=json&applicationId=%s&categoryType=%s",
                    appId, URLEncoder.encode(categoryType, "UTF-8"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("APIリクエスト失敗: " + response.statusCode());
                return;
            }
            JSONObject root = new JSONObject(response.body());
            JSONObject resultObj = root.optJSONObject("result");
            if (resultObj == null) {
                System.out.println("APIレスポンスにresultがありませんでした。");
                return;
            }
            String typeKey = categoryType.isEmpty() ? "large" : categoryType;
            JSONArray categories = resultObj.optJSONArray(typeKey);
            if (categories == null || categories.length() == 0) {
                System.out.println("該当するカテゴリがありませんでした。");
                return;
            }
            System.out.println("--- レシピカテゴリ一覧 ---");
            for (int i = 0; i < categories.length(); i++) {
                JSONObject cat = categories.getJSONObject(i);
                System.out.println((i + 1) + ". " + cat.optString("categoryName", "(名称なし)") +
                        " [ID: " + cat.optString("categoryId", "-") + "]");
            }

            // ランキング表示機能
            System.out.print("ランキングを見たいカテゴリIDを入力してください（スキップはEnter）: ");
            String categoryId = scanner.nextLine().trim();
            if (!categoryId.isEmpty()) {
                String rankingUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/Recipe/CategoryRanking/20170426?format=json&applicationId=%s&categoryId=%s",
                        appId, URLEncoder.encode(categoryId, "UTF-8"));
                HttpRequest rankingReq = HttpRequest.newBuilder().uri(URI.create(rankingUrl)).build();
                HttpResponse<String> rankingRes = client.send(rankingReq, HttpResponse.BodyHandlers.ofString());
                if (rankingRes.statusCode() != 200) {
                    System.out.println("ランキングAPIリクエスト失敗: " + rankingRes.statusCode());
                    return;
                }
                JSONObject rankingRoot = new JSONObject(rankingRes.body());
                JSONArray recipes = rankingRoot.optJSONArray("result");
                if (recipes == null || recipes.length() == 0) {
                    System.out.println("該当するランキングレシピがありませんでした。");
                    return;
                }
                System.out.println("--- カテゴリ内ランキング上位レシピ ---");
                for (int i = 0; i < recipes.length(); i++) {
                    JSONObject recipe = recipes.getJSONObject(i);
                    System.out.println((i + 1) + ". " + recipe.optString("recipeTitle", "(タイトルなし)") +
                            " by " + recipe.optString("nickName", "-") +
                            " [レシピID: " + recipe.optString("recipeId", "-") + "]");
                    System.out.println("    URL: " + recipe.optString("recipeUrl", "-"));
                }
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
