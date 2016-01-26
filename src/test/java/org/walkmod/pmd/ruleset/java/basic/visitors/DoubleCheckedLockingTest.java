package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.SynchronizedStmt;
import org.walkmod.pmd.ruleset.java.basic.visitors.DoubleCheckedLocking;


public class DoubleCheckedLockingTest {

   @Test
   public void testMissingEquals() throws Exception {
      CompilationUnit cu = ASTManager.parse(new File("src/test/resources/examples/doubleCheckedLocking.txt"));
      DoubleCheckedLocking<?> visitor = new DoubleCheckedLocking<Object>();
      visitor.visit(cu, null);
      MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(1);
      BlockStmt block = md.getBody();
      IfStmt ifStmt = (IfStmt)block.getStmts().get(0);
      BlockStmt stmt = (BlockStmt)ifStmt.getThenStmt();
      Assert.assertTrue(stmt.getStmts().get(0) instanceof SynchronizedStmt);
   }
}
