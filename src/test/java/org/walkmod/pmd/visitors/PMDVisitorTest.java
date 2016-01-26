package org.walkmod.pmd.visitors;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;


public class PMDVisitorTest {

   @Test
   public void testCfgFile() throws Exception {
      PMDVisitor visitor = new PMDVisitor();
      visitor.setConfigurationFile("src/test/resources/pmd.xml");
      Set<String> rules = visitor.getRules();

      Assert.assertNotNull(rules);
      
      Assert.assertTrue(rules.contains("JumbledIncrementer"));

   }
}
