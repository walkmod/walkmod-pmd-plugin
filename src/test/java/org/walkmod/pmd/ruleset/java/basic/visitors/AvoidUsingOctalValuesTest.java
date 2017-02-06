package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.IntegerLiteralExpr;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.ast.stmt.ForeachStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.stmt.TryStmt;
import org.walkmod.javalang.visitors.CloneVisitor;

public class AvoidUsingOctalValuesTest {

   @Test
   public void test() throws Exception {
      CompilationUnit cu = ASTManager
            .parse("public class Foo{ public void bar(){int i = 012; int j = 010; int k = i * j; } }");

      AvoidUsingOctalValues visitor = new AvoidUsingOctalValues();

      visitor.visit(cu, cu);

      MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

      BlockStmt block = md.getBody();

      List<Statement> stmts = block.getStmts();

      ExpressionStmt estmt = (ExpressionStmt) stmts.get(0);

      VariableDeclarationExpr vde = (VariableDeclarationExpr) estmt.getExpression();

      IntegerLiteralExpr ile = (IntegerLiteralExpr) vde.getVars().get(0).getInit();

      Assert.assertEquals("10", ile.getValue());

      estmt = (ExpressionStmt) stmts.get(1);

      vde = (VariableDeclarationExpr) estmt.getExpression();

      ile = (IntegerLiteralExpr) vde.getVars().get(0).getInit();

      Assert.assertEquals("8", ile.getValue());
   }
   
   @Test
   public void testBug() throws Exception {
      CompilationUnit cu = ASTManager
            .parse(new File("src/test/resources/examples/bugOctal.txt"));

      AvoidUsingOctalValues visitor = new AvoidUsingOctalValues();

      CompilationUnit aux = (CompilationUnit) new CloneVisitor().visit(cu, null);
      visitor.visit(cu, aux);

      MethodDeclaration md = (MethodDeclaration) aux.getTypes().get(0).getMembers().get(4);

      BlockStmt block = md.getBody();

      List<Statement> stmts = block.getStmts();

      TryStmt trystmt = (TryStmt) stmts.get(2);
      ForeachStmt forStmt = (ForeachStmt) trystmt.getTryBlock().getStmts().get(1);
      BlockStmt bStmt = (BlockStmt) forStmt.getBody();
      IfStmt stmt = (IfStmt) bStmt.getStmts().get(2);
      
      ExpressionStmt estmt = (ExpressionStmt) ((BlockStmt)stmt.getThenStmt()).getStmts().get(0);
      MethodCallExpr vde = (MethodCallExpr) estmt.getExpression();

      BinaryExpr ile = (BinaryExpr) vde.getArgs().get(0);

      Assert.assertNotEquals("0755", ile.getRight().toString());
   }
}
