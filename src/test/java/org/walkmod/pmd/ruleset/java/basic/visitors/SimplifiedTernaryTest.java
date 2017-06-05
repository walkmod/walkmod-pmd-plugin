package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.junit.Assert;
import org.junit.Test;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ReturnStmt;
import org.walkmod.javalang.test.SemanticTest;

public class SimplifiedTernaryTest extends SemanticTest {

    @Test
    public void when_returns_true_or_something_condition_is_rewritten() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public boolean something(){ return false;}  public boolean bar(boolean condition) { return condition ? true : something(); } }");
        SimplifiedTernary st = new SimplifiedTernary();
        st.visit(cu, cu);
        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(1);

        BlockStmt block = md.getBody();

        ReturnStmt stmt = (ReturnStmt) block.getStmts().get(0);
        Assert.assertFalse(stmt.getExpr() instanceof ConditionalExpr);
    }

    @Test
    public void when_returns_true_or_false_is_rewritten_as_condition() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public boolean bar(boolean condition) { return condition ? true :false; } }");
        SimplifiedTernary st = new SimplifiedTernary();
        st.visit(cu, cu);
        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        BlockStmt block = md.getBody();

        ReturnStmt stmt = (ReturnStmt) block.getStmts().get(0);
        Assert.assertTrue(stmt.getExpr() instanceof NameExpr);
    }
}
