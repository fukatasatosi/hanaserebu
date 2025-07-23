package 雑;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class 映画 {
    /**
     * ジャンルIDで映画を検索し、タイトル・公開年・概要を表示
     */
    public static void searchByGenre(String genreId) {
        try {
            String apiUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY
                    + "&language=ja&with_genres=" + genreId;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray results = json.getJSONArray("results");
                if (results.length() == 0) {
                    System.out.println("該当する映画が見つかりませんでした。");
                } else {
                    for (int i = 0; i < Math.min(5, results.length()); i++) {
                        JSONObject movie = results.getJSONObject(i);
                        String title = movie.optString("title");
                        String overview = movie.optString("overview");
                        String release = movie.optString("release_date");
                        System.out.println("タイトル: " + title);
                        System.out.println("公開日: " + release);
                        System.out.println("概要: " + overview);
                        System.out.println("----------------------");
                    }
                }
            } else {
                System.out.println("APIリクエスト失敗: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    /**
     * 俳優名で映画を検索し、タイトル・公開年・概要を表示
     */
    public static void searchByActor(String actorName) {
        try {
            String encoded = URLEncoder.encode(actorName, "UTF-8");
            String personUrl = "https://api.themoviedb.org/3/search/person?api_key=" + API_KEY + "&language=ja&query="
                    + encoded;
            URL url = new URL(personUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray results = json.getJSONArray("results");
                if (results.length() == 0) {
                    System.out.println("該当する俳優が見つかりませんでした。");
                    return;
                }
                int personId = results.getJSONObject(0).getInt("id");
                // 俳優IDで出演映画を取得
                String movieUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY
                        + "&language=ja&with_cast=" + personId;
                URL url2 = new URL(movieUrl);
                HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
                conn2.setRequestMethod("GET");
                int responseCode2 = conn2.getResponseCode();
                if (responseCode2 == 200) {
                    BufferedReader in2 = new BufferedReader(new InputStreamReader(conn2.getInputStream(), "UTF-8"));
                    String inputLine2;
                    StringBuilder response2 = new StringBuilder();
                    while ((inputLine2 = in2.readLine()) != null) {
                        response2.append(inputLine2);
                    }
                    in2.close();
                    JSONObject json2 = new JSONObject(response2.toString());
                    JSONArray movies = json2.getJSONArray("results");
                    if (movies.length() == 0) {
                        System.out.println("該当する映画が見つかりませんでした。");
                    } else {
                        for (int i = 0; i < Math.min(5, movies.length()); i++) {
                            JSONObject movie = movies.getJSONObject(i);
                            String title = movie.optString("title");
                            String overview = movie.optString("overview");
                            String release = movie.optString("release_date");
                            System.out.println("タイトル: " + title);
                            System.out.println("公開日: " + release);
                            System.out.println("概要: " + overview);
                            System.out.println("----------------------");
                        }
                    }
                } else {
                    System.out.println("映画取得APIリクエスト失敗: " + responseCode2);
                }
            } else {
                System.out.println("俳優検索APIリクエスト失敗: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    // TMDb APIキー
    private static final String API_KEY = "433e1c8d8ff4ca2fb7b41d60db25577d";

    /**
     * TMDb APIを使って映画を検索し、タイトル・公開年・概要を表示
     */
    public static void searchMovie(String query) {
        try {
            String encoded = URLEncoder.encode(query, "UTF-8");
            String apiUrl = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&language=ja&query="
                    + encoded;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray results = json.getJSONArray("results");
                if (results.length() == 0) {
                    System.out.println("該当する映画が見つかりませんでした。");
                } else {
                    for (int i = 0; i < Math.min(5, results.length()); i++) {
                        JSONObject movie = results.getJSONObject(i);
                        String title = movie.optString("title");
                        String overview = movie.optString("overview");
                        String release = movie.optString("release_date");
                        System.out.println("タイトル: " + title);
                        System.out.println("公開日: " + release);
                        System.out.println("概要: " + overview);
                        System.out.println("----------------------");
                    }
                }
            } else {
                System.out.println("APIリクエスト失敗: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.println("検索方法を選択してください:");
        System.out.println("1: 映画タイトルで検索");
        System.out.println("2: ジャンルIDで検索");
        System.out.println("3: 俳優名で検索");
        System.out.print("番号を入力: ");
        String mode = scanner.nextLine().trim();
        if ("1".equals(mode)) {
            System.out.print("検索したい映画タイトルを入力してください: ");
            String query = scanner.nextLine().trim();
            searchMovie(query);
        } else if ("2".equals(mode)) {
            System.out.print("ジャンルIDを入力してください（例: 28=アクション, 35=コメディ）: ");
            String genreId = scanner.nextLine().trim();
            searchByGenre(genreId);
        } else if ("3".equals(mode)) {
            System.out.print("俳優名を入力してください: ");
            String actor = scanner.nextLine().trim();
            searchByActor(actor);
        } else {
            System.out.println("無効な選択です。");
        }
        scanner.close();
    }
}