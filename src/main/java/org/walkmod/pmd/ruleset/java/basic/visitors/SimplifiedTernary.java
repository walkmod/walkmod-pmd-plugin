package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

@RequiresSemanticAnalysis
public class SimplifiedTernary<T> extends AbstractPMDRuleVisitor<T> {

   @Override
   public void visit(ConditionalExpr n, T ctx) {
      Expression thenExpr = n.getThenExpr();
      Expression elseExpr = n.getElseExpr();
      if (thenExpr != null && elseExpr != null) {
         SymbolData sdThen = thenExpr.getSymbolData();
         if (sdThen != null) {
            if (sdThen.getClazz().isAssignableFrom(boolean.class)) {
               if(thenExpr instanceof BooleanLiteralExpr){
                  BooleanLiteralExpr literal = (BooleanLiteralExpr) thenExpr;
                  if(literal.getValue()){
                     //condition || foo  when the literalBoolean is true
                     n.getParentNode().replaceChildNode(n, new BinaryExpr(n.getCondition(), n.getElseExpr(), BinaryExpr.Operator.or));
                  }
                  else{
                     //!condition && foo when the literalBoolean is false
                     n.getParentNode().replaceChildNode(n, new BinaryExpr(new UnaryExpr(n.getCondition(), UnaryExpr.Operator.not), n.getElseExpr(), BinaryExpr.Operator.and));
                  }
               }
               else if(elseExpr instanceof BooleanLiteralExpr){
                  BooleanLiteralExpr literal = (BooleanLiteralExpr) elseExpr;
                  
                  if(literal.getValue()){
                     //!condition || foo when the literalBoolean is true
                     n.getParentNode().replaceChildNode(n, new BinaryExpr(new UnaryExpr(n.getCondition(), UnaryExpr.Operator.not), n.getElseExpr(), BinaryExpr.Operator.or));
                  }
                  else{
                     //condition && foo  when the literalBoolean is false
                     n.getParentNode().replaceChildNode(n, new BinaryExpr(n.getCondition(), n.getElseExpr(), BinaryExpr.Operator.and));
                  }
                  
               }
            }
         }
      }
      super.visit(n, ctx);
   }
}
