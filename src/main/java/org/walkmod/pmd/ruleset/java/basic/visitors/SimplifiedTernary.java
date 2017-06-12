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
import org.walkmod.javalang.ast.expr.AssignExpr;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.expr.EnclosedExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.NameExpr;
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
                    final Expression strippedThenExpr = stripEnclosed(thenExpr);
                    final Expression strippedElseExpr = stripEnclosed(elseExpr);
                    if (strippedThenExpr instanceof BooleanLiteralExpr) {
                        refactorLiteralThen(aux, ((BooleanLiteralExpr) strippedThenExpr).getValue(),
                                strippedElseExpr);
                    } else if (strippedElseExpr instanceof BooleanLiteralExpr) {
                        refactorLiteralElse(aux, ((BooleanLiteralExpr) strippedElseExpr).getValue());
                    }
                }
            }
        }
    }

    private static Expression stripEnclosed(Expression expr) {
        if (expr instanceof EnclosedExpr) {
            return stripEnclosed(((EnclosedExpr) expr).getInner());
        } else {
            return expr;
        }
    }

    private static void refactorLiteralThen(ConditionalExpr aux, final boolean thenValue, Expression strippedElseExpr) {
        if (thenValue) {
            //condition || foo  when the literalBoolean is true
            Node newNode;
            if (strippedElseExpr instanceof BooleanLiteralExpr) {
                boolean elseValue = ((BooleanLiteralExpr)strippedElseExpr).getValue();
                newNode = isAssignment(aux.getCondition()) && elseValue
                        ? or(aux.getCondition(), literalTrue())
                        : aux.getCondition();
            } else {
                newNode = or(aux.getCondition(), aux.getElseExpr());
            }
            aux.getParentNode().replaceChildNode(aux, newNode);
        } else {
            //!condition && foo when the literalBoolean is false
            Node newNode;
            if (strippedElseExpr instanceof BooleanLiteralExpr) {
                boolean elseValue = ((BooleanLiteralExpr)strippedElseExpr).getValue();
                newNode = elseValue
                        ? not(aux.getCondition())
                        : isAssignment(aux.getCondition())
                        ? and(aux.getCondition(), literalFalse())
                        : literalFalse();
            } else {
                newNode = and(not(aux.getCondition()), aux.getElseExpr());
            }
            aux.getParentNode().replaceChildNode(aux, newNode);
        }
    }

    private static boolean isAssignment(final Expression expr) {
        return stripEnclosed(expr) instanceof AssignExpr;
    }

    private static BooleanLiteralExpr literalFalse() {
        return new BooleanLiteralExpr(false);
    }

    private static BooleanLiteralExpr literalTrue() {
        return new BooleanLiteralExpr(true);
    }

    private static UnaryExpr not(final Expression condition) {
        return new UnaryExpr(encloseIfNeeded(condition), UnaryExpr.Operator.not);
    }

    private static BinaryExpr and(final Expression e1, Expression e2) {
        return new BinaryExpr(e1, e2, BinaryExpr.Operator.and);
    }

    private static BinaryExpr or(final Expression e1, final Expression e2) {
        return new BinaryExpr(e1, e2, BinaryExpr.Operator.or);
    }

    private static void refactorLiteralElse(ConditionalExpr aux, final boolean elseValue) {
        if (elseValue) {
            //!condition || foo when the literalBoolean is true
            aux.getParentNode().replaceChildNode(aux, or(not(aux.getCondition()), aux.getThenExpr()));
        } else {
            //condition && foo  when the literalBoolean is false
            aux.getParentNode().replaceChildNode(aux, and(aux.getCondition(), aux.getThenExpr()));
        }
    }

    private static Expression encloseIfNeeded(Expression expr) {
        return expr instanceof EnclosedExpr
                || expr instanceof NameExpr
                ? expr : new EnclosedExpr(expr);

    }
}
