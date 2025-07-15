package PIKACHU;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import java.util.Scanner;

public class p {
    // メイン処理: ポケモンAPIで2体のポケモン名を入力しバトルをシミュレート
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("1体目のポケモン名を入力してください: ");
            String name1 = scanner.nextLine().trim().toLowerCase();
            System.out.print("2体目のポケモン名を入力してください: ");
            String name2 = scanner.nextLine().trim().toLowerCase();
            scanner.close();

            Pokemon poke1 = fetchPokemon(name1);
            Pokemon poke2 = fetchPokemon(name2);
            if (poke1 == null || poke2 == null) {
                System.out.println("どちらかのポケモンが見つかりませんでした。");
                return;
            }
            int hp1 = poke1.hp, hp2 = poke2.hp;
            int turn = 1;
            System.out.printf("%s(HP:%d) vs %s(HP:%d) バトル開始！\n", poke1.name, hp1, poke2.name, hp2);
            while (hp1 > 0 && hp2 > 0) {
                System.out.printf("--- ターン%d ---\n", turn);
                // 1が2に攻撃
                int damage1 = Math.max(1, poke1.attack - poke2.defense / 2);
                hp2 -= damage1;
                System.out.printf("%sの攻撃！ %sに%dダメージ (残りHP:%d)\n", poke1.name, poke2.name, damage1, Math.max(0, hp2));
                if (hp2 <= 0)
                    break;
                // 2が1に攻撃
                int damage2 = Math.max(1, poke2.attack - poke1.defense / 2);
                hp1 -= damage2;
                System.out.printf("%sの攻撃！ %sに%dダメージ (残りHP:%d)\n", poke2.name, poke1.name, damage2, Math.max(0, hp1));
                turn++;
            }
            if (hp1 > 0) {
                System.out.println(poke1.name + "の勝ち！");
            } else {
                System.out.println(poke2.name + "の勝ち！");
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    // ポケモンAPIからステータス取得
    private static Pokemon fetchPokemon(String name) {
        try {
            String url = "https://pokeapi.co/api/v2/pokemon/" + name;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                return null;
            JSONObject obj = new JSONObject(response.body());
            int hp = 0, attack = 0, defense = 0;
            for (Object o : obj.getJSONArray("stats")) {
                JSONObject stat = (JSONObject) o;
                String statName = stat.getJSONObject("stat").getString("name");
                if (statName.equals("hp"))
                    hp = stat.getInt("base_stat");
                if (statName.equals("attack"))
                    attack = stat.getInt("base_stat");
                if (statName.equals("defense"))
                    defense = stat.getInt("base_stat");
            }
            String pokeName = obj.getString("name");
            return new Pokemon(pokeName, hp, attack, defense);
        } catch (Exception e) {
            return null;
        }
    }

    // ポケモン情報クラス
    private static class Pokemon {
        String name;
        int hp, attack, defense;

        Pokemon(String name, int hp, int attack, int defense) {
            this.name = name;
            this.hp = hp;
            this.attack = attack;
            this.defense = defense;
        }
    }
}
