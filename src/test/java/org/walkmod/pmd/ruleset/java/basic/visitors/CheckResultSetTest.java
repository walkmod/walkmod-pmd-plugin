package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.test.SemanticTest;


public class CheckResultSetTest extends SemanticTest{

   @Test
   public void testParams() throws Exception{
      CompilationUnit cu = compile("import java.sql.ResultSet; public class Foo{ public void bar(ResultSet rs) throws Exception{ rs.next();  String firstName = rs.getString(1); } }");
      CheckResultSet<?> visitor = new CheckResultSet<Object>();
      visitor.visit(cu, null);
      
      List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
      MethodDeclaration md = (MethodDeclaration)members.get(0);
      
      BlockStmt block = md.getBody();
      Assert.assertEquals(1, block.getStmts().size());
      
   }
   
   @Test
   public void testVars() throws Exception{
      CompilationUnit cu = compile("import java.sql.ResultSet; public class Foo{ ResultSet aux; public void bar() throws Exception{ ResultSet rs = aux; rs.next();  String firstName = rs.getString(1); } }");
      CheckResultSet<?> visitor = new CheckResultSet<Object>();
      visitor.visit(cu, null);
      
      List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
      MethodDeclaration md = (MethodDeclaration)members.get(1);
      
      BlockStmt block = md.getBody();
      Assert.assertEquals(2, block.getStmts().size());
      
   }
}
