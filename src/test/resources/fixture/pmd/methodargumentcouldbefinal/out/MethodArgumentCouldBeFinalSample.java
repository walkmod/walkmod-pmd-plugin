public class MethodArgumentCouldBeFinalSample {

    public class ParamsBeingAssignedAreFinal {
        int x;

        public void setX(final int x) {
            this.x = x;
        }
    }

    public class ParamsBeingAssignedToAreNotFinal {
        int x;

        public void setX(int x) {
            x = 1;
            this.x = x;
        }
    }

    public interface InterfaceMethodParametersAreNotFinal {
        void method(int i);
    }

    public abstract class AbstractMethodParametersAreNotFinal {
        abstract void method(int i);
    }

    public int readOnlyUsageIsFinal(final int count) {
        return -count;
    }

    public void postIncrementIsNotFinal(int count) {
        count++;
    }

    public void preIncrementIsNotFinal(int count) {
        ++count;
    }

    public void postDecrementIsNotFinal(int count) {
        count--;
    }

    public void preDecrementIsNotFinal(int count) {
        --count;
    }

    public interface F2 {
        int f(int a, int b);
    }

    public interface F1 {
        int f(int a);
    }

    public void testOnlyLambdaParametersWithTypeCanBeMadeFinal() {
        F1 o1a =  a->a;
        F1 o1b = ( a)->a;
        F1 o1c = (final int a)->a;
        F2 o2a = ( a,  b)->a + b;
        F2 o2b = (final int a, final int b)->a + b;
    }
}
