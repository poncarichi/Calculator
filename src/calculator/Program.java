package calculator;

import java.util.Scanner;

public class Program
{
    public static void main(String[] args)
    {
        char answer;
        Calculator calc;
        System.out.println("Enter two numbers and operation sign" +
                "\n(For example: 1 + 2 or III - I):");
        do {
            Scanner in = new Scanner(System.in);
            String str = in.nextLine();
            calc = new Calculator(str);
            System.out.println("Do you want to continue? (Y / N)");
            answer = in.next().toUpperCase().charAt(0);
            if (answer != 'N'){
                System.out.println("Enter two numbers and operation sign:");
            }
        } while (answer != 'N');
    }
}

