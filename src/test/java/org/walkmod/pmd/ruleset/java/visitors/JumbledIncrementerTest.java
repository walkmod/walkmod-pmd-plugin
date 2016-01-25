package org.walkmod.pmd.ruleset.java.visitors;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ForStmt;
import org.walkmod.javalang.test.SemanticTest;


public class JumbledIncrementerTest extends SemanticTest {

   @Test
   public void testExample() throws Exception {
      String code = FileUtils.readFileToString(new File("src/test/resources/examples/jumbledincrementer.txt"));
      CompilationUnit cu = compile(code);
      JumbledIncrementer<?> visitor = new JumbledIncrementer<Object>();
      visitor.visit(cu, null);
      MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
      ForStmt forStmt = (ForStmt) md.getBody().getStmts().get(0);
      ForStmt innerForStmt = (ForStmt) ((BlockStmt)forStmt.getBody()).getStmts().get(0);
      UnaryExpr updateExpr = (UnaryExpr) innerForStmt.getUpdate().get(0);
      NameExpr nExpr = (NameExpr) updateExpr.getExpr();
      Assert.assertEquals("k", nExpr.getName());
   }
}
