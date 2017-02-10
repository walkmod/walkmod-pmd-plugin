package org.walkmod.pmd.ruleset.java.controversial.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.test.SemanticTest;

public class AtLeastOneConstructorTest extends SemanticTest {

    @Test
    public void should_add_empty_constructor_if_it_does_not_exists() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ private String name; public String getName(){ return name; } }");

        AtLeastOneConstructor visitor = new AtLeastOneConstructor();
        visitor.visit(cu, cu);

        Assert.assertEquals(3, cu.getTypes().get(0).getMembers().size());
    }
    
    @Test
    public void should_not_add_contructor_if_it_already_exists() throws Exception{
        CompilationUnit cu = compile(
                "public class Foo{ private String name; public Foo(){} public String getName(){ return name; } }");

        AtLeastOneConstructor visitor = new AtLeastOneConstructor();
        visitor.visit(cu, cu);

        Assert.assertEquals(3, cu.getTypes().get(0).getMembers().size());
    }
}
