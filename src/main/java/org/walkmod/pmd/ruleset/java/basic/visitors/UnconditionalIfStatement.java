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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

public class UnconditionalIfStatement<T> extends AbstractPMDRuleVisitor<T> {

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
                  } else {
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
