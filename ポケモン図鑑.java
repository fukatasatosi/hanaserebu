import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;

public class ポケモン図鑑 {
    // メイン処理: ポケモン名を入力し、APIから情報を取得して表示
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("ポケモン名を入力してください（例: pikachu）: ");
            String name = scanner.nextLine().trim().toLowerCase();
            scanner.close();

            // ポケモン基本情報取得
            String url = "https://pokeapi.co/api/v2/pokemon/" + name;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("ポケモンが見つかりませんでした。");
                return;
            }
            JSONObject poke = new JSONObject(response.body());
            // タイプ
            JSONArray types = poke.getJSONArray("types");
            StringBuilder typeStr = new StringBuilder();
            for (int i = 0; i < types.length(); i++) {
                typeStr.append(types.getJSONObject(i).getJSONObject("type").getString("name"));
                if (i < types.length() - 1)
                    typeStr.append(", ");
            }
            // 特性
            JSONArray abilities = poke.getJSONArray("abilities");
            StringBuilder abStr = new StringBuilder();
            for (int i = 0; i < abilities.length(); i++) {
                abStr.append(abilities.getJSONObject(i).getJSONObject("ability").getString("name"));
                if (i < abilities.length() - 1)
                    abStr.append(", ");
            }
            // 必殺技（初期から覚えている技のみ）
            JSONArray moves = poke.getJSONArray("moves");
            String move = "なし";
            for (int i = 0; i < moves.length(); i++) {
                JSONObject moveObj = moves.getJSONObject(i);
                JSONArray details = moveObj.getJSONArray("version_group_details");
                for (int j = 0; j < details.length(); j++) {
                    JSONObject detail = details.getJSONObject(j);
                    String learnMethod = detail.getJSONObject("move_learn_method").getString("name");
                    int levelLearned = detail.getInt("level_learned_at");
                    if ((learnMethod.equals("level-up") && levelLearned == 1) || learnMethod.equals("egg")) {
                        move = moveObj.getJSONObject("move").getString("name");
                        break;
                    }
                }
                if (!move.equals("なし"))
                    break;
            }
            // 性別（gender APIで判定）
            String genderUrl = "https://pokeapi.co/api/v2/pokemon-species/" + name;
            HttpRequest genderReq = HttpRequest.newBuilder().uri(URI.create(genderUrl)).build();
            HttpResponse<String> genderRes = client.send(genderReq, HttpResponse.BodyHandlers.ofString());
            String genderStr = "不明";
            if (genderRes.statusCode() == 200) {
                JSONObject species = new JSONObject(genderRes.body());
                int genderRate = species.getInt("gender_rate");
                if (genderRate == -1)
                    genderStr = "性別不明";
                else if (genderRate == 0)
                    genderStr = "♂のみ";
                else if (genderRate == 8)
                    genderStr = "♀のみ";
                else
                    genderStr = "♂・♀";
            }
            // 身長・体重
            double height = poke.getInt("height") / 10.0;
            double weight = poke.getInt("weight") / 10.0;
            // 遭遇場所
            String locUrl = "https://pokeapi.co/api/v2/pokemon/" + name + "/encounters";
            HttpRequest locReq = HttpRequest.newBuilder().uri(URI.create(locUrl)).build();
            HttpResponse<String> locRes = client.send(locReq, HttpResponse.BodyHandlers.ofString());
            StringBuilder locStr = new StringBuilder();
            if (locRes.statusCode() == 200) {
                JSONArray locs = new JSONArray(locRes.body());
                for (int i = 0; i < locs.length(); i++) {
                    JSONObject area = locs.getJSONObject(i).getJSONObject("location_area");
                    locStr.append(area.getString("name"));
                    if (i < locs.length() - 1)
                        locStr.append(", ");
                }
                if (locs.length() == 0)
                    locStr.append("（情報なし）");
            } else {
                locStr.append("（取得失敗）");
            }
            // 画像URL取得
            String imageUrl = poke.getJSONObject("sprites").getString("front_default");

            // 結果表示
            System.out.println("--- ポケモン情報 ---");
            System.out.println("名前: " + name);
            System.out.println("タイプ: " + typeStr);
            System.out.println("特性: " + abStr);
            System.out.println("必殺技: " + move);
            System.out.println("性別: " + genderStr);
            System.out.println("身長: " + height + " m");
            System.out.println("体重: " + weight + " kg");
            System.out.println("遭遇場所: " + locStr);
            System.out.println("画像URL: " + imageUrl);

            // Swingで画像表示
            if (imageUrl != null && !imageUrl.equals("null")) {
                try {
                    URL imgUrl = new URL(imageUrl);
                    ImageIcon icon = new ImageIcon(imgUrl);
                    JFrame frame = new JFrame("ポケモン画像: " + name);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    JLabel label = new JLabel(icon);
                    frame.getContentPane().add(label, BorderLayout.CENTER);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception ex) {
                    System.out.println("画像の表示に失敗: " + ex.getMessage());
                }
            }

            // 覚えることができる技一覧
            System.out.println("\n--- 覚えることができる技一覧 ---");
            for (int i = 0; i < moves.length(); i++) {
                JSONObject moveObj = moves.getJSONObject(i);
                String moveName = moveObj.getJSONObject("move").getString("name");
                System.out.println(moveName);
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
