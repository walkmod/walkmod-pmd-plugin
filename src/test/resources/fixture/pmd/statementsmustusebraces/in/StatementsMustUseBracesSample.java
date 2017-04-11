import java.util.Arrays;

public class StatementsMustUseBraces {
    public void f(int i) {
        do
            System.out.println("Hi!");
        while (false);

        do ;
        while (false);

        for (int i1 = 0; i1 < 10; i1++)
            ;

        for (int i2 = 0; i2 < 10; i2++)
            System.out.println("Hi!");

        for (String s: Arrays.asList("a", "b"))
            System.out.println("Hi!");

        if (true)
            System.out.println("Hi!");

        if (true)
            System.out.println("Hi!");
        else
            System.out.println("Ho!");

        if (true) {
            System.out.println("Hi!");
        } else
            System.out.println("Ho!");

        if (true)
            System.out.println("Hi!");
        else {
            System.out.println("Ho!");
        }

        // don't add braces to else-if
        if (i > 0) {
            System.out.println("Hi!");
        } else if (i < -5) {
            System.out.println("Ho!");
        } else {
            System.out.println("Hu!");
        }

        while (i-- > 0)
            System.out.println("Hi!");

        while (i-- > 0)
            ;
    }

    boolean f2() {
        if ("".isEmpty())
            return true;
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }
}