package org.walkmod.pmd.ruleset.java.comments.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.JavadocComment;


public class CommentsSizeTest {

   @Test
   public void test() throws Exception {
      CompilationUnit cu = ASTManager.parse("" + "/**\n" + "*\n" + "*  too many lines!\n" + "*\n" + "*\n" + "*\n"
            + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*/\n public class Foo{}");

      CommentsSize<Object> visitor = new CommentsSize<Object>();
      cu.accept(visitor, null);
      
      JavadocComment comment = cu.getTypes().get(0).getJavaDoc();
      
      String content = comment.getContent();
      
      Assert.assertEquals("*  too many lines!\n", content);
   }
}
