package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.ast.expr.StringLiteralExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.test.SemanticTest;

public class AvoidDecimalLiteralsInBigDecimalConstructorTest extends SemanticTest {

   @Test
   public void test() throws Exception {
      CompilationUnit cu = compile(FileUtils
            .readFileToString(new File("src/test/resources/examples/avoidDecimalLiteralsInBigDecimalConstructor.txt")));

      AvoidDecimalLiteralsInBigDecimalConstructor<?> visitor = new AvoidDecimalLiteralsInBigDecimalConstructor<Object>();

      visitor.visit(cu, null);

      MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

      List<Statement> stmts = md.getBody().getStmts();
      Statement stmt = stmts.get(stmts.size() - 1);
     
      ExpressionStmt expr = (ExpressionStmt) stmt;
      VariableDeclarationExpr assign = (VariableDeclarationExpr) expr.getExpression();

      ObjectCreationExpr call = (ObjectCreationExpr) assign.getVars().get(0).getInit();
      
      Assert.assertTrue(call.getArgs().get(0) instanceof StringLiteralExpr);
      
   }
}
