package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.test.SemanticTest;

public class BigIntegerInstantiationTest extends SemanticTest {

   @Test
   public void testBigInteger() throws Exception {
      CompilationUnit cu = compile(
            "import java.math.BigInteger; public class Foo{ public void bar() { BigInteger bi = new BigInteger(\"1\");} }");
      BigIntegerInstantiation<?> bii = new BigIntegerInstantiation<Object>();
      bii.visit(cu, null);
      
      List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
      MethodDeclaration md = (MethodDeclaration) members.get(0);

      List<Statement> stmts = md.getBody().getStmts();

      ExpressionStmt estmt = (ExpressionStmt) stmts.get(0);

      VariableDeclarationExpr mce = (VariableDeclarationExpr) estmt.getExpression();
      
      Expression expr = mce.getVars().get(0).getInit();

      Assert.assertTrue(expr instanceof FieldAccessExpr);
   }
   
   @Test
   public void testBigDecimal() throws Exception {
      CompilationUnit cu = compile(
            "import java.math.BigDecimal; public class Foo{ public void bar() { BigDecimal bi = new BigDecimal(10);} }");
      BigIntegerInstantiation<?> bii = new BigIntegerInstantiation<Object>();
      bii.visit(cu, null);
      
      List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
      MethodDeclaration md = (MethodDeclaration) members.get(0);

      List<Statement> stmts = md.getBody().getStmts();

      ExpressionStmt estmt = (ExpressionStmt) stmts.get(0);

      VariableDeclarationExpr mce = (VariableDeclarationExpr) estmt.getExpression();
      
      Expression expr = mce.getVars().get(0).getInit();

      Assert.assertTrue(expr instanceof FieldAccessExpr);
   }
}
