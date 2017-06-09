/*
 * Copyright (C) 2016 Raquel Pau.
 *
 * Walkmod is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Walkmod is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Walkmod. If not, see <http://www.gnu.org/licenses/>.
 */
package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
@Modification
public class SimplifiedTernary extends PMDRuleVisitor {

    @Override
    public void visit(ConditionalExpr n, Node ctx) {
        super.visit(n, ctx);
        ConditionalExpr aux = (ConditionalExpr) ctx;
        Expression thenExpr = n.getThenExpr();
        Expression elseExpr = n.getElseExpr();
        if (thenExpr != null && elseExpr != null) {
            SymbolData sdThen = thenExpr.getSymbolData();
            if (sdThen != null) {
                if (sdThen.getClazz().isAssignableFrom(boolean.class)) {
                    if (thenExpr instanceof BooleanLiteralExpr) {
                        BooleanLiteralExpr literal = (BooleanLiteralExpr) thenExpr;

                        if (literal.getValue()) {
                            //condition || foo  when the literalBoolean is true
                            Node newNode = null;
                            if (elseExpr instanceof BooleanLiteralExpr) {
                                newNode = aux.getCondition();
                            } else {
                                newNode = new BinaryExpr(aux.getCondition(), aux.getElseExpr(), BinaryExpr.Operator.or);
                            }
                            aux.getParentNode().replaceChildNode(aux, newNode);
                        } else {
                            //!condition && foo when the literalBoolean is false
                            Node newNode = null;
                            if (elseExpr instanceof BooleanLiteralExpr) {
                                newNode = aux.getCondition();
                            } else {
                                newNode = new BinaryExpr(new UnaryExpr(aux.getCondition(), UnaryExpr.Operator.not),
                                        aux.getElseExpr(), BinaryExpr.Operator.and);
                            }
                            aux.getParentNode().replaceChildNode(aux, newNode);
                        }
                    } else if (elseExpr instanceof BooleanLiteralExpr) {
                        BooleanLiteralExpr literal = (BooleanLiteralExpr) elseExpr;

                        if (literal.getValue()) {
                            //!condition || foo when the literalBoolean is true
                            aux.getParentNode().replaceChildNode(aux,
                                    new BinaryExpr(new UnaryExpr(aux.getCondition(), UnaryExpr.Operator.not),
                                            aux.getElseExpr(), BinaryExpr.Operator.or));
                        } else {
                            //condition && foo  when the literalBoolean is false
                            aux.getParentNode().replaceChildNode(aux,
                                    new BinaryExpr(aux.getCondition(), aux.getElseExpr(), BinaryExpr.Operator.and));
                        }
                    }
                }
            }
        }
    }
}
