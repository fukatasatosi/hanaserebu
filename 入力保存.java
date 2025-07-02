import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class 入力保存 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("あなた: ");
        String userInput = scanner.nextLine();
        scanner.close();
        try (FileWriter fw = new FileWriter("user_input.txt", false)) {
            fw.write(userInput);
        } catch (IOException e) {
            System.out.println("入力保存エラー: " + e.getMessage());
        }
    }

    // 入力値をファイルに保存するだけのユーティリティ
    public static void saveInput(String input) {
        try (FileWriter fw = new FileWriter("user_input.txt", false)) {
            fw.write(input);
        } catch (IOException e) {
            System.out.println("入力保存エラー: " + e.getMessage());
        }
    }
}
