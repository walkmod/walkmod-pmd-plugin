package org.walkmod.pmd.common;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.AssignExpr;
import org.walkmod.javalang.ast.expr.EnclosedExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr;

/**
 */
public class FinalAnalyzer {

    public static boolean isAssigned(Node reference) {
        Node parent = reference.getParentNode();

        while (parent instanceof EnclosedExpr) {
            parent = parent.getParentNode();
        }
        if (parent instanceof AssignExpr) {
            AssignExpr assign = (AssignExpr) parent;
            return assign.getTarget() == reference;
        }
        if (parent instanceof UnaryExpr) {
            UnaryExpr ue = (UnaryExpr) parent;
            UnaryExpr.Operator op = ue.getOperator();

            if (op.equals(UnaryExpr.Operator.posIncrement)
                    || op.equals(UnaryExpr.Operator.posDecrement)
                    || op.equals(UnaryExpr.Operator.preIncrement)
                    || op.equals(UnaryExpr.Operator.preDecrement)) {
                return true;
            }
        }

        return false;
    }
}
