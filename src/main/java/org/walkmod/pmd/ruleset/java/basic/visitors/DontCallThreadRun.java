package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.lang.reflect.Method;
import java.util.List;

import org.walkmod.javalang.ast.MethodSymbolData;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

@RequiresSemanticAnalysis
public class DontCallThreadRun<T> extends AbstractPMDRuleVisitor<T> {

   @Override
   public void visit(MethodCallExpr n, T ctx) {
      if (n.getName().equals("run")) {

         List<Expression> args = n.getArgs();
         if (args == null || args.isEmpty()) {
            MethodSymbolData msd = n.getSymbolData();
            if (msd != null) {
               Method m = msd.getMethod();
               if (Thread.class.isAssignableFrom(m.getDeclaringClass())) {
                  n.setName("start");
               }
            }
         }
      }
      super.visit(n, ctx);
   }

}
