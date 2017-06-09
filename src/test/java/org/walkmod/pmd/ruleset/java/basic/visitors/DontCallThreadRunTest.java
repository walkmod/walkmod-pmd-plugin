package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.test.SemanticTest;

public class DontCallThreadRunTest extends SemanticTest {

    @Test
    public void test() throws Exception {

        CompilationUnit cu = compile("public class Foo { public void bar(Thread thread){ thread.run(); } }");
        DontCallThreadRun visitor = new DontCallThreadRun();

        visitor.visit(cu, cu);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        ExpressionStmt exprStmt = (ExpressionStmt) stmts.get(0);

        MethodCallExpr mce = (MethodCallExpr) exprStmt.getExpression();
        Assert.assertEquals("start", mce.getName());
    }
}
