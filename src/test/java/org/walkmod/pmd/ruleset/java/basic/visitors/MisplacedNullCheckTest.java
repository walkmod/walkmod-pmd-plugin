package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.junit.Assert;
import org.junit.Test;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.test.SemanticTest;

public class MisplacedNullCheckTest extends SemanticTest {

    @Test
    public void testAnd() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ public void bar(Object c){ if(c.toString().equals(\"hello\") && c!= null) {}} }");

        MisplacedNullCheck<?> mnc = new MisplacedNullCheck<Object>();

        mnc.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        BlockStmt block = md.getBody();

        IfStmt stmt = (IfStmt) block.getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        BinaryExpr lbe = (BinaryExpr) be.getLeft();

        Assert.assertTrue(lbe.getOperator().equals(BinaryExpr.Operator.notEquals));
    }

    @Test
    public void testReversedNullChecking() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ public void bar(Object c){ if(c.toString().equals(\"hello\") && null != c) {}} }");

        MisplacedNullCheck<?> mnc = new MisplacedNullCheck<Object>();

        mnc.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        BlockStmt block = md.getBody();

        IfStmt stmt = (IfStmt) block.getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        BinaryExpr lbe = (BinaryExpr) be.getLeft();

        Assert.assertTrue(lbe.getOperator().equals(BinaryExpr.Operator.notEquals));
    }

    @Test
    public void testAndWithFieldAccess() throws Exception {

        CompilationUnit cu =
                compile("public class Foo{ public void bar(){ if(Bar.name.equals(\"hello\") && Bar.name != null) {}} }",
                        "public class Bar{ public static Object name; }");

        MisplacedNullCheck<?> mnc = new MisplacedNullCheck<Object>();

        mnc.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        BlockStmt block = md.getBody();

        IfStmt stmt = (IfStmt) block.getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        BinaryExpr lbe = (BinaryExpr) be.getLeft();

        Assert.assertTrue(lbe.getOperator().equals(BinaryExpr.Operator.notEquals));
    }

    @Test
    public void testAndWithFields() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ private String name; public void bar(){ if(this.name.equals(\"hello\") && name != null) {}} }");

        MisplacedNullCheck<?> mnc = new MisplacedNullCheck<Object>();

        mnc.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(1);

        BlockStmt block = md.getBody();

        IfStmt stmt = (IfStmt) block.getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        BinaryExpr lbe = (BinaryExpr) be.getLeft();

        Assert.assertTrue(lbe.getOperator().equals(BinaryExpr.Operator.notEquals));
    }

    @Test
    public void testAndWithFieldsOfDifferentObjects() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ private String name; public void bar(Foo aux){ if(this.name.equals(\"hello\") && aux.name != null) {}} }");

        MisplacedNullCheck<?> mnc = new MisplacedNullCheck<Object>();

        mnc.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(1);

        BlockStmt block = md.getBody();

        IfStmt stmt = (IfStmt) block.getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        Assert.assertTrue(be.getLeft() instanceof MethodCallExpr);
    }

    @Test
    public void testOr() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ public void bar(Object c){ if(c.toString().equals(\"hello\") || c== null) {}} }");

        MisplacedNullCheck<?> mnc = new MisplacedNullCheck<Object>();

        mnc.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        BlockStmt block = md.getBody();

        IfStmt stmt = (IfStmt) block.getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        BinaryExpr lbe = (BinaryExpr) be.getLeft();

        Assert.assertTrue(lbe.getOperator().equals(BinaryExpr.Operator.equals));
    }

    @Test
    public void testAndWithHighCommonAncestors() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ public void bar(Object c){ if((c.toString().equals(\"hello\") && true == true) && c!= null) {}} }");

        MisplacedNullCheck<?> mnc = new MisplacedNullCheck<Object>();

        mnc.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);

        BlockStmt block = md.getBody();

        IfStmt stmt = (IfStmt) block.getStmts().get(0);

        BinaryExpr be = (BinaryExpr) stmt.getCondition();

        BinaryExpr lbe = (BinaryExpr) be.getLeft();

        Assert.assertTrue(lbe.getOperator().equals(BinaryExpr.Operator.notEquals));
    }
}
