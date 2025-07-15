package URL使用;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        System.out.print("犬、猫、QRのいずれかを入力してください: ");
        String input = scanner.nextLine();
        String result = "";
        if (input.equals("犬")) {
            result = InuImageFetcher.fetchDogImageUrl();
        } else if (input.equals("猫")) {
            result = NekoImageFetcher.fetchCatImageUrl();
        } else if (input.equals("QR")) {
            System.out.print("QRコードにしたい文字列またはURLを入力してください: ");
            String qrInput = scanner.nextLine();
            result = QRCodeFetcher.getQrCodeUrl(qrInput);
        } else {
            result = "対応していません";
        }
        System.out.println(result);
        scanner.close();
    }
}
