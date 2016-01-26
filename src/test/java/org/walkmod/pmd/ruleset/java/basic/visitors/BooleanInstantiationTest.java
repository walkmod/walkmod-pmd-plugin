package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.pmd.ruleset.java.basic.visitors.BooleanInstantiation;

public class BooleanInstantiationTest extends SemanticTest {

   @Test
   public void testValueOf() throws Exception {
      CompilationUnit cu = compile(
            FileUtils.readFileToString(new File("src/test/resources/examples/booleanInstantiation.txt")));
      BooleanInstantiation<?> visitor = new BooleanInstantiation<Object>();
      visitor.visit(cu, null);
      MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
      BlockStmt block = md.getBody();
      ExpressionStmt stmt = (ExpressionStmt) block.getStmts().get(0);
      VariableDeclarationExpr assign = (VariableDeclarationExpr) stmt.getExpression();

      Assert.assertTrue(assign.getVars().get(0).getInit() instanceof FieldAccessExpr);
     
   }
   
   @Test
   public void testConstructor() throws Exception {
      CompilationUnit cu = compile(
            FileUtils.readFileToString(new File("src/test/resources/examples/booleanInstantiation2.txt")));
      BooleanInstantiation<?> visitor = new BooleanInstantiation<Object>();
      visitor.visit(cu, null);
      MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
      BlockStmt block = md.getBody();
      ExpressionStmt stmt = (ExpressionStmt) block.getStmts().get(0);
      VariableDeclarationExpr assign = (VariableDeclarationExpr) stmt.getExpression();

      Assert.assertTrue(assign.getVars().get(0).getInit() instanceof FieldAccessExpr);
     
   }
}
