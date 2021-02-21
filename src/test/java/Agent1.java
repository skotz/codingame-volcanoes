import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        for (int i = 0; i < 80; i++) {
            System.err.println(scanner.nextLine());
        }

        while (true) {
            String input = scanner.nextLine();

            System.out.println("N20");

            System.err.println(input);
        }
    }
}
