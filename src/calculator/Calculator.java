package calculator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Calculator {
    private int left_operand;
    private int right_operand;
    private char operation;
    private boolean is_roman = false;

    public Calculator(String str) {
        try {
            Parse_string(str);
            System.out.print(left_operand + " ");
            System.out.print(operation + " ");
            System.out.println(right_operand + " ");

            System.out.println(is_roman ? OperationRoman() : OperationLatin());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void Parse_string(String str) throws Exception {
        String[] operands = str.split(" ");
        if (operands.length != 3) {
            throw new Exception("Invalid arguments");
        } else if (str.contains("\n")) {
            throw new Exception("Invalid format");
        }

        // извлекаем первое число
        if (tryParseInt(operands[0])) {
            if (operands[0].length() != 1) {
                throw new Exception("Invalid arguments");
            } else {
                this.left_operand = Integer.parseInt(operands[0]);
            }
        } else {
            if (operands[0].length() <= 4) {
                boolean roman = true;
                String tmp = operands[0];
                for (int i = 0; i < tmp.length(); i++) {
                    if (tmp.charAt(i) != 'V' &&
                            tmp.charAt(i) != 'I' &&
                            tmp.charAt(i) != 'X') {
                        roman = false;
                        break;
                    }
                }

                if (roman) {
                    this.left_operand = Convert_from_roman(tmp);
                    this.is_roman = true;
                }
            }
        }

        // Извлекаем оператор
        if (operands[1].length() != 1) {
            throw new Exception("Invalid operator");
        } else {
            if (operands[1].charAt(0) == '/' ||
                    operands[1].charAt(0) == '*' ||
                    operands[1].charAt(0) == '+' ||
                    operands[1].charAt(0) == '-') {
                operation = operands[1].charAt(0);
            } else {
                throw new Exception("Invalid operator");
            }
        }

        // Извлекаем второе число
        if (tryParseInt(operands[2])) {
            if (operands[2].length() != 1) {
                throw new Exception("Invalid arguments");
            } else {
                this.right_operand = Integer.parseInt(operands[2]);
                if (is_roman) {
                    throw new Exception("Invalid arguments");
                }
            }
        } else {
            if (operands[2].length() <= 4) {
                boolean roman = true;
                String tmp = operands[2];
                for (int i = 0; i < tmp.length(); i++) {
                    if (tmp.charAt(i) != 'V' &&
                            tmp.charAt(i) != 'I' &&
                            tmp.charAt(i) != 'X') {
                        roman = false;
                        break;
                    }
                }

                if (roman) {
                    this.right_operand = Convert_from_roman(tmp);
                    if (!is_roman) {
                        throw new Exception("Invalid arguments");
                    }
                }
            }
        }
    }

    private boolean tryParseInt(String value) {
        // В строке числовое значение или нет
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int Convert_from_roman(String str) throws Exception {
        int res = RomanNumeral.romanToArabic(str);
        if (res < 1 || res > 10) {
            throw new Exception(str + " - invalid argument");
        }
        return res;
    }

    private int OperationLatin() throws Exception{
        Operation op;
        switch (operation) {
            case '+':
                op = Operation.Sum;
                return op.action(left_operand, right_operand);
            case '-':
                op = Operation.Subtract;
                return op.action(left_operand, right_operand);
            case '*':
                op = Operation.Multiply;
                return op.action(left_operand, right_operand);
            case '/':
                op = Operation.Divide;
                if (right_operand == 0) {
                    throw new Exception("Division by zero attempt");
                }
                return op.action(left_operand, right_operand);
            default:
                return 0;
        }
    }

    private String OperationRoman() throws Exception{
        Operation op = Operation.Sum;
        int result;
        switch (operation){
            case '+':
                break;
            case '-':
                op = Operation.Subtract;
                break;
            case '/':
                op = Operation.Divide;
                break;
            case '*':
                op = Operation.Multiply;
        }
        result = op.action(left_operand, right_operand);
        if (result <= 0) {
            throw new Exception("There are no zero or negative numbers in roman numerals");
        }
        return RomanNumeral.arabicToRoman(result);
    }
}


enum Operation {
    Sum {
        public int action(int l, int r) {
            return l + r;
        }
    },
    Subtract {
        public int action(int l, int r) {
            return l - r;
        }
    },
    Multiply {
        public int action(int l, int r) {
            return l * r;
        }
    },
    Divide {
        public int action(int l, int r) {
            return l / r;
        }
    };

    public abstract int action(int l, int r);
}

// нагло стырено с codeflow. Работает вроде бы.
enum RomanNumeral {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);

    private final int value;

    RomanNumeral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Проверить 300500 раз
    public static List<RomanNumeral> getReverseSortedValues() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                .collect(Collectors.toList());
    }

    public static int romanToArabic(String input) {
        String romanNumeral = input.toUpperCase();
        int result = 0;

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }

        if (romanNumeral.length() > 0) {
            throw new IllegalArgumentException(input + " cannot be converted to a Roman Numeral");
        }

        return result;
    }

    public static String arabicToRoman(int number) {
        if ((number <= 0) || (number > 4000)) {
            throw new IllegalArgumentException(number + " is not in range (0,4000]");
        }

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }

        return sb.toString();
    }
}