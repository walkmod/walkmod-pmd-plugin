package org.walkmod.pmd.ruleset.java.visitors;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.test.SemanticTest;

public class ForLoopShouldBeWhileLoopTest extends SemanticTest {
   @Test
   public void testExample() throws Exception {
      String code = FileUtils.readFileToString(new File("src/test/resources/examples/forloopshouldbewhileloop.txt"));
      CompilationUnit cu = compile(code);
      ForLoopShouldBeWhileLoop<?> visitor = new ForLoopShouldBeWhileLoop<Object>();
      visitor.visit(cu, null);
      System.out.println(cu.toString());
   }

}
