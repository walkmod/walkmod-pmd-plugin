package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.math.BigDecimal;
import java.util.List;

import org.walkmod.javalang.ast.ConstructorSymbolData;
import org.walkmod.javalang.ast.expr.DoubleLiteralExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.ast.expr.StringLiteralExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

@RequiresSemanticAnalysis
public class AvoidDecimalLiteralsInBigDecimalConstructor<T> extends AbstractPMDRuleVisitor<T> {

   @Override
   public void visit(ObjectCreationExpr expr, T ctx) {
      ConstructorSymbolData csd = expr.getSymbolData();

      if (csd != null) {
         Class<?> clazz = csd.getClazz();
         if (clazz.getName().equals(BigDecimal.class.getName())) {
            List<Expression> args = expr.getArgs();
            if (args != null && args.size() == 1) {
               Expression arg = args.get(0);
               if (arg instanceof DoubleLiteralExpr) {
                  args.clear();
                  DoubleLiteralExpr dle = (DoubleLiteralExpr) arg;
                  args.add(new StringLiteralExpr(dle.getValue()));
               }
            }
         }
      }
   }
}
