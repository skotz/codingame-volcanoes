import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.err.println(scanner.nextLine());
        for (int i = 0; i < 80; i++) {
            System.err.println(scanner.nextLine());
        }

        while (true) {
            String position = scanner.nextLine();
            String moves = scanner.nextLine();

            System.err.println(position);
            System.err.println(moves);

            // String move = moves.split(" ")[0];

            System.out.println("RANDOM");
        }
    }
}
