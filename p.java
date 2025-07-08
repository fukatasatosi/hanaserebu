import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class p {
    // メイン処理: ポケモンAPIでハピナス同士のバトルをシミュレート
    public static void main(String[] args) {
        try {
            // ハピナスのデータ取得
            String url = "https://pokeapi.co/api/v2/pokemon/blissey";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject blissey = new JSONObject(response.body());
            int hp = 0, attack = 0, defense = 0;
            for (Object o : blissey.getJSONArray("stats")) {
                JSONObject stat = (JSONObject) o;
                String name = stat.getJSONObject("stat").getString("name");
                if (name.equals("hp"))
                    hp = stat.getInt("base_stat");
                if (name.equals("attack"))
                    attack = stat.getInt("base_stat");
                if (name.equals("defense"))
                    defense = stat.getInt("base_stat");
            }
            // ハピナス2体の初期HP
            int hp1 = hp, hp2 = hp;
            int turn = 1;
            System.out.printf("ハピナスA(HP:%d) vs ハピナスB(HP:%d) バトル開始！\n", hp1, hp2);
            while (hp1 > 0 && hp2 > 0) {
                System.out.printf("--- ターン%d ---\n", turn);
                // AがBに攻撃
                int damageA = Math.max(1, attack - defense / 2);
                hp2 -= damageA;
                System.out.printf("ハピナスAの攻撃！ ハピナスBに%dダメージ (残りHP:%d)\n", damageA, Math.max(0, hp2));
                if (hp2 <= 0)
                    break;
                // BがAに攻撃
                int damageB = Math.max(1, attack - defense / 2);
                hp1 -= damageB;
                System.out.printf("ハピナスBの攻撃！ ハピナスAに%dダメージ (残りHP:%d)\n", damageB, Math.max(0, hp1));
                turn++;
            }
            if (hp1 > 0) {
                System.out.println("ハピナスAの勝ち！");
            } else {
                System.out.println("ハピナスBの勝ち！");
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
