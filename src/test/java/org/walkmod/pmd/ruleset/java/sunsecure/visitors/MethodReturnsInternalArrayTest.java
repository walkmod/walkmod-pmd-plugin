package org.walkmod.pmd.ruleset.java.sunsecure.visitors;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.stmt.ReturnStmt;
import org.walkmod.javalang.test.SemanticTest;

public class MethodReturnsInternalArrayTest extends SemanticTest {

    @Test
    public void when_array_is_exposed_then_is_copied() throws Exception {
        CompilationUnit cu = compile("public class Foo{ private int[] a; public int[] getA(){ return a; }}");
        MethodReturnsInternalArray visitor = new MethodReturnsInternalArray();
        
        visitor.visit(cu, cu);
        
        MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(1);
        ReturnStmt returnStmt = (ReturnStmt) md.getBody().getStmts().get(0);
        
        Assert.assertTrue(returnStmt.getExpr() instanceof ConditionalExpr);
       
    }
    
    @Test
    public void when_array_is_exposed_then_is_copied_2() throws Exception {
        CompilationUnit cu = compile("public class Foo{ private int a[]; public int[] getA(){ return a; }}");
        MethodReturnsInternalArray visitor = new MethodReturnsInternalArray();
        
        visitor.visit(cu, cu);
        
        MethodDeclaration md = (MethodDeclaration)cu.getTypes().get(0).getMembers().get(1);
        ReturnStmt returnStmt = (ReturnStmt) md.getBody().getStmts().get(0);
        
        Assert.assertTrue(returnStmt.getExpr() instanceof ConditionalExpr);
    }
}
