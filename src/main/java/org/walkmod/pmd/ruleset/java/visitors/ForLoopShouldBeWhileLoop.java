package org.walkmod.pmd.ruleset.java.visitors;

import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ForStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.stmt.WhileStmt;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;

public class ForLoopShouldBeWhileLoop<T> extends VoidVisitorAdapter<T> {

   public void visit(ForStmt n, T ctx) {
      List<Expression> initExprs = n.getInit();
      List<Expression> updateExprs = n.getUpdate();
      boolean emptyInit = initExprs == null || initExprs.isEmpty();
      boolean emptyUpdate = updateExprs == null || updateExprs.isEmpty();
      if (emptyInit && emptyUpdate) {
         WhileStmt whileStmt = new WhileStmt(n.getCompare(), n.getBody());
         Node parent = n.getParentNode();
         if (parent instanceof BlockStmt) {
            BlockStmt block = (BlockStmt) parent;
            List<Statement> list = new LinkedList<Statement>(block.getStmts());
            int max = list.size();
            boolean found = false;
            for (int i = 0; i < max && !found; i++) {

               Statement current = list.get(i);
               if (current == n) {
                  list.remove(i);
                  list.add(i, whileStmt);

                  found = true;
               }
            }
            block.setStmts(list);

         }
      }
   }
}
