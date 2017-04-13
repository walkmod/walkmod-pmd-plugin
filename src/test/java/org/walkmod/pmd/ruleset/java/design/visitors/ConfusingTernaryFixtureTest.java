package org.walkmod.pmd.ruleset.java.design.visitors;

import org.junit.Test;
import org.walkmod.pmd.ruleset.java.testsupport.SemanticFixtureTest;


public class ConfusingTernaryFixtureTest extends SemanticFixtureTest {
   @Test
   public void testFixture() throws Exception {
      doTestFixture(getClass().getSimpleName(), new ConfusingTernary());
   }
}
