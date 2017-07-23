public class SimplifiedTernarySample {
    public boolean p2() {
        return false;
    }

    public boolean p3() {
        return false;
    }

    public boolean bar1(boolean condition) {
        return condition || p3();
    }

    public boolean bar1a(boolean condition) {
        return (condition) || (p3());
    }

    public boolean bar2(boolean condition) {
        return !condition || p2();
    }

    public boolean bar2a(boolean condition) {
        return !(condition) || (p2());
    }

    public boolean bar2b(Object o) {
        return !(o instanceof String) || (p2());
    }

    public boolean bar2c(Object o) {
        return !(o instanceof String) || (p2());
    }

    public boolean bar3(boolean condition) {
        return !condition && p3();
    }

    public boolean bar3a(Object o) {
        return !(o instanceof String) && p3();
    }

    public boolean bar4(boolean condition) {
        return condition && p2();
    }

    public boolean bar5(boolean condition) {
        return condition;
    }

    private boolean b5a;
    public boolean bar5a(boolean condition) {
        return condition ? (b5a = true) : (b5a = false);
    }

    public boolean bar6(boolean condition) {
        return !condition;
    }

    public boolean bar6a(Object o) {
        return !(o instanceof String);
    }

    private boolean b7;
    public boolean bar7a(Object o) {
        return !(b7 = o instanceof String);
    }

    public boolean bar7b(Object o) {
        return (b7 = o instanceof String);
    }

    public boolean bar7c(Object o) {
        return (b7 = o instanceof String) && false;
    }

    public boolean bar7d(Object o) {
        return (b7 = o instanceof String) || true;
    }

    public String f1(boolean condition) {
        return condition ? "A" + "B" : "C" + "D";
    }

    public String f1a(boolean condition) {
        return (condition) ? ("A" + "B") : ("C" + "D");
    }
}
