package 楽天;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 楽天トラベルAPIを利用した簡易ホテル・施設検索アプリ
 * https://webservice.rakuten.co.jp/api/simplehotelsearch/
 */
public class 楽天トラベル {
    /**
     * メイン処理: 楽天トラベルAPIで施設検索を行い、結果を表示
     * 
     * @param args コマンドライン引数（未使用）
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("都道府県を選択してください（例: 13=東京都, 27=大阪府）: ");
            String prefCode = scanner.nextLine().trim();
            if (prefCode.isEmpty()) {
                // デフォルトで大阪府（27）
                prefCode = "27";
                System.out.println("都道府県コード未入力のため大阪府(27)で検索します。");
            }
            System.out.print("検索キーワード（ホテル名・地名など、省略可。例: 梅田）: ");
            String keyword = scanner.nextLine().trim();

            // 楽天APIアプリIDを入力してください
            String appId = "1057854877760086255"; // ←ご自身の楽天アプリIDに変更

            // --- 楽天トラベルAPI（SimpleHotelSearch）で施設検索 ---
            // API仕様上、都道府県コードだけでは検索不可。middleClassCodeには正しいエリアコード（例: 27→270000, 13→130000）が必要
            String largeClassCode = "japan";
            String middleClassCode;
            if (prefCode.matches("\\d{2}")) {
                middleClassCode = prefCode + "0000";
            } else if (prefCode.matches("\\d{6}")) {
                middleClassCode = prefCode;
            } else {
                System.out.println("都道府県コードは2桁または6桁の数字で入力してください。");
                return;
            }
            String url = String.format(
                    "https://app.rakuten.co.jp/services/api/Travel/SimpleHotelSearch/20170426?format=json&applicationId=%s&largeClassCode=%s&middleClassCode=%s&keyword=%s",
                    appId, URLEncoder.encode(largeClassCode, "UTF-8"), URLEncoder.encode(middleClassCode, "UTF-8"),
                    URLEncoder.encode(keyword, "UTF-8"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("APIリクエスト失敗: " + response.statusCode());
                return;
            }
            JSONObject root = new JSONObject(response.body());
            JSONArray hotels = root.optJSONArray("hotels");
            if (hotels == null || hotels.length() == 0) {
                System.out.println("該当する施設がありませんでした。");
                return;
            }
            System.out.println("--- 検索結果 ---");
            for (int i = 0; i < Math.min(10, hotels.length()); i++) {
                JSONObject hotel = hotels.getJSONObject(i).getJSONObject("hotel").getJSONArray("hotelBasicInfo")
                        .getJSONObject(0);
                System.out.println((i + 1) + ". " + hotel.optString("hotelName", "(名称なし)"));
                System.out.println("  住所: " + hotel.optString("address1", "") + hotel.optString("address2", ""));
                System.out.println("  アクセス: " + hotel.optString("access", "-"));
                System.out.println("  電話: " + hotel.optString("telephoneNo", "-"));
                System.out.println("  料金: " + hotel.optInt("hotelMinCharge", 0) + "円～");
                System.out.println("  URL: " + hotel.optString("hotelInformationUrl", "-"));
                // 施設詳細情報の取得・表示
                System.out.print("  → この施設の詳細情報を表示しますか？ (y/n): ");
                String showDetail = scanner.nextLine().trim().toLowerCase();
                if (showDetail.equals("y")) {
                    int hotelNo = hotel.optInt("hotelNo", 0);
                    if (hotelNo > 0) {
                        String detailUrl = String.format(
                                "https://app.rakuten.co.jp/services/api/Travel/HotelDetailSearch/20170426?format=json&applicationId=%s&hotelNo=%d",
                                appId, hotelNo);
                        HttpRequest detailReq = HttpRequest.newBuilder().uri(URI.create(detailUrl)).build();
                        HttpResponse<String> detailRes = client.send(detailReq, HttpResponse.BodyHandlers.ofString());
                        if (detailRes.statusCode() == 200) {
                            JSONObject detailRoot = new JSONObject(detailRes.body());
                            JSONArray detailHotels = detailRoot.optJSONArray("hotels");
                            if (detailHotels != null && detailHotels.length() > 0) {
                                JSONObject detail = detailHotels.getJSONObject(0).getJSONObject("hotel")
                                        .getJSONArray("hotelDetailInfo").getJSONObject(0);
                                System.out.println("    --- 施設詳細 ---");
                                System.out.println("    チェックイン: " + detail.optString("checkinTime", "-"));
                                System.out.println("    チェックアウト: " + detail.optString("checkoutTime", "-"));
                                System.out.println("    駐車場: " + detail.optString("parkingInformation", "-"));
                                System.out.println("    部屋数: " + detail.optString("roomNum", "-"));
                                System.out.println("    施設説明: " + detail.optString("hotelCaption", "-"));
                            } else {
                                System.out.println("    詳細情報が取得できませんでした。");
                            }
                        } else {
                            System.out.println("    詳細APIリクエスト失敗: " + detailRes.statusCode());
                        }
                    } else {
                        System.out.println("    ホテル番号が取得できませんでした。");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
