package org.walkmod.pmd.visitors;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.pmd.Rule;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.javalang.visitors.CloneVisitor;
import org.walkmod.pmd.ruleset.java.basic.visitors.OverrideBothEqualsAndHashcode;
import org.walkmod.walkers.VisitorContext;

import net.sourceforge.pmd.RuleSet;

public class PMDVisitorTest extends SemanticTest {

    @Test
    public void testCfgFile() throws Exception {
        PMDVisitor visitor = new PMDVisitor();
        visitor.setConfigurationFile("src/test/resources/pmd.xml");
        RuleSet rules = visitor.getRules();

        Assert.assertNotNull(rules);

        Assert.assertNotNull(rules.getRuleByName("BooleanInstantiation"));

    }

    @Test
    public void testMavenDefaults() throws Exception{
        PMDVisitor visitor = new PMDVisitor();
        String defaults = "java-basic, java-empty, java-imports, java-unnecessary, java-unusedcode";
        visitor.setConfigurationFile(defaults);
        RuleSet rules = visitor.getRules();
        Assert.assertNotNull(rules);

        Assert.assertNotNull(rules.getRuleByName("UnusedImports"));
    }

    @Test
    public void testCfgFile2() throws Exception {
        PMDVisitor visitor = new PMDVisitor();
        visitor.setConfigurationFile("src/test/resources/pmd2.xml");
        RuleSet rules = visitor.getRules();

        Assert.assertNotNull(rules);

        Assert.assertNotNull(rules.getRuleByName("AvoidUsingOctalValues"));

    }

    @Test
    public void testWorkflow() throws Exception {
        PMDVisitor visitor = new PMDVisitor();
        visitor.setConfigurationFile("src/test/resources/pmd.xml");
        List<PMDRuleVisitor> list = new LinkedList<PMDRuleVisitor>();
        OverrideBothEqualsAndHashcode aux = new OverrideBothEqualsAndHashcode();
        visitor.setSplitExecution(true);
        list.add(aux);
        visitor.setVisitors(list);
        CompilationUnit cu = ASTManager
                .parse(new File("src/test/resources/examples/overrideBothEqualsAndHashcode2.txt"));
        VisitorContext ctx = new VisitorContext();
        visitor.visit(cu, ctx);
        cu = (CompilationUnit) ctx.getResultNodes().iterator().next();
        List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
        Assert.assertNotNull(members);
        Assert.assertEquals(2, members.size());
        Assert.assertEquals("hashCode", ((MethodDeclaration) members.get(1)).getName());

    }

    @Test
    public void testRulesPriority_issue9() throws Exception{
        String code = "public class Foo { " +
                "private boolean x = true; " +
                "public void foo(int y) {" +
                " if (x) { " +
                "   if(y ==0) { " +
                "       System.out.println(\"hello\");" +
                "   } " +
                " }" +
                "} " +
                "}";
        CompilationUnit cu = compile(code);
        PMDVisitor visitor = new PMDVisitor();

        VisitorContext ctx = new VisitorContext();
        visitor.visit(cu, ctx);

        Assert.assertEquals(2, cu.getTypes().get(0).getMembers().size());

    }

    @Test
    public void testRulesPriority_issue10() throws Exception{
        String code = FileUtils.readFileToString(new File("src/test/resources/examples/NPE1.txt"));
        CompilationUnit cu = compile(code);
        PMDVisitor visitor = new PMDVisitor();

        VisitorContext ctx = new VisitorContext();
        visitor.visit(cu, ctx);

        Assert.assertTrue(true);

    }
}
