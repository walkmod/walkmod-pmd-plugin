package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.test.SemanticTest;

public class ExtendsObjectTest extends SemanticTest{

   @Test
   public void test() throws Exception{
      CompilationUnit cu = compile(FileUtils.readFileToString(new File("src/test/resources/examples/extendsObject.txt")));
      ExtendsObject<?> visitor = new ExtendsObject<Object>();
      visitor.visit(cu, null);
      ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);
      Assert.assertNull(coid.getExtends());
   }
}
