package org.walkmod.pmd.ruleset.java.typeresolution.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.pmd.ruleset.java.typeresolution.visitors.UnusedImports;

public class UnusedImportsTest extends SemanticTest {

    @Test
    public void should_remove_import_when_it_is_not_used() throws Exception {
        CompilationUnit cu = compile("import java.util.List; public class Foo{  } ");

        UnusedImports visitor = new UnusedImports();
        visitor.visit(cu, cu);

        Assert.assertEquals(0, cu.getImports().size());

    }
    
    @Test
    public void should_respect_imports_when_these_are_used() throws Exception{
        CompilationUnit cu = compile("import java.util.List; public class Foo{ List members; } ");
        UnusedImports visitor = new UnusedImports();
        visitor.visit(cu, cu);

        Assert.assertEquals(1, cu.getImports().size());
    }
}
