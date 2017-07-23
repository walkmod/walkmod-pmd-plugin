package org.walkmod.pmd.ruleset.java.design.visitors;

import java.util.Collections;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.SymbolDataAware;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.StringLiteralExpr;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

/**
 * http://pmd.sourceforge.net/pmd-5.1.2/rules/java/design.html#PositionLiteralsFirstInComparisons
 */
@Modification
public class PositionLiteralsFirstInComparisons extends PMDRuleVisitor {

    @Override
    public void visit(MethodCallExpr n, Node ctx) {
        super.visit(n, ctx);
        final List<Expression> args = n.getArgs();
        if (boolean.class.equals(symbolDataClazz(n))
                && n.getName().equals("equals")
                && size(args) == 1
                && Object.class.equals(n.getSymbolData().getMethod().getParameterTypes()[0])) {
            final Expression arg0 = args.get(0);
            final Expression scope = n.getScope();
            if (arg0 instanceof StringLiteralExpr) {
                // To clone or not to clone? Without clone the expression vanishes ...
                final List<Expression> newArgs = scope != null
                        ? Collections.singletonList(clone(scope))
                        : Collections.<Expression>singletonList(new NameExpr("this"));
                MethodCallExpr newCall = new MethodCallExpr(arg0, n.getName(), newArgs);
                ctx.getParentNode().replaceChildNode(n, newCall);
            }
        }
    }

    private static Expression clone(Expression scope) {
        try {
            return scope.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Class<?> symbolDataClazz(SymbolDataAware n) {
        final SymbolData sd = n.getSymbolData();
        return sd != null ? sd.getClazz() : null;
    }

    private static int size(List<?> l) {
        return l != null ? l.size() : 0;
    }
}
