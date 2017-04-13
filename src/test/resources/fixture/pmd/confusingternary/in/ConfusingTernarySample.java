import java.util.List;

public class ConfusingTernarySample {
    public boolean testConditionalExpr(boolean secMode) {
        return secMode != true ? false : true;
    }

    public boolean testCompositeBooleanExpr(List value) {
        if (value != null && !value.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean testEqualsCondition(List value) {
        if (value != null) {
            return true;
        } else {
            return false;
        }
    }
}
