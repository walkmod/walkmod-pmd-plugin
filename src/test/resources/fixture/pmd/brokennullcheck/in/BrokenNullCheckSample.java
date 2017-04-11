public class BrokenNullCheckSample {
    public void bar1(String s) {
        if (s != null || !s.equals("")) {
            System.out.println(s);
        }
    }

    public void bar2(String s) {
        if (s != null || s.equals("")) {
            System.out.println(s);
        }
    }

    public void bar3(String s) {
        if (s == null && s.equals("")) {
            System.out.println(s);
        }
    }

    public void bar4(String s) {
        if (s != null || (!s.equals("") && 3 < 4)) {
            System.out.println(s);
        }
    }

    public void bar5(String s) {
        if (s == null && (!s.equals("") && 3 < 4)) {
            System.out.println(s);
        }
    }

    public void bar6(String s) {
        if (s != null || (s.equals("") && 3 < 4)) {
            System.out.println(s);
        }
    }

    public void bar7(String s) {
        if (s == null && (s.equals("") && 3 < 4)) {
            System.out.println(s);
        }
    }

    public void bar8(String s) {
        if ((s != null && 3 < 4) || !s.equals("")) {
            System.out.println(s);
        }
    }

    // testBinaryOperatorsCase3And
    public void bar9(String s) {
        if ((s == null && 3 < 4) && !s.equals("")) {
            System.out.println(s);
        }
    }

    // testBinaryOperatorsCase4()
    public void bara(String s) {
        if ((s != null && 3 < 4) || s.equals("")) {
            System.out.println(s);
        }
    }

    // testBinaryOperatorsCase4And
    public void barb(String s) {
        if ((s != null && 3 < 4) || s.equals("")) {
            System.out.println(s);
        }
    }

    // testIssueWithValidBooleanExpressions()
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return true;
        }
        return false;
    }

    // testIssueWithValidTernaryExpressions
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        Object authConfigMap = null;
        result = prime * result + ((authConfigMap == null) ? 0 : authConfigMap.hashCode());
        return result;
    }

    // testIssueInvalidNullCheck
    String networkMode;
    String PREDEFINED_NETWORKS;

    boolean isUserDefinedNetwork() {
        return networkMode != null && !PREDEFINED_NETWORKS.contains(networkMode) && !networkMode.startsWith("container:");
    }
}