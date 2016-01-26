package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.EnclosedExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

public class CollapsibleIfStatements<A> extends AbstractPMDRuleVisitor<A> {

   @Override
   public void visit(IfStmt n, A ctx) {
      Node parent = n.getParentNode();

      if (parent != null) {
         if (parent instanceof BlockStmt) {
            parent = parent.getParentNode();
         }
         if (parent instanceof IfStmt) {
            IfStmt parentIf = (IfStmt) parent;

            Statement elseStmt = parentIf.getElseStmt();
            if (elseStmt == null) {

               Statement thisElseStmt = n.getElseStmt();
               if (thisElseStmt == null) {

                  Expression rightExpression = parentIf.getCondition();
                  if (rightExpression instanceof BinaryExpr) {
                     rightExpression = new EnclosedExpr(parentIf.getCondition());
                  }

                  Expression leftExpression = n.getCondition();
                  if (leftExpression instanceof BinaryExpr) {
                     leftExpression = new EnclosedExpr(n.getCondition());
                  }

                  BinaryExpr condition = new BinaryExpr(rightExpression, leftExpression, BinaryExpr.Operator.and);
                 

                  if (parentIf.getThenStmt() == n) {
                     parentIf.setThenStmt(n.getThenStmt());
                     parentIf.setCondition(condition);
                  } else {
                     Statement stmt = parentIf.getThenStmt();
                     if (stmt instanceof BlockStmt) {
                        BlockStmt block = (BlockStmt) stmt;
                        List<Statement> stmts = block.getStmts();
                        if (stmts.size() == 1) {
                           parentIf.setThenStmt(n.getThenStmt());
                           parentIf.setCondition(condition);
                        }
                     }
                  }

               }
            }
         }
      }
      super.visit(n, ctx);
   }
}
