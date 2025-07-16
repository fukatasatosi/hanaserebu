package 雑;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        System.out.print("dog、cat、QR、address、hello、calcのいずれかを入力してください: ");
        String input = scanner.nextLine();
        String result = "";
        if (input.equals("dog")) {
            result = InuImageFetcher.fetchDogImageUrl();
        } else if (input.equals("cat")) {
            result = NekoImageFetcher.fetchCatImageUrl();
        } else if (input.equals("QR")) {
            System.out.print("QRコードにしたい文字列またはURLを入力してください: ");
            String qrInput = scanner.nextLine();
            result = QRCodeFetcher.getQrCodeUrl(qrInput);
        } else if (input.equals("address")) {
            System.out.print("郵便番号を入力してください（例: 1000001）: ");
            String zipcode = scanner.nextLine().trim();
            result = AddressFetcher.fetchAddress(zipcode);
        } else if (input.equals("hello")) {
            System.out.print("言語コードを入力してください（例: ja, en, fr, kk）: ");
            String lang = scanner.nextLine().trim();
            result = HelloFetcher.fetchHello(lang);
        } else if (input.equals("calc")) {
            System.out.print("計算式を入力してください（例: 1+2*3）: ");
            String expr = scanner.nextLine();
            result = CalculatorFetcher.fetchCalcResult(expr);
        } else {
            result = "対応していません";
        }
        System.out.println(result);
        scanner.close();
    }
}
