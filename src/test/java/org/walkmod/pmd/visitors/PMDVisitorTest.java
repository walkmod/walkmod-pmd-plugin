package org.walkmod.pmd.visitors;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.pmd.ruleset.java.basic.visitors.OverrideBothEqualsAndHashcode;
import org.walkmod.walkers.VisitorContext;


public class PMDVisitorTest extends SemanticTest{

   @Test
   public void testCfgFile() throws Exception {
      PMDVisitor visitor = new PMDVisitor();
      visitor.setConfigurationFile("src/test/resources/pmd.xml");
      Set<String> rules = visitor.getRules();

      Assert.assertNotNull(rules);
      
      Assert.assertTrue(rules.contains("BooleanInstantiation"));

   }
   
   @Test
   public void testCfgFile2() throws Exception {
      PMDVisitor visitor = new PMDVisitor();
      visitor.setConfigurationFile("src/test/resources/pmd2.xml");
      Set<String> rules = visitor.getRules();

      Assert.assertNotNull(rules);
      
      Assert.assertTrue(rules.contains("AvoidUsingOctalValues"));

   }
   
   @Test
   public void testWorkflow() throws Exception{
      PMDVisitor visitor = new PMDVisitor();
      visitor.setConfigurationFile("src/test/resources/pmd.xml");
      List<AbstractPMDRuleVisitor<?>> list = new LinkedList<AbstractPMDRuleVisitor<?>>();
      OverrideBothEqualsAndHashcode<?> aux = new OverrideBothEqualsAndHashcode<Object>();
      aux.visitChildren(false);
      list.add(aux);
      visitor.setVisitors(list);
      CompilationUnit cu = ASTManager.parse(new File("src/test/resources/examples/overrideBothEqualsAndHashcode2.txt"));
      visitor.visit(cu, new VisitorContext());
      List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();
      Assert.assertNotNull(members);
      Assert.assertEquals(2, members.size());
      Assert.assertEquals("hashCode", ((MethodDeclaration) members.get(1)).getName());
      
   }
}
