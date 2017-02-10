package org.walkmod.pmd.ruleset.java.optimizations.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.pmd.ruleset.java.optimizations.visitors.MethodArgumentCouldBeFinal;

public class MethodArgumentCouldBeFinalTest extends SemanticTest {

    @Test
    public void testAssignedParamsAreFinal() throws Exception {
        CompilationUnit cu = compile("public class Foo{ int x; public void setX(int x){ this.x = x;} }");
        MethodArgumentCouldBeFinal visitor = new MethodArgumentCouldBeFinal();
        
        visitor.visit(cu, cu);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(1);
        Parameter param = md.getParameters().get(0);
        Assert.assertTrue(ModifierSet.isFinal(param.getModifiers()));
    }
}
