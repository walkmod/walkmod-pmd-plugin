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

import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ForStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.stmt.WhileStmt;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Modification
public class ForLoopShouldBeWhileLoop extends PMDRuleVisitor {

    public void visit(ForStmt n, Node ctx) {
        super.visit(n, ctx);
        n = (ForStmt) ctx;
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
