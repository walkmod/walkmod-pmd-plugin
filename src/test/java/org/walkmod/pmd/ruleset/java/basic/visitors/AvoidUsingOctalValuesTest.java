package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.IntegerLiteralExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.ast.stmt.Statement;

public class AvoidUsingOctalValuesTest {

   @Test
   public void test() throws Exception {
      CompilationUnit cu = ASTManager
            .parse("public class Foo{ public void bar(){int i = 012; int j = 010; int k = i * j; } }");

      AvoidUsingOctalValues<?> visitor = new AvoidUsingOctalValues<Object>();

      visitor.visit(cu, null);

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
}
