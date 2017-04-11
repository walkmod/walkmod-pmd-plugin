package fixture.pmd.collapsibleifstatements.in;

public class CollapsibleIfStatementsSample {
    void f(boolean a, boolean b, boolean c) {
        if (a && b) {}// original implementation

        // do stuff



        if (a && b) {
            System.out.println("here I am");
        }// original implementation





        if (a && (b || c)) {
            System.out.println("here I am");
        }

        if ((a || b) && c) {
            System.out.println("here I am");
        }
    }
}
