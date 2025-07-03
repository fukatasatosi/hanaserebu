import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class hanaserebu {
    private static final String API_KEY = "AIzaSyClEis8qc3GR4vzGh_QUL0WeiTMlUnYjQQ";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
            + API_KEY;
    private static final String POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/pikachu";

    public static void main(String[] args) {
        System.out.println("終了するには 'exit' と入力");
        while (true) {
            // 入力保存クラスで入力を取得しファイル保存
            String userInput = getUserInputAndSave();
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("チャットを終了します。");
                break;
            }
            // 天気情報を取得
            String weatherInfo = 天気情報取得();
            // Geminiへの入力に天気情報を付加
            if(userInput.contains("天気") || userInput.contains("weather") || userInput.contains("天候") || userInput.contains("気象")) {
                userInput += "\n" + "you can refer to the following information: " + weatherInfo;
            }
            String response = getGeminiResponse(userInput);
            System.out.println("ピカチュウ: " + response);
        }
    }

    // 入力保存クラスを使って入力を取得しファイル保存
    private static String getUserInputAndSave() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("あなた: ");
        String userInput = scanner.nextLine();
        入力保存.saveInput(userInput);
        return userInput;
    }

    // 天気.javaのmain相当の情報を取得して文字列で返す
    private static String 天気情報取得() {
        try {
            // 天気クラスのmain処理を呼び出す代わりに、天気情報を取得する静的メソッドを呼ぶ想定
            return 天気.getWeatherSummary();
        } catch (Exception e) {
            return "天気情報取得エラー: " + e.getMessage();
        }
    }

    private static String getGeminiResponse(String userInput) {
        try {
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            String jsonInput = "{" +
                    "\"contents\": [{\"parts\": [{\"text\": \"" + "Please Answer the following sentence as if you are "
                    + POKEMON_URL + " " + escapeJson(userInput) + "\"}]}]}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code != 200) {
                return "エラー: " + code + " - " + conn.getResponseMessage();
            }
            InputStream is = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();
            conn.disconnect();
            return parseGeminiResponse(response.toString());
        } catch (Exception e) {
            return "エラー: " + e.getMessage();
        }
    }

    private static String parseGeminiResponse(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray candidates = obj.getJSONArray("candidates");
            if (candidates.length() == 0)
                return "返答が取得できませんでした。(candidatesなし)";
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            if (parts.length() == 0)
                return "返答が取得できませんでした。(partsなし)";
            JSONObject part = parts.getJSONObject(0);
            if (!part.has("text"))
                return "返答が取得できませんでした。(textなし)";
            return part.getString("text");
        } catch (Exception e) {
            return "返答が取得できませんでした。(JSONパースエラー): " + e.getMessage();
        }
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
