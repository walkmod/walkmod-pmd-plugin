public class MethodArgumentCouldBeFinalSample {

    public class ParamsBeingAssignedAreFinal {
        int x;

        public void setX(int x) {
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

    public int readOnlyUsageIsFinal(int count) {
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
}
