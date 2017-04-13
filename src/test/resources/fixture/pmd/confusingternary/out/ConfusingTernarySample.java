import java.util.List;

public class ConfusingTernarySample {
    public boolean testConditionalExpr(boolean secMode) {
        return secMode == true ? true : false;
    }

    public boolean testCompositeBooleanExpr(List value) {
        if (value == null || value.isEmpty()) {
            return false;
        } else {
            return true;
        }






    }

    public boolean testEqualsCondition(List value) {
        if (value == null) {
            return false;
        } else {
            return true;
        }






    }
}
