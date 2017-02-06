/* 
  Copyright (C) 2016 Raquel Pau.
 
  Walkmod is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
 
  Walkmod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public License
  along with Walkmod.  If not, see <http://www.gnu.org/licenses/>.*/
package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr.Operator;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

public class AvoidMultipleUnaryOperators extends PMDRuleVisitor {

    @Override
    public void visit(UnaryExpr n, Node ctx) {

        UnaryExpr auxNode = (UnaryExpr) ctx;
        Expression expr = auxNode.getExpr();
        Operator op = auxNode.getOperator();
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
                    auxNode.getParentNode().replaceChildNode(auxNode, expr);
                } else {
                    if (expr instanceof BooleanLiteralExpr) {
                        BooleanLiteralExpr literal = (BooleanLiteralExpr) expr;
                        if (literal.getValue()) {
                            auxNode.getParentNode().replaceChildNode(auxNode, new BooleanLiteralExpr(false));
                        } else {
                            auxNode.getParentNode().replaceChildNode(auxNode, new BooleanLiteralExpr(true));
                        }
                    } else {
                        auxNode.setExpr(expr);
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
                    auxNode.getParentNode().replaceChildNode(auxNode, expr);
                } else {
                    auxNode.setExpr(aux);
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
                    auxNode.getParentNode().replaceChildNode(auxNode, expr);
                } else {
                    auxNode.setExpr(aux);
                }
            }
        }
    }
}
