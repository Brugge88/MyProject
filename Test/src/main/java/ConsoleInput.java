import java.util.Scanner;

public class ConsoleInput {

    public int getMaxValue() {
        int maxValue = 0;
        boolean flag = false;
        Scanner scanner;

        while (!flag) {
            System.out.println("\nВведите максимальное положительное целое значение больше нуля для вставки: ");
            scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                maxValue = scanner.nextInt();
                if (maxValue == 0 || maxValue < 0) {
                    System.out.println("Некорректное значение!");
                } else {
                    scanner.close();
                    flag = true;
                }
            } else {
                System.out.println("Некорректное значение!");
            }
        }
        return maxValue;
    }


}
