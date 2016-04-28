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
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.LiteralExpr;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

@RequiresSemanticAnalysis
public class BooleanInstantiation<T> extends AbstractPMDRuleVisitor<T> {

   @Override
   public void visit(MethodCallExpr n, T ctx) {
      if (n.getName().equals("valueOf")) {
         Method method = n.getSymbolData().getMethod();
         if (method != null) {
            if (method.getDeclaringClass().getName().equals(Boolean.class.getName())) {
               Expression arg = n.getArgs().get(0);
               if (arg instanceof LiteralExpr) {
                  Node parent = n.getParentNode();
                  String label = n.getArgs().get(0).toString().toLowerCase();
                  if (label.equals("true") || label.equals("false")) {
                     FieldAccessExpr newExpr = new FieldAccessExpr(n.getScope(), label.toUpperCase());
                     parent.replaceChildNode(n, newExpr);
                  }
               }
            }
         }
      }
      super.visit(n, ctx);
   }

   @Override
   public void visit(ObjectCreationExpr n, T ctx) {
      ConstructorSymbolData sd = n.getSymbolData();
      if (sd != null) {
         if (sd.getClazz().getName().equals(Boolean.class.getName())) {
            Expression arg = n.getArgs().get(0);
            if (arg instanceof LiteralExpr) {
               Node parent = n.getParentNode();
               String label = n.getArgs().get(0).toString().toLowerCase();
               if (label.equals("\"true\"") || label.equals("\"false\"")) {
                  FieldAccessExpr newExpr = new FieldAccessExpr(new NameExpr("Boolean"),
                        label.toUpperCase().replaceAll("\"", ""));
                  parent.replaceChildNode(n, newExpr);
               }
            }
         }
      }
      super.visit(n, ctx);
   }
}
