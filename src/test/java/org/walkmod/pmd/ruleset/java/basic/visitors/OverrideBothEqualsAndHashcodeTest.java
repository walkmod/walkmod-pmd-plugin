package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.walkers.ChangeLogVisitor;
import org.walkmod.walkers.VisitorContext;

public class OverrideBothEqualsAndHashcodeTest {

    @Test
    public void testMissingEquals() throws Exception {
        CompilationUnit cu =
                ASTManager.parse(new File("src/test/resources/examples/overrideBothEqualsAndHashcode.txt"));
        List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(1, members.size());
        OverrideBothEqualsAndHashcode visitor = new OverrideBothEqualsAndHashcode();
        visitor.visit(cu, cu);
        members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(2, members.size());
        Assert.assertEquals("equals", ((MethodDeclaration) members.get(1)).getName());
    }

    @Test
    public void testMissingHashCode() throws Exception {
        CompilationUnit cu =
                ASTManager.parse(new File("src/test/resources/examples/overrideBothEqualsAndHashcode2.txt"));
        List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(1, members.size());
        OverrideBothEqualsAndHashcode visitor = new OverrideBothEqualsAndHashcode();
        visitor.visit(cu, cu);
        members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(2, members.size());
        Assert.assertEquals("hashCode", ((MethodDeclaration) members.get(1)).getName());
    }

    @Test
    public void testBothAppear() throws Exception {
        CompilationUnit cu =
                ASTManager.parse(new File("src/test/resources/examples/overrideBothEqualsAndHashcode3.txt"));
        List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(2, members.size());
        OverrideBothEqualsAndHashcode visitor = new OverrideBothEqualsAndHashcode();
        visitor.visit(cu, cu);
        members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(2, members.size());
    }

    @Test
    public void testNeitherAppear() throws Exception {
        CompilationUnit cu =
                ASTManager.parse(new File("src/test/resources/examples/overrideBothEqualsAndHashcode4.txt"));
        List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(2, members.size());
        OverrideBothEqualsAndHashcode visitor = new OverrideBothEqualsAndHashcode();
        visitor.visit(cu, cu);
        members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(2, members.size());
    }

    @Test
    public void testBugHashCode() throws Exception {
        CompilationUnit cu = ASTManager.parse(new File("src/test/resources/examples/bugHashCode.txt"));
        CompilationUnit copy = ASTManager.parse(new File("src/test/resources/examples/bugHashCode.txt"));

        OverrideBothEqualsAndHashcode visitor = new OverrideBothEqualsAndHashcode();
        visitor.visit(cu, copy);
        ChangeLogVisitor clv = new ChangeLogVisitor();
        clv.setGenerateActions(true);
        VisitorContext ctx = new VisitorContext();
        ctx.put(ChangeLogVisitor.NODE_TO_COMPARE_KEY, cu);
        clv.visit((CompilationUnit) copy, ctx);

        Assert.assertEquals(2, clv.getActionsToApply().size());
    }
}
