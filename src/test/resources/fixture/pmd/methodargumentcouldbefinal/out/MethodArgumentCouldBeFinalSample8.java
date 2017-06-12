public class MethodArgumentCouldBeFinalSample8 {

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
