package 雑;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SleepApi {
    public static void main(String[] args) throws Exception {
        // ユーザーから睡眠時間（時間単位）を入力
        Scanner scanner = new Scanner(System.in);
        System.out.print("昨夜の睡眠時間（時間）を入力してください: ");
        double hours = scanner.nextDouble();

        // 睡眠状況を判定
        String status;
        if (hours >= 7.0) {
            status = "良好";
        } else if (hours >= 5.0) {
            status = "普通";
        } else {
            status = "不足";
        }
        System.out.println("あなたの睡眠状況: " + status);

        // --- Oura APIからデータ取得（元のコード） ---
        String accessToken = "GS72IGXQEU465QRPYR3XDWRDTRKRDSE4"; // ←発行したOura APIトークン
        String startDate = "2025-06-24"; // 取得開始日
        String endDate = "2025-07-03";   // 取得終了日

        String urlStr = String.format(
            "https://api.ouraring.com/v2/usercollection/sleep?start_date=%s&end_date=%s",
            startDate, endDate
        );
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Oura Sleep Data: " + response.toString());
        } else {
            System.out.println("Error: " + responseCode);
        }
    }
}