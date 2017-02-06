package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.EnclosedExpr;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.test.SemanticTest;

public class BrokenNullCheckTest extends SemanticTest {

    @Test
    public void testBasicCase() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if (s!=null || !s.equals(\"\")){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.and));
    }

    @Test
    public void testBasicCase2() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if (s!=null || s.equals(\"\")){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.and));
    }

    @Test
    public void testBasicCaseAnd() throws Exception {

        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if (s==null && s.equals(\"\")){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.or));
    }

    @Test
    public void testBinaryOperatorsCase() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if (s!=null || (!s.equals(\"\") && 3 < 4)){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.and));
    }

    @Test
    public void testBinaryOperatorsCaseAnd() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if (s==null && (!s.equals(\"\") && 3 < 4)){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.or));
    }

    @Test
    public void testBinaryOperatorsCase2() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if (s!=null || (s.equals(\"\") && 3 < 4)){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.and));
    }

    @Test
    public void testBinaryOperatorsCase2And() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if (s==null && (s.equals(\"\") && 3 < 4)){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.or));
    }

    @Test
    public void testBinaryOperatorsCase3() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if ((s!=null && 3 < 4) || !s.equals(\"\")){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.or));
        Assert.assertTrue(binExpr.getRight() instanceof EnclosedExpr);

    }

    @Test
    public void testBinaryOperatorsCase3And() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if ((s==null && 3 < 4) && !s.equals(\"\")){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.and));
        Assert.assertTrue(binExpr.getRight() instanceof EnclosedExpr);

    }

    @Test
    public void testBinaryOperatorsCase4() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if ((s!=null && 3 < 4) || s.equals(\"\")){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.or));
        Assert.assertTrue(binExpr.getRight() instanceof EnclosedExpr);

    }

    @Test
    public void testBinaryOperatorsCase4And() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ public void bar(String s) { if ((s!=null && 3 < 4) || s.equals(\"\")){ System.out.println(s); } } }");
        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.or));
        Assert.assertTrue(binExpr.getRight() instanceof EnclosedExpr);

    }

    @Test
    public void testIssueWithValidBooleanExpressions() throws Exception {
        CompilationUnit cu = compile("public class Foo { public boolean equals(final Object o) { "
                + "if (o == null || getClass() != o.getClass()) {return true;} return false;} }");

        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();

        visitor.visit(cu, null);

        MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        List<Statement> stmts = md.getBody().getStmts();

        IfStmt ifStmt = (IfStmt) stmts.get(0);
        BinaryExpr binExpr = (BinaryExpr) ifStmt.getCondition();

        Assert.assertTrue(binExpr.getOperator().equals(BinaryExpr.Operator.or));
    }

    @Test
    public void testIssueWithValidTernaryExpressions() throws Exception {
        CompilationUnit cu = compile("public class Foo { public int hashCode() {"
                + " final int prime = 31; int result = 1; Object authConfigMap = null; "
                + " result = prime * result + ((authConfigMap == null) ? 0 : authConfigMap.hashCode()); return result; } }");

        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();
        visitor.visit(cu, null);
        //MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
        //List<Statement> stmts = md.getBody().getStmts();
        Assert.assertTrue(true);

    }

    @Test
    public void testIssueInvalidNullCheck() throws Exception {
        CompilationUnit cu = compile("public class Foo { String networkMode; String PREDEFINED_NETWORKS;"
                + " public boolean isUserDefinedNetwork() {  return networkMode != null && !PREDEFINED_NETWORKS.contains(networkMode) && !networkMode.startsWith(\"container:\"); } } ");

        BrokenNullCheck<?> visitor = new BrokenNullCheck<Object>();
        visitor.visit(cu, null);
        Assert.assertTrue(true);
    }

  
}
