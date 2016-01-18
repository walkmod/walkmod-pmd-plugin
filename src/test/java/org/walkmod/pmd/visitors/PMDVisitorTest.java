package org.walkmod.pmd.visitors;

import org.junit.Test;

public class PMDVisitorTest {

   @Test
   public void testCfgFile() throws Exception{
      PMDVisitor<?> visitor = new PMDVisitor<Object>();
      visitor.setConfigurationFile("src/test/resources/pmd.xml");
   }
}
