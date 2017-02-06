package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.pmd.ruleset.java.basic.visitors.ReturnFromFinallyBlock;


public class ReturnFromFinallyBlockTest {

   @Test
   public void testExample() throws Exception{
      CompilationUnit cu = ASTManager.parse(new File("src/test/resources/examples/returnFromFinallyBlock.txt"));
      ReturnFromFinallyBlock visitor = new ReturnFromFinallyBlock();
      visitor.visit(cu, null);
      MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(0);
      BlockStmt block = md.getBody();
      Assert.assertEquals(2, block.getStmts().size());
   }
}
