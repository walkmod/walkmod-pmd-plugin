package org.walkmod.pmd.ruleset.java.optimizations.visitors;

import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.body.FieldDeclaration;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.DoubleLiteralExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.IntegerLiteralExpr;
import org.walkmod.javalang.ast.expr.LongLiteralExpr;
import org.walkmod.javalang.ast.expr.NullLiteralExpr;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Modification
public class RedundantFieldInitializer extends PMDRuleVisitor {

    @Override
    public void visit(FieldDeclaration n, Node node) {
        FieldDeclaration aux = (FieldDeclaration) node;

        List<VariableDeclarator> vds = aux.getVariables();

        if (vds != null && vds.size() == 1) {
            VariableDeclarator vd = vds.get(0);
            Expression expr = vd.getInit();

            if (expr instanceof NullLiteralExpr) {
                modifyVar(vds, vd);
            } else if (expr instanceof IntegerLiteralExpr) {
                IntegerLiteralExpr iexpr = (IntegerLiteralExpr) expr;
                if ("0".equals(iexpr.getValue())) {
                    modifyVar(vds, vd);
                }
            } else if (expr instanceof LongLiteralExpr) {
                LongLiteralExpr lle = (LongLiteralExpr) expr;
                if ("0L".equals(lle.getValue())) {
                    modifyVar(vds, vd);
                }
            } else if (expr instanceof DoubleLiteralExpr) {
                DoubleLiteralExpr lle = (DoubleLiteralExpr) expr;
                if ("0d".equals(lle.getValue())) {
                    modifyVar(vds, vd);
                }
            } else if (expr instanceof BooleanLiteralExpr) {
                BooleanLiteralExpr lle = (BooleanLiteralExpr) expr;
                if (!lle.getValue()) {
                    modifyVar(vds, vd);
                }
            }
        }
    }

    private void modifyVar(List<VariableDeclarator> vds, VariableDeclarator vd) {
        vds.clear();
        try {
            vd = vd.clone();
        } catch (CloneNotSupportedException e) {
        }
        vd.setInit(null);
        vds.add(vd);
    }
}
