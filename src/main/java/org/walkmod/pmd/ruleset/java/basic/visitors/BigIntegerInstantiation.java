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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.ConstructorSymbolData;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.ast.expr.StringLiteralExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
@Modification
public class BigIntegerInstantiation extends PMDRuleVisitor {

    private Map<String, String> integerConstants;

    private Map<String, String> decimalConstants;

    public BigIntegerInstantiation() {
        integerConstants = new HashMap<String, String>();
        integerConstants.put("0", "ZERO");
        integerConstants.put("1", "ONE");

        decimalConstants = new HashMap<String, String>();
        decimalConstants.put("0", "ZERO");
        decimalConstants.put("1", "ONE");
        decimalConstants.put("10", "TEN");
    }

    @Override
    public void visit(ObjectCreationExpr oce, Node ctx) {

        ObjectCreationExpr auxNode = (ObjectCreationExpr) ctx;

        ConstructorSymbolData sd = oce.getSymbolData();
        if (sd != null) {

            String className = sd.getName();
            Map<String, String> mapping = null;
            if (className.equals(BigInteger.class.getName())) {
                mapping = integerConstants;
            } else if (className.equals(BigDecimal.class.getName())) {
                mapping = decimalConstants;
            }

            if (mapping != null) {

                List<Expression> args = auxNode.getArgs();
                if (args != null && args.size() == 1) {

                    Expression arg = args.get(0);
                    if (arg instanceof StringLiteralExpr) {
                        StringLiteralExpr ile = (StringLiteralExpr) arg;

                        String value = ile.getValue().replace('"', '\0');
                        try {
                            if (mapping.containsKey(value)) {
                                Node parent = oce.getParentNode();
                                if (parent != null) {
                                    NameExpr nexpr =
                                            (NameExpr) ASTManager.parse(NameExpr.class, auxNode.getType().toString());
                                    parent.replaceChildNode(auxNode, new FieldAccessExpr(nexpr, mapping.get(value)));
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}
