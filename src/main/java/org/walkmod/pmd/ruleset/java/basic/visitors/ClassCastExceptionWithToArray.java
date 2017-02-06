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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ParseException;
import org.walkmod.javalang.ast.MethodSymbolData;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.ArrayCreationExpr;
import org.walkmod.javalang.ast.expr.CastExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.QualifiedNameExpr;
import org.walkmod.javalang.ast.type.ReferenceType;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
public class ClassCastExceptionWithToArray extends PMDRuleVisitor {

    @Override
    public void visit(MethodCallExpr n, Node ctx) {
        super.visit(n, ctx);

        MethodCallExpr aux = (MethodCallExpr) ctx;

        String name = n.getName();
        if (name.equals("toArray")) {
            List<Expression> args = n.getArgs();
            if (args == null || args.isEmpty()) {

                Node parentNode = n.getParentNode();

                if (parentNode != null) {
                    if (parentNode instanceof CastExpr) {

                        CastExpr cast = (CastExpr) parentNode;

                        Type castedType = cast.getType();
                        if (castedType instanceof ReferenceType) {
                            Type arrayType = ((ReferenceType) castedType).getType();
                            Expression scope = n.getScope();

                            if (scope instanceof NameExpr) {
                                if (!(scope instanceof QualifiedNameExpr)) {

                                    try {
                                        NameExpr scopeForSizeOp = (NameExpr) ASTManager.parse(NameExpr.class,
                                                scope.toString());
                                        MethodSymbolData msd = n.getSymbolData();
                                        if (msd != null) {
                                            Method method = msd.getMethod();
                                            if (method != null) {
                                                Class<?> clazz = method.getDeclaringClass();
                                                if (Collection.class.isAssignableFrom(clazz)) {
                                                    args = new LinkedList<Expression>();
                                                    List<Expression> dimensions = new LinkedList<Expression>();
                                                    dimensions.add(new MethodCallExpr(scopeForSizeOp, "size"));
                                                    args.add(new ArrayCreationExpr(arrayType, dimensions, 0));
                                                    aux.setArgs(args);
                                                }
                                            }
                                        }
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
