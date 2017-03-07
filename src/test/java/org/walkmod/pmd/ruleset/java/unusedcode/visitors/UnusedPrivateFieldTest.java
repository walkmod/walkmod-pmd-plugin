package org.walkmod.pmd.ruleset.java.unusedcode.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.test.SemanticTest;

public class UnusedPrivateFieldTest extends SemanticTest{

    @Test
    public void should_remove_field_when_it_is_not_used() throws Exception{
        CompilationUnit cu = compile("public class Foo{ private int x; } ");

        UnusedPrivateField visitor = new UnusedPrivateField();
        visitor.visit(cu, cu);
        Assert.assertEquals(0,cu.getTypes().get(0).getMembers().size());
    }
    
    @Test
    public void should_not_remove_field_when_it_is_used() throws Exception{
        CompilationUnit cu = compile("public class Foo{ private int x; public void bar(){ x = 1;}} ");

        UnusedPrivateField visitor = new UnusedPrivateField();
        visitor.visit(cu, cu);
        Assert.assertEquals(2,cu.getTypes().get(0).getMembers().size());
    }


    @Test
    public void should_not_remove_serial_version_ids() throws Exception{
        CompilationUnit cu = compile("public class Foo{ private static final long serialVersionUID = 0x1e28782b066ab988L; } ");

        UnusedPrivateField visitor = new UnusedPrivateField();
        visitor.visit(cu, cu);
        Assert.assertEquals(1,cu.getTypes().get(0).getMembers().size());
    }
}
