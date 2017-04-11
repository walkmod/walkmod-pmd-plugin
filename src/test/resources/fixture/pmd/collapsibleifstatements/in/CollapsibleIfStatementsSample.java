package fixture.pmd.collapsibleifstatements.in;

public class CollapsibleIfStatementsSample {
    void f(boolean a, boolean b, boolean c) {
        if (a) {            // original implementation
            if (b) {
                // do stuff
            }
        }

        if (a) {            // original implementation
            if (b) {
                System.out.println("here I am");
            }
        }

        if (a) {
            if (b || c) {
                System.out.println("here I am");
            }
        }

        if (a || b) {
            if (c) {
                System.out.println("here I am");
            }
        }
    }
}
