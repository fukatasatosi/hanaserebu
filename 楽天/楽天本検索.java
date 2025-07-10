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
 * 楽天ブックス総合検索アプリ
 * 楽天BooksTotal APIを利用して書籍情報を検索・表示します。
 * https://webservice.rakuten.co.jp/api/BooksTotalSearch/
 */
public class 楽天本検索 {
    /**
     * メイン処理: 楽天ブックス総合検索APIで書籍情報を取得し表示
     * 
     * @param args コマンドライン引数（未使用）
     */
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("検索キーワードを入力してください: ");
            String keyword = scanner.nextLine();

            System.out.print("書籍検索（詳細書籍検索）も行いますか？ (y/n): ");
            String doBookSearch = scanner.nextLine().trim().toLowerCase();

            System.out.print("CDも検索しますか？ (y/n): ");
            String doCdSearch = scanner.nextLine().trim().toLowerCase();

            System.out.print("Blu-ray/DVDも検索しますか？ (y/n): ");
            String doDvdSearch = scanner.nextLine().trim().toLowerCase();

            System.out.print("洋書も検索しますか？ (y/n): ");
            String doForeignBookSearch = scanner.nextLine().trim().toLowerCase();
            scanner.close();

            // 楽天APIアプリIDを入力してください
            String appId = "1057854877760086255"; // ←ご自身の楽天アプリIDに変更

            // 楽天BooksTotal APIで書籍検索
            String url = String.format(
                    "https://app.rakuten.co.jp/services/api/BooksTotal/Search/20170404?format=json&applicationId=%s&keyword=%s",
                    appId, URLEncoder.encode(keyword, "UTF-8"));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("APIリクエスト失敗: " + response.statusCode());
                return;
            }
            JSONObject root = new JSONObject(response.body());
            JSONArray items = root.optJSONArray("Items");
            if (items == null || items.length() == 0) {
                System.out.println("該当する書籍がありませんでした。");
                return;
            }
            System.out.println("--- 総合検索結果（BooksTotal API） ---");
            for (int i = 0; i < Math.min(10, items.length()); i++) {
                JSONObject item = items.getJSONObject(i).getJSONObject("Item");
                System.out.println((i + 1) + ". " + item.optString("title", "(タイトルなし)"));
                System.out.println("  著者: " + item.optString("author", "-"));
                System.out.println("  出版社: " + item.optString("publisherName", "-"));
                System.out.println("  発売日: " + item.optString("salesDate", "-"));
                System.out.println("  ISBN: " + item.optString("isbn", "-"));
                System.out.println("  価格: " + item.optInt("itemPrice", 0) + "円");
                System.out.println("  URL: " + item.optString("itemUrl", "-"));
            }

            // BooksBook API（詳細書籍検索）
            if (doBookSearch.equals("y")) {
                String bookUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?format=json&applicationId=%s&title=%s",
                        appId, URLEncoder.encode(keyword, "UTF-8"));
                HttpRequest bookReq = HttpRequest.newBuilder().uri(URI.create(bookUrl)).build();
                HttpResponse<String> bookRes = client.send(bookReq, HttpResponse.BodyHandlers.ofString());
                if (bookRes.statusCode() == 200) {
                    JSONObject bookRoot = new JSONObject(bookRes.body());
                    JSONArray bookItems = bookRoot.optJSONArray("Items");
                    if (bookItems != null && bookItems.length() > 0) {
                        System.out.println("\n--- 詳細書籍検索結果（BooksBook API） ---");
                        for (int i = 0; i < Math.min(10, bookItems.length()); i++) {
                            JSONObject item = bookItems.getJSONObject(i).getJSONObject("Item");
                            System.out.println((i + 1) + ". " + item.optString("title", "(タイトルなし)"));
                            System.out.println("  著者: " + item.optString("author", "-"));
                            System.out.println("  出版社: " + item.optString("publisherName", "-"));
                            System.out.println("  発売日: " + item.optString("salesDate", "-"));
                            System.out.println("  ISBN: " + item.optString("isbn", "-"));
                            System.out.println("  価格: " + item.optInt("itemPrice", 0) + "円");
                            System.out.println("  URL: " + item.optString("itemUrl", "-"));
                        }
                    } else {
                        System.out.println("\nBooksBook APIで該当する書籍がありませんでした。");
                    }
                } else {
                    System.out.println("\nBooksBook APIリクエスト失敗: " + bookRes.statusCode());
                }
            }

            // BooksCD API（CD検索）
            if (doCdSearch.equals("y")) {
                String cdUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/BooksCD/Search/20170404?format=json&applicationId=%s&title=%s",
                        appId, URLEncoder.encode(keyword, "UTF-8"));
                HttpRequest cdReq = HttpRequest.newBuilder().uri(URI.create(cdUrl)).build();
                HttpResponse<String> cdRes = client.send(cdReq, HttpResponse.BodyHandlers.ofString());
                if (cdRes.statusCode() == 200) {
                    JSONObject cdRoot = new JSONObject(cdRes.body());
                    JSONArray cdItems = cdRoot.optJSONArray("Items");
                    if (cdItems != null && cdItems.length() > 0) {
                        System.out.println("\n--- CD検索結果（BooksCD API） ---");
                        for (int i = 0; i < Math.min(10, cdItems.length()); i++) {
                            JSONObject item = cdItems.getJSONObject(i).getJSONObject("Item");
                            System.out.println((i + 1) + ". " + item.optString("title", "(タイトルなし)"));
                            System.out.println("  アーティスト: " + item.optString("artistName", "-") );
                            System.out.println("  レーベル: " + item.optString("label", "-") );
                            System.out.println("  発売日: " + item.optString("salesDate", "-") );
                            System.out.println("  JAN: " + item.optString("jan", "-") );
                            System.out.println("  価格: " + item.optInt("itemPrice", 0) + "円");
                            System.out.println("  URL: " + item.optString("itemUrl", "-") );
                        }
                    } else {
                        System.out.println("\nBooksCD APIで該当するCDがありませんでした。");
                    }
                } else {
                    System.out.println("\nBooksCD APIリクエスト失敗: " + cdRes.statusCode());
                }
            }

            // BooksDVD API（Blu-ray/DVD検索）
            if ("y".equals(doDvdSearch)) {
                String dvdUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/BooksDVD/Search/20170404?format=json&applicationId=%s&title=%s",
                        appId, URLEncoder.encode(keyword, "UTF-8"));
                HttpRequest dvdReq = HttpRequest.newBuilder().uri(URI.create(dvdUrl)).build();
                HttpResponse<String> dvdRes = client.send(dvdReq, HttpResponse.BodyHandlers.ofString());
                if (dvdRes.statusCode() == 200) {
                    JSONObject dvdRoot = new JSONObject(dvdRes.body());
                    JSONArray dvdItems = dvdRoot.optJSONArray("Items");
                    if (dvdItems != null && dvdItems.length() > 0) {
                        System.out.println("\n--- Blu-ray/DVD検索結果（BooksDVD API） ---");
                        for (int i = 0; i < Math.min(10, dvdItems.length()); i++) {
                            JSONObject item = dvdItems.getJSONObject(i).getJSONObject("Item");
                            System.out.println((i + 1) + ". " + item.optString("title", "(タイトルなし)"));
                            System.out.println("  出演者: " + item.optString("artistName", "-") );
                            System.out.println("  メーカー: " + item.optString("makerName", "-") );
                            System.out.println("  ディスク種別: " + item.optString("mediaFlag", "-") );
                            System.out.println("  発売日: " + item.optString("salesDate", "-") );
                            System.out.println("  JAN: " + item.optString("jan", "-") );
                            System.out.println("  価格: " + item.optInt("itemPrice", 0) + "円");
                            System.out.println("  URL: " + item.optString("itemUrl", "-") );
                        }
                    } else {
                        System.out.println("\nBooksDVD APIで該当するBlu-ray/DVDがありませんでした。");
                    }
                } else {
                    System.out.println("\nBooksDVD APIリクエスト失敗: " + dvdRes.statusCode());
                }
            }
            // BooksForeignBook API（洋書検索）
            if (doForeignBookSearch.equals("y")) {
                String foreignBookUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/BooksForeignBook/Search/20170404?format=json&applicationId=%s&title=%s",
                        appId, URLEncoder.encode(keyword, "UTF-8"));
                HttpRequest foreignBookReq = HttpRequest.newBuilder().uri(URI.create(foreignBookUrl)).build();
                HttpResponse<String> foreignBookRes = client.send(foreignBookReq, HttpResponse.BodyHandlers.ofString());
                if (foreignBookRes.statusCode() == 200) {
                    JSONObject foreignBookRoot = new JSONObject(foreignBookRes.body());
                    JSONArray foreignBookItems = foreignBookRoot.optJSONArray("Items");
                    if (foreignBookItems != null && foreignBookItems.length() > 0) {
                        System.out.println("\n--- 洋書検索結果（BooksForeignBook API） ---");
                        for (int i = 0; i < Math.min(10, foreignBookItems.length()); i++) {
                            JSONObject item = foreignBookItems.getJSONObject(i).getJSONObject("Item");
                            System.out.println((i + 1) + ". " + item.optString("title", "(タイトルなし)"));
                            System.out.println("  著者: " + item.optString("author", "-") );
                            System.out.println("  出版社: " + item.optString("publisherName", "-") );
                            System.out.println("  発売日: " + item.optString("salesDate", "-") );
                            System.out.println("  ISBN: " + item.optString("isbn", "-") );
                            System.out.println("  価格: " + item.optInt("itemPrice", 0) + "円");
                            System.out.println("  URL: " + item.optString("itemUrl", "-") );
                        }
                    } else {
                        System.out.println("\nBooksForeignBook APIで該当する洋書がありませんでした。");
                    }
                } else {
                    System.out.println("\nBooksForeignBook APIリクエスト失敗: " + foreignBookRes.statusCode());
                }
            }

            // BooksMagazine API（雑誌検索）
            System.out.print("雑誌も検索しますか？ (y/n): ");
            Scanner magScanner = new Scanner(System.in);
            String doMagazineSearch = magScanner.nextLine().trim().toLowerCase();
            // ゲーム本検索も続けて聞く
            System.out.print("ゲーム本も検索しますか？ (y/n): ");
            String doGameSearch = magScanner.nextLine().trim().toLowerCase();
            System.out.print("ソフトウェア本も検索しますか？ (y/n): ");
            String doSoftwareSearch = magScanner.nextLine().trim().toLowerCase();
            System.out.print("ジャンル検索も行いますか？ (y/n): ");
            String doGenreSearch = magScanner.nextLine().trim().toLowerCase();
            magScanner.close();
            // BooksGenre API（ジャンル検索）
            if (doGenreSearch.equals("y")) {
                System.out.print("ジャンルIDを入力してください（例: 001）：");
                Scanner genreScanner = new Scanner(System.in);
                String genreId = genreScanner.nextLine().trim();
                genreScanner.close();
                String genreUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/BooksGenre/Search/20121128?format=json&applicationId=%s&genreId=%s",
                        appId, genreId);
                HttpRequest genreReq = HttpRequest.newBuilder().uri(URI.create(genreUrl)).build();
                HttpResponse<String> genreRes = client.send(genreReq, HttpResponse.BodyHandlers.ofString());
                if (genreRes.statusCode() == 200) {
                    JSONObject genreRoot = new JSONObject(genreRes.body());
                    if (genreRoot.has("genreName")) {
                        System.out.println("\n--- ジャンル検索結果（BooksGenre API） ---");
                        System.out.println("ジャンルID: " + genreId);
                        System.out.println("ジャンル名: " + genreRoot.optString("genreName", "(不明)") );
                    } else {
                        System.out.println("\nジャンル情報が見つかりませんでした。");
                    }
                } else {
                    System.out.println("\nBooksGenre APIリクエスト失敗: " + genreRes.statusCode());
                }
            }
            if (doMagazineSearch.equals("y")) {
            // BooksSoftware API（ソフトウェア本検索）
            if (doSoftwareSearch.equals("y")) {
                String softwareUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/BooksSoftware/Search/20170404?format=json&applicationId=%s&title=%s",
                        appId, URLEncoder.encode(keyword, "UTF-8"));
                HttpRequest softwareReq = HttpRequest.newBuilder().uri(URI.create(softwareUrl)).build();
                HttpResponse<String> softwareRes = client.send(softwareReq, HttpResponse.BodyHandlers.ofString());
                if (softwareRes.statusCode() == 200) {
                    JSONObject softwareRoot = new JSONObject(softwareRes.body());
                    JSONArray softwareItems = softwareRoot.optJSONArray("Items");
                    if (softwareItems != null && softwareItems.length() > 0) {
                        System.out.println("\n--- ソフトウェア本検索結果（BooksSoftware API） ---");
                        for (int i = 0; i < Math.min(10, softwareItems.length()); i++) {
                            JSONObject item = softwareItems.getJSONObject(i).getJSONObject("Item");
                            System.out.println((i + 1) + ". " + item.optString("title", "(タイトルなし)"));
                            System.out.println("  メーカー: " + item.optString("makerName", "-") );
                            System.out.println("  JAN: " + item.optString("jan", "-") );
                            System.out.println("  発売日: " + item.optString("salesDate", "-") );
                            System.out.println("  価格: " + item.optInt("itemPrice", 0) + "円");
                            System.out.println("  URL: " + item.optString("itemUrl", "-") );
                        }
                    } else {
                        System.out.println("\nBooksSoftware APIで該当するソフトウェア本がありませんでした。");
                    }
                } else {
                    System.out.println("\nBooksSoftware APIリクエスト失敗: " + softwareRes.statusCode());
                }
            }
                String magazineUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/BooksMagazine/Search/20170404?format=json&applicationId=%s&title=%s",
                        appId, URLEncoder.encode(keyword, "UTF-8"));
                HttpRequest magazineReq = HttpRequest.newBuilder().uri(URI.create(magazineUrl)).build();
                HttpResponse<String> magazineRes = client.send(magazineReq, HttpResponse.BodyHandlers.ofString());
                if (magazineRes.statusCode() == 200) {
                    JSONObject magazineRoot = new JSONObject(magazineRes.body());
                    JSONArray magazineItems = magazineRoot.optJSONArray("Items");
                    if (magazineItems != null && magazineItems.length() > 0) {
                        System.out.println("\n--- 雑誌検索結果（BooksMagazine API） ---");
                        for (int i = 0; i < Math.min(10, magazineItems.length()); i++) {
                            JSONObject item = magazineItems.getJSONObject(i).getJSONObject("Item");
                            System.out.println((i + 1) + ". " + item.optString("title", "(タイトルなし)"));
                            System.out.println("  出版社: " + item.optString("publisherName", "-") );
                            System.out.println("  発売日: " + item.optString("salesDate", "-") );
                            System.out.println("  JAN: " + item.optString("jan", "-") );
                            System.out.println("  価格: " + item.optInt("itemPrice", 0) + "円");
                            System.out.println("  URL: " + item.optString("itemUrl", "-") );
                        }
                    } else {
                        System.out.println("\nBooksMagazine APIで該当する雑誌がありませんでした。");
                    }
                } else {
                    System.out.println("\nBooksMagazine APIリクエスト失敗: " + magazineRes.statusCode());
                }
            }

            // BooksGame API（ゲーム本検索）
            if (doGameSearch.equals("y")) {
                String gameUrl = String.format(
                        "https://app.rakuten.co.jp/services/api/BooksGame/Search/20170404?format=json&applicationId=%s&title=%s",
                        appId, URLEncoder.encode(keyword, "UTF-8"));
                HttpRequest gameReq = HttpRequest.newBuilder().uri(URI.create(gameUrl)).build();
                HttpResponse<String> gameRes = client.send(gameReq, HttpResponse.BodyHandlers.ofString());
                if (gameRes.statusCode() == 200) {
                    JSONObject gameRoot = new JSONObject(gameRes.body());
                    JSONArray gameItems = gameRoot.optJSONArray("Items");
                    if (gameItems != null && gameItems.length() > 0) {
                        System.out.println("\n--- ゲーム本検索結果（BooksGame API） ---");
                        for (int i = 0; i < Math.min(10, gameItems.length()); i++) {
                            JSONObject item = gameItems.getJSONObject(i).getJSONObject("Item");
                            System.out.println((i + 1) + ". " + item.optString("title", "(タイトルなし)"));
                            System.out.println("  メーカー: " + item.optString("makerName", "-") );
                            System.out.println("  JAN: " + item.optString("jan", "-") );
                            System.out.println("  発売日: " + item.optString("salesDate", "-") );
                            System.out.println("  価格: " + item.optInt("itemPrice", 0) + "円");
                            System.out.println("  URL: " + item.optString("itemUrl", "-") );
                        }
                    } else {
                        System.out.println("\nBooksGame APIで該当するゲーム本がありませんでした。");
                    }
                } else {
                    System.out.println("\nBooksGame APIリクエスト失敗: " + gameRes.statusCode());
                }
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
