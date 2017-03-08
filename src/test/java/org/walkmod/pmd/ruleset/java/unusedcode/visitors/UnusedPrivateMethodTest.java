package org.walkmod.pmd.ruleset.java.unusedcode.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.test.SemanticTest;

public class UnusedPrivateMethodTest extends SemanticTest{

    @Test
    public void should_remove_method_when_it_is_not_used() throws Exception{
        CompilationUnit cu = compile("public class Foo{ private void x(){} } ");

        UnusedPrivateMethod visitor = new UnusedPrivateMethod();
        visitor.visit(cu, cu);
        Assert.assertEquals(0,cu.getTypes().get(0).getMembers().size());
    }

    @Test
    public void should_not_remove_method_when_it_is_used() throws Exception{
        CompilationUnit cu = compile("public class Foo{ public void bar(){x();} private void x(){ } } ");

        UnusedPrivateMethod visitor = new UnusedPrivateMethod();
        visitor.visit(cu, cu);
        Assert.assertEquals(2,cu.getTypes().get(0).getMembers().size());
    }

    @Test
    public void should_not_remove_method_when_it_is_writeObject() throws Exception{
        CompilationUnit cu = compile("import java.io.*; public class Foo{ private void writeObject(ObjectOutputStream s) throws IOException {} } ");

        UnusedPrivateMethod visitor = new UnusedPrivateMethod();
        visitor.visit(cu, cu);
        Assert.assertEquals(1,cu.getTypes().get(0).getMembers().size());
    }

    @Test
    public void should_not_remove_method_when_it_is_readObject() throws Exception{
        CompilationUnit cu = compile("import java.io.*; public class Foo{ private void readObject(ObjectInputStream s) throws IOException {} } ");

        UnusedPrivateMethod visitor = new UnusedPrivateMethod();
        visitor.visit(cu, cu);
        Assert.assertEquals(1,cu.getTypes().get(0).getMembers().size());
    }
}
