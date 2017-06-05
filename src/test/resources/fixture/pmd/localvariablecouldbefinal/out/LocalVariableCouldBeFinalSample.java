public class LocalVariableCouldBeFinalSample {

    public void unusedVarsAreFinal(Object c) {
        final String a = "a";
    }

    public void assignedVarsAreNotFinal(Object c) {
        String a = "a";
        a = "b";
    }

    public int readOnlyUsageIsFinal() {
        final int count = 0;
        return -count;
    }

    public void postIncrementIsNotFinal() {
        int count = 0;
        count++;
    }

    public void preIncrementIsNotFinal() {
        int count = 0;
        ++count;
    }

    public void postDecrementIsNotFinal() {
        int count = 0;
        count--;
    }

    public void preDecrementIsNotFinal() {
        int count = 0;
        --count;
    }
}
