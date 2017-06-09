package org.walkmod.pmd.ruleset.java.design.visitors;

import org.junit.Assert;
import org.junit.Test;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.FieldDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.test.SemanticTest;

public class ImmutableFieldTest extends SemanticTest {

    @Test
    public void should_add_final_when_it_is_not_modified_from_method() throws Exception {
        CompilationUnit cu =
                compile("public class Foo{ private String name =\"bar\"; public String getName(){ return name; } }");

        ImmutableField visitor = new ImmutableField();
        visitor.visit(cu, cu);

        FieldDeclaration fd = (FieldDeclaration) cu.getTypes().get(0).getMembers().get(0);
        Assert.assertTrue(ModifierSet.isFinal(fd.getModifiers()));
    }

    @Test
    public void should_not_add_final_when_it_is_modified_from_method_with_this() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ private String name =\"bar\"; public void setName(String x){ this.name = x; } }");

        ImmutableField visitor = new ImmutableField();
        visitor.visit(cu, cu);

        FieldDeclaration fd = (FieldDeclaration) cu.getTypes().get(0).getMembers().get(0);
        Assert.assertFalse(ModifierSet.isFinal(fd.getModifiers()));
    }

    @Test
    public void should_not_add_final_when_it_is_modified_from_method_without_this() throws Exception {
        CompilationUnit cu =
                compile("public class Foo{ private String name =\"bar\"; public void setName(String x){ name = x; } }");

        ImmutableField visitor = new ImmutableField();
        visitor.visit(cu, cu);

        FieldDeclaration fd = (FieldDeclaration) cu.getTypes().get(0).getMembers().get(0);
        Assert.assertFalse(ModifierSet.isFinal(fd.getModifiers()));
    }
}
