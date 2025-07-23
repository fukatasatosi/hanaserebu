package 雑;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class 辞書 {
    /**
     * dictionaryapi.devを使って英単語の意味・例文を検索して表示
     */
    public static void searchWord(String word) {
        try {
            String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONArray arr = new JSONArray(response.toString());
                JSONObject entry = arr.getJSONObject(0);
                System.out.println("単語: " + entry.optString("word"));
                if (entry.has("phonetics")) {
                    JSONArray phonetics = entry.getJSONArray("phonetics");
                    for (int i = 0; i < phonetics.length(); i++) {
                        String text = phonetics.getJSONObject(i).optString("text");
                        if (!text.isEmpty()) {
                            System.out.println("発音: " + text);
                            break;
                        }
                    }
                }
                JSONArray meanings = entry.getJSONArray("meanings");
                for (int i = 0; i < meanings.length(); i++) {
                    JSONObject meaning = meanings.getJSONObject(i);
                    String partOfSpeech = meaning.optString("partOfSpeech");
                    System.out.println("品詞: " + partOfSpeech);
                    JSONArray definitions = meaning.getJSONArray("definitions");
                    for (int j = 0; j < definitions.length(); j++) {
                        JSONObject def = definitions.getJSONObject(j);
                        System.out.println("意味: " + def.optString("definition"));
                        if (def.has("example")) {
                            System.out.println("例文: " + def.getString("example"));
                        }
                    }
                    System.out.println();
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
        System.out.print("検索したい英単語を入力してください: ");
        String word = scanner.nextLine().trim();
        searchWord(word);
        scanner.close();
    }
}