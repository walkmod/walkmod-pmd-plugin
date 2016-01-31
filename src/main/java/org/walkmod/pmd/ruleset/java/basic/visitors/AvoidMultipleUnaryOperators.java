package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr.Operator;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

public class AvoidMultipleUnaryOperators<T> extends AbstractPMDRuleVisitor<T> {

   @Override
   public void visit(UnaryExpr n, T ctx) {
      Expression expr = n.getExpr();
      Operator op = n.getOperator();
      if (expr instanceof UnaryExpr) {
         if (op.equals(Operator.not)) {
            boolean accum = false;
            boolean finish = false;
            UnaryExpr aux = null;
            while ((expr instanceof UnaryExpr) && !finish) {
               aux = (UnaryExpr) expr;
               if (aux.getOperator().equals(Operator.not)) {
                  accum = !accum;
                  expr = aux.getExpr();
               } else {
                  finish = true;
               }
            }
            if (accum) {
               n.getParentNode().replaceChildNode(n, expr);
            } else {
               if (expr instanceof BooleanLiteralExpr) {
                  BooleanLiteralExpr literal = (BooleanLiteralExpr) expr;
                  if (literal.getValue()) {
                     n.getParentNode().replaceChildNode(n, new BooleanLiteralExpr(false));
                  } else {
                     n.getParentNode().replaceChildNode(n, new BooleanLiteralExpr(true));
                  }
               } else {
                  n.setExpr(expr);
               }
            }
         } else if (op.equals(Operator.negative) || op.equals(Operator.positive)) {
            boolean accum = op.equals(Operator.positive);
            boolean finish = false;
            UnaryExpr aux = null;
            while ((expr instanceof UnaryExpr) && !finish) {
               aux = (UnaryExpr) expr;
               if (aux.getOperator().equals(Operator.negative)) {
                  accum = !accum;
                  expr = aux.getExpr();
               } else if (!aux.getOperator().equals(Operator.positive)) {
                  finish = true;
               } else {
                  expr = aux.getExpr();
               }
            }
            if (accum) {
               n.getParentNode().replaceChildNode(n, expr);
            } else {
               n.setExpr(aux);
            }
         } else if (op.equals(Operator.inverse)) {
            boolean accum = false;
            boolean finish = false;
            UnaryExpr aux = null;
            while ((expr instanceof UnaryExpr) && !finish) {
               aux = (UnaryExpr) expr;
               if (aux.getOperator().equals(Operator.inverse)) {
                  accum = !accum;
                  expr = aux.getExpr();
               } else {
                  finish = true;
               }

            }
            if (accum) {
               n.getParentNode().replaceChildNode(n, expr);
            } else {
               n.setExpr(aux);
            }
         }
      }
   }
}
