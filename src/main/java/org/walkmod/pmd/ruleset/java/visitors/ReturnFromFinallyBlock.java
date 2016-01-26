package org.walkmod.pmd.ruleset.java.visitors;

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
import org.walkmod.pmd.visitors.AbstactPMDRuleVisitor;

public class ReturnFromFinallyBlock<T> extends AbstactPMDRuleVisitor<T> {

   public void visit(MethodDeclaration md, T ctx) {
      Type type = md.getType();
      if (!(type instanceof VoidType)) {
         FinallyBlockAnalizer visitor = new FinallyBlockAnalizer();
         md.getBody().accept(visitor, md);
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
               boolean found = false;
               while (it.hasNext() && !found) {
                  Statement stmt = it.next();
                  if (stmt instanceof ReturnStmt) {
                     returnStmtToAdd = (ReturnStmt) stmt;
                     it.remove();
                  }
               }
            }
            if(stmts.isEmpty()){
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
