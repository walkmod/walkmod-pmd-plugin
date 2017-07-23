public class PositionLiteralsFirstInComparisonsSample {

    public void doRefactor1(String s) {
        out(s.equals("hello"));
        out(this.equals("hello"));
        out(equals("hello"));
        if (s.equals("hello")) {
            out(true);
        }
        if (this.equals("hello")) {
            out(true);
        }
        if (equals("hello")) {
            out(true);
        }
    }

    public void doRefactor2(PositionLiteralsFirstInComparisonsSample o) {
        out(o.equals("hello"));
    }

    public void doNotRefactor(String s, String t, PositionLiteralsFirstInComparisonsSample o) {
        out("hello".equals(s));
        out("hello".equals(o));
        out("hello".equals(this));

        out(s.equals(t));
        out(t.equals(s));

        out(o.equals(s, t));
        out(this.equals(s, t));
        out(equals(s, t));

        out(o.equals());
        out(this.equals());
        out(equals());

        N1 n1 = new N1();
        n1.equals("hello");

        N2 n2 = new N2();
        out(n2.equals("hello"));

        N3 n3 = new N3();
        out(n3.equals("hello") > 0);
    }

    public static class N1 {
        public void equals(String o) {
        }
    }

    public static class N2 {
        public boolean equals(String o) {
            return false;
        }
    }

    public static class N3 {
        public int equals(String o) {
            return 0;
        }
    }

    public void out(boolean b) {
    }

    public boolean equals() {
        return false;
    }

    public boolean equals(String s, String t) {
        return false;
    }
}
