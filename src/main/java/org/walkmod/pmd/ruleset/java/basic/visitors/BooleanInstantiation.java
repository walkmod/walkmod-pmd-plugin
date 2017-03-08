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

import java.lang.reflect.Method;

import org.walkmod.javalang.ast.ConstructorSymbolData;
import org.walkmod.javalang.ast.MethodSymbolData;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.LiteralExpr;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
@Modification
public class BooleanInstantiation extends PMDRuleVisitor {

    @Override
    public void visit(MethodCallExpr n, Node ctx) {
        super.visit(n, ctx);
        MethodCallExpr aux = (MethodCallExpr) ctx;

        if (n.getName().equals("valueOf")) {
            MethodSymbolData msd = n.getSymbolData();
            if (msd != null) {
                Method method = msd.getMethod();
                if (method != null) {
                    if (method.getDeclaringClass().getName().equals(Boolean.class.getName())) {
                        Expression arg = aux.getArgs().get(0);
                        if (arg instanceof LiteralExpr) {
                            Node parent = aux.getParentNode();
                            String label = aux.getArgs().get(0).toString().toLowerCase();
                            if (label.equals("true") || label.equals("false")) {
                                FieldAccessExpr newExpr = new FieldAccessExpr(aux.getScope(), label.toUpperCase());
                                parent.replaceChildNode(aux, newExpr);
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public void visit(ObjectCreationExpr n, Node ctx) {

        super.visit(n, ctx);
        ObjectCreationExpr aux = (ObjectCreationExpr) ctx;

        ConstructorSymbolData sd = n.getSymbolData();
        if (sd != null) {
            if (sd.getName().equals(Boolean.class.getName())) {
                Expression arg = aux.getArgs().get(0);
                if (arg instanceof LiteralExpr) {
                    Node parent = aux.getParentNode();
                    String label = aux.getArgs().get(0).toString().toLowerCase();
                    if (label.equals("\"true\"") || label.equals("\"false\"")) {
                        FieldAccessExpr newExpr = new FieldAccessExpr(new NameExpr("Boolean"),
                                label.toUpperCase().replaceAll("\"", ""));
                        parent.replaceChildNode(aux, newExpr);
                    }
                }
            }
        }

    }
}
