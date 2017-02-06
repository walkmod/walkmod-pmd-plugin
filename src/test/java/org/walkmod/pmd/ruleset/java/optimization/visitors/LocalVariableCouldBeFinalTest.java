package org.walkmod.pmd.ruleset.java.optimization.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.pmd.ruleset.java.optimization.visitors.LocalVariableCouldBeFinal;

public class LocalVariableCouldBeFinalTest extends SemanticTest {

    @Test
    public void testUnusedVarsAreFinal() throws Exception {

        CompilationUnit cu = compile("public class Foo{ public void bar(Object c){  String a = \"a\"; } }");

        LocalVariableCouldBeFinal mnc = new LocalVariableCouldBeFinal();

        mnc.visit(cu, cu);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        ExpressionStmt stmt = (ExpressionStmt) md.getBody().getStmts().get(0);
       
        VariableDeclarationExpr vde = (VariableDeclarationExpr) stmt.getExpression();

        Assert.assertTrue(ModifierSet.isFinal(vde.getModifiers()));
    }
    

    @Test
    public void testAssignedVarsAreNotFinal() throws Exception {

        CompilationUnit cu = compile("public class Foo{ public void bar(Object c){  String a = \"a\"; a=\"b\";} }");

        LocalVariableCouldBeFinal mnc = new LocalVariableCouldBeFinal();

        mnc.visit(cu, cu);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        ExpressionStmt stmt = (ExpressionStmt) md.getBody().getStmts().get(0);
       
        VariableDeclarationExpr vde = (VariableDeclarationExpr) stmt.getExpression();

        Assert.assertTrue(!ModifierSet.isFinal(vde.getModifiers()));
    }
}
