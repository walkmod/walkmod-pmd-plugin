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
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ReturnStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.stmt.TryStmt;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.javalang.ast.type.VoidType;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Modification
public class ReturnFromFinallyBlock extends PMDRuleVisitor {

    public void visit(MethodDeclaration md, Node ctx) {
        md = (MethodDeclaration) ctx;
        Type type = md.getType();
        if (!(type instanceof VoidType)) {
            FinallyBlockAnalizer visitor = new FinallyBlockAnalizer();
            BlockStmt body = md.getBody();
            if (body != null) {
                body.accept(visitor, md);
            }
        }
        super.visit(md, ctx);
    }

    public class FinallyBlockAnalizer extends VoidVisitorAdapter<MethodDeclaration> {

        public void visit(TryStmt n, MethodDeclaration ctx) {
            BlockStmt finallyBlock = n.getFinallyBlock();
            Node parent = n.getParentNode();
            ReturnStmt returnStmtToAdd = null;
            if (finallyBlock != null && parent != null && parent instanceof BlockStmt) {
                List<Statement> stmts = finallyBlock.getStmts();
                if (stmts != null) {
                    Iterator<Statement> it = stmts.iterator();
                  
                    while (it.hasNext() && returnStmtToAdd == null) {
                        Statement stmt = it.next();
                        if (stmt instanceof ReturnStmt) {
                            returnStmtToAdd = (ReturnStmt) stmt;
                            it.remove();
                           
                        }
                    }
                }
                if (returnStmtToAdd != null) {
                    if (stmts.isEmpty()) {
                        n.setFinallyBlock(null);
                    }
                    BlockStmt aux = (BlockStmt) parent;
                    List<Statement> newStmts = new LinkedList<Statement>(aux.getStmts());
                    Iterator<Statement> it = newStmts.iterator();
                    int pos = 0;
                    int i = 0;
                    while (it.hasNext()) {
                        Statement next = it.next();
                        if (next == n) {
                            pos = i + 1;
                        }
                        i++;
                    }
                    newStmts.add(pos, new ReturnStmt(returnStmtToAdd.getExpr()));
                    aux.setStmts(newStmts);
                }
            }

        }
    }
}
