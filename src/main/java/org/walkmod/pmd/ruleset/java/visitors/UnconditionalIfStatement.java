package org.walkmod.pmd.ruleset.java.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;

public class UnconditionalIfStatement<T> extends VoidVisitorAdapter<T> {

   @Override
   public void visit(IfStmt n, T ctx) {
      Expression condition = n.getCondition();
      if (condition instanceof BooleanLiteralExpr) {
         BooleanLiteralExpr literal = (BooleanLiteralExpr) condition;
         if (literal.getValue() == true) {
            Node parent = n.getParentNode();
            if (parent instanceof BlockStmt) {
               BlockStmt block = (BlockStmt) parent;
               List<Statement> stmts = block.getStmts();
               List<Statement> newStmts = new LinkedList<Statement>(stmts);
               Iterator<Statement> it = newStmts.iterator();
               int i = 0;
               int pos = -1;
               while (it.hasNext() && pos == -1) {
                  Statement next = it.next();
                  if (next == n) {
                     it.remove();
                     pos = i;
                  }
                  i++;
               }
               if (pos != -1) {
                  Statement stmt = n.getThenStmt();
                  if (!(stmt instanceof BlockStmt)) {
                     newStmts.add(pos, n.getThenStmt());
                  }
                  else{
                     BlockStmt blockThen = (BlockStmt) stmt;
                     List<Statement> stmtsList = blockThen.getStmts();
                     newStmts.addAll(pos, stmtsList);
                  }
               }
               block.setStmts(newStmts);
            }
         }
      }
      super.visit(n, ctx);
   }

}
