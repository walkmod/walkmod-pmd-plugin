package org.walkmod.pmd.ruleset.java.design.visitors;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.actions.Action;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.ReturnStmt;
import org.walkmod.javalang.visitors.CloneVisitor;
import org.walkmod.javalang.walkers.ChangeLogVisitor;
import org.walkmod.walkers.VisitorContext;

public class ConfusingTernaryTest {

    @Test
    public void testConditionalExpr() throws Exception {
        CompilationUnit cu = ASTManager.parse(
                "public class Foo { public boolean bar(boolean secMode) { return secMode != true ? false : true; } }");

        ConfusingTernary visitor = new ConfusingTernary();

        CompilationUnit auxCu = (CompilationUnit) new CloneVisitor().visit(cu, null);

        visitor.visit(cu, auxCu);

        MethodDeclaration md = (MethodDeclaration) auxCu.getTypes().get(0).getMembers().get(0);

        ReturnStmt stmt = (ReturnStmt) md.getBody().getStmts().get(0);

        ConditionalExpr cexpr = (ConditionalExpr) stmt.getExpr();

        BinaryExpr be = (BinaryExpr) cexpr.getCondition();

        Assert.assertEquals(BinaryExpr.Operator.equals, be.getOperator());

        BooleanLiteralExpr litExpr = (BooleanLiteralExpr) cexpr.getThenExpr();

        Assert.assertEquals(true, litExpr.getValue());

        litExpr = (BooleanLiteralExpr) cexpr.getElseExpr();

        Assert.assertEquals(false, litExpr.getValue());
    }

    @Test
    public void testCompositeBooleanExpr() throws Exception {
        CompilationUnit cu = ASTManager.parse(
                "public class Foo { public boolean bar(List secMode) { if (value != null && !value.isEmpty()) { return true; }else{ return false; } } }");

        ConfusingTernary visitor = new ConfusingTernary();

        CompilationUnit auxCu = (CompilationUnit) new CloneVisitor().visit(cu, null);

        visitor.visit(cu, auxCu);

        MethodDeclaration md = (MethodDeclaration) auxCu.getTypes().get(0).getMembers().get(0);

        IfStmt stmt = (IfStmt) md.getBody().getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        Assert.assertEquals(BinaryExpr.Operator.or, be.getOperator());

        BinaryExpr left = (BinaryExpr) be.getLeft();

        Assert.assertEquals(BinaryExpr.Operator.equals, left.getOperator());

        Expression right = (Expression) be.getRight();

        Assert.assertTrue(right instanceof MethodCallExpr);

        ReturnStmt rstmt = (ReturnStmt) ((BlockStmt) stmt.getThenStmt()).getStmts().get(0);

        BooleanLiteralExpr litExpr = (BooleanLiteralExpr) rstmt.getExpr();

        Assert.assertEquals(false, litExpr.getValue());

        rstmt = (ReturnStmt) ((BlockStmt) stmt.getElseStmt()).getStmts().get(0);

        litExpr = (BooleanLiteralExpr) rstmt.getExpr();

        Assert.assertEquals(true, litExpr.getValue());
    }

    @Test
    public void testEqualsCondition() throws Exception {
        CompilationUnit cu = ASTManager.parse(
                "public class Foo { public boolean bar(List secMode) { if (value != null) { return true; }else{ return false; } } }");

        ConfusingTernary visitor = new ConfusingTernary();

        CompilationUnit auxCu = (CompilationUnit) new CloneVisitor().visit(cu, null);

        visitor.visit(cu, auxCu);

        MethodDeclaration md = (MethodDeclaration) auxCu.getTypes().get(0).getMembers().get(0);

        IfStmt stmt = (IfStmt) md.getBody().getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        Assert.assertEquals(BinaryExpr.Operator.equals, be.getOperator());

        ReturnStmt rstmt = (ReturnStmt) ((BlockStmt) stmt.getThenStmt()).getStmts().get(0);

        BooleanLiteralExpr litExpr = (BooleanLiteralExpr) rstmt.getExpr();

        Assert.assertEquals(false, litExpr.getValue());

        rstmt = (ReturnStmt) ((BlockStmt) stmt.getElseStmt()).getStmts().get(0);

        litExpr = (BooleanLiteralExpr) rstmt.getExpr();

        Assert.assertEquals(true, litExpr.getValue());
    }

    @Test
    public void testBugWithZeroPosition() throws Exception {
        CompilationUnit cu = ASTManager.parse(new File("src/test/resources/examples/bugOnTernary.txt"));
        ConfusingTernary visitor = new ConfusingTernary();

        CompilationUnit auxCu = (CompilationUnit) new CloneVisitor().visit(cu, null);

        visitor.visit(cu, auxCu);

        ChangeLogVisitor cmp = new ChangeLogVisitor();

        cmp.setGenerateActions(true);
        VisitorContext ctx = new VisitorContext();
        ctx.put(ChangeLogVisitor.NODE_TO_COMPARE_KEY, auxCu);

        cmp.visit(cu, ctx);
        List<Action> actions = cmp.getActionsToApply();

        Assert.assertNotNull(actions);

        Assert.assertNotNull(actions.get(0));

        Assert.assertNotEquals(0, actions.get(0).getBeginLine());
    }
}
