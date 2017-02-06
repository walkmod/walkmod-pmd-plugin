package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ReturnStmt;
import org.walkmod.javalang.test.SemanticTest;


public class SimplifiedTernaryTest extends SemanticTest{

   @Test
   public void test() throws Exception{
      CompilationUnit cu = compile("public class Foo{ public boolean something(){ return false;}  public boolean bar(boolean condition) { return condition ? true : something(); } }");
      SimplifiedTernary st = new SimplifiedTernary();
      st.visit(cu, cu);
      MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(1);
      
      BlockStmt block = md.getBody();
      
      ReturnStmt stmt = (ReturnStmt)block.getStmts().get(0);
      Assert.assertFalse(stmt.getExpr() instanceof ConditionalExpr);
      
   }
}
