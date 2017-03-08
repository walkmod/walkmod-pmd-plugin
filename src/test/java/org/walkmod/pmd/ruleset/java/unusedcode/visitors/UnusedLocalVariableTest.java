package org.walkmod.pmd.ruleset.java.unusedcode.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.test.SemanticTest;

public class UnusedLocalVariableTest extends SemanticTest {

    @Test
    public void should_remove_var_when_it_is_not_used() throws Exception {
        CompilationUnit cu = compile("public class Foo{ void bar() {int x; }} ");

        UnusedLocalVariable visitor = new UnusedLocalVariable();
        visitor.visit(cu, cu);
        MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(0);
        Assert.assertEquals(0, md.getBody().getStmts().size());

    }
    
    @Test
    public void should_not_remove_var_when_it_is_used() throws Exception {
        CompilationUnit cu = compile("public class Foo{ void bar() {int x = 0; x++; }} ");

        UnusedLocalVariable visitor = new UnusedLocalVariable();
        visitor.visit(cu, cu);
        MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(0);
        Assert.assertEquals(2, md.getBody().getStmts().size());

    }

    @Test
    public void should_not_remove_var_issue11() throws Exception{
        CompilationUnit cu = compile("import javax.swing.*; public class Foo {" +
                " public JFrame getMainFrame(){ return null;} " +
                "public void bar() {" +
                "   JFrame mainFrame = getMainFrame();\n" +
                "   mainFrame.setTitle(\"frameTitle\");\n" +
                "   mainFrame.setSize(50, 50);\n" +
                "   mainFrame.setJMenuBar(null);" +
                "}" +
                "}");



        UnusedLocalVariable visitor = new UnusedLocalVariable();
        visitor.visit(cu, cu);

        MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(1);
        Assert.assertEquals(4, md.getBody().getStmts().size());
    }
}
