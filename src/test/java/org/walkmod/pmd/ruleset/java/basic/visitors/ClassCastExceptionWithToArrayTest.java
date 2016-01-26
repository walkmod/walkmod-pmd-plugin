package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.CastExpr;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.test.SemanticTest;


public class ClassCastExceptionWithToArrayTest extends SemanticTest{

   @Test
   public void test() throws Exception{
      CompilationUnit cu = compile(FileUtils.readFileToString(new File("src/test/resources/examples/classCastExceptionWithToArray.txt")));
      ClassCastExceptionWithToArray<?> visitor = new ClassCastExceptionWithToArray<Object>();
      visitor.visit(cu, null);
      
      MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(0);
      
      BlockStmt block = md.getBody();
      List<Statement> stmts = block.getStmts();
      Statement stmt = stmts.get(stmts.size()-1);
      //Integer[] a = (Integer [])c.toArray();
      ExpressionStmt expr = (ExpressionStmt) stmt;
      VariableDeclarationExpr assign = (VariableDeclarationExpr)expr.getExpression();
      
      CastExpr cast = (CastExpr)assign.getVars().get(0).getInit();
      MethodCallExpr call = (MethodCallExpr)cast.getExpr();
      
      Assert.assertNotNull(call.getArgs());
   }
}
