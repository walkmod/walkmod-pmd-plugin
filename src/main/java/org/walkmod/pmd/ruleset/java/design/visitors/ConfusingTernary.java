package org.walkmod.pmd.ruleset.java.design.visitors;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.BinaryExpr.Operator;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Modification
public class ConfusingTernary extends PMDRuleVisitor {

    @Override
    public void visit(IfStmt n, Node context) {

        if (context instanceof IfStmt) {
            n = (IfStmt) context;

            Expression e = n.getCondition();
            if (e instanceof BinaryExpr) {

                if (isNegative(e) && n.getElseStmt() != null) {
                    try {
                        IfStmt newIfStmt = n.clone();

                        BinaryExpr bexpr = (BinaryExpr) newIfStmt.getCondition();
                        negate(bexpr);

                       
                        Statement then = newIfStmt.getThenStmt();

                        newIfStmt.setThenStmt(n.getElseStmt());
                        newIfStmt.setElseStmt(then);
                        context.getParentNode().replaceChildNode(context, newIfStmt);

                        n.getThenStmt().accept(this, newIfStmt.getElseStmt());
                        n.getElseStmt().accept(this, newIfStmt.getThenStmt());

                    } catch (CloneNotSupportedException exception) {
                    }
                } else {
                    super.visit(n, context);
                }
            }
        }

    }

    private boolean isNegative(Expression n) {
        if (n instanceof UnaryExpr) {
            UnaryExpr aux = (UnaryExpr) n;
            return aux.getOperator().equals(UnaryExpr.Operator.not);
        } else if (n instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) n;
            Operator op = be.getOperator();
            if (op.equals(Operator.notEquals)) {
                return true;
            } else if (op.equals(Operator.and)) {
                return isNegative(be.getRight()) && isNegative(be.getLeft());
            }
        }
        return false;
    }

    private void negate(Expression n) {
        if (n instanceof UnaryExpr) {
            UnaryExpr aux = (UnaryExpr) n;
            n.getParentNode().replaceChildNode(aux, aux.getExpr());
        } else if (n instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) n;
            Operator op = be.getOperator();
            if (op.equals(Operator.notEquals)) {
                be.setOperator(Operator.equals);

            } else if (op.equals(Operator.and)) {
                be.setOperator(Operator.or);
                negate(be.getRight());
                negate(be.getLeft());
            }
        }

    }

    @Override
    public void visit(ConditionalExpr n, Node context) {
        if (context instanceof ConditionalExpr) {
            ConditionalExpr aux = (ConditionalExpr) context;

            if (isNegative(n.getCondition()) && aux.getElseExpr() != null) {
                try {
                    ConditionalExpr copy = aux.clone();
                    negate(copy.getCondition());
                    Expression thenExpr = copy.getThenExpr();
                    copy.setThenExpr(copy.getElseExpr());
                    copy.setElseExpr(thenExpr);

                    context.getParentNode().replaceChildNode(context, copy);

                } catch (CloneNotSupportedException e) {
                }
            }
        }
    }
}
