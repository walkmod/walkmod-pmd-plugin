package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;

public class UnconditionalIfStatementTest {

    @Test
    public void testExample() throws Exception {
        CompilationUnit cu = ASTManager.parse(new File("src/test/resources/examples/unconditionalIfStatement.txt"));
        UnconditionalIfStatement visitor = new UnconditionalIfStatement();
        visitor.visit(cu, null);
        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        BlockStmt block = md.getBody();
        Assert.assertEquals(2, block.getStmts().size());
        Assert.assertFalse(block.getStmts().get(0) instanceof IfStmt);
    }

    @Test
    public void testIssue3() throws Exception {
        CompilationUnit cu =
                ASTManager.parse(new File("src/test/resources/examples/unconditionalIfStatement_issue3.txt"));
        UnconditionalIfStatement visitor = new UnconditionalIfStatement();
        visitor.visit(cu, null);
        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        BlockStmt block = md.getBody();
        Assert.assertEquals(1, block.getStmts().size());
        Assert.assertTrue(block.getStmts().get(0).isNewNode());
        Assert.assertFalse(block.getStmts().get(0) instanceof IfStmt);
    }
}
