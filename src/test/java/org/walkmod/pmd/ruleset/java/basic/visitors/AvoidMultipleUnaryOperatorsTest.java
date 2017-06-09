package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.junit.Assert;
import org.junit.Test;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;

public class AvoidMultipleUnaryOperatorsTest {

    @Test
    public void test() throws Exception {
        CompilationUnit cu = ASTManager.parse("public class Foo{ public void bar() { boolean x = !!!!! true; } }");
        AvoidMultipleUnaryOperators visitor = new AvoidMultipleUnaryOperators();

        visitor.visit(cu, cu);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        ExpressionStmt stmt = (ExpressionStmt) md.getBody().getStmts().get(0);
        VariableDeclarationExpr vde = (VariableDeclarationExpr) stmt.getExpression();
        Expression init = vde.getVars().get(0).getInit();
        Assert.assertTrue(init instanceof BooleanLiteralExpr);

        BooleanLiteralExpr aux = (BooleanLiteralExpr) init;
        Assert.assertFalse(aux.getValue());
    }
}
