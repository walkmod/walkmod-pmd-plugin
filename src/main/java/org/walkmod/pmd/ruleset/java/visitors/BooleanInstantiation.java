package org.walkmod.pmd.ruleset.java.visitors;

import java.lang.reflect.Method;

import org.walkmod.javalang.ast.ConstructorSymbolData;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.LiteralExpr;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;

@RequiresSemanticAnalysis
public class BooleanInstantiation<T> extends VoidVisitorAdapter<T> {

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
                  FieldAccessExpr newExpr = new FieldAccessExpr(n.getScope(), label.toUpperCase());
                  parent.replaceChildNode(n, newExpr);
               }
            }
         }
      }
   }
}
