package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.junit.Test;
import org.walkmod.pmd.ruleset.java.basic.visitors.SimplifiedTernary;
import org.walkmod.pmd.ruleset.java.testsupport.SemanticFixtureTest;


public class SimplifiedTernaryFixtureTest extends SemanticFixtureTest {

   @Test
   public void testFixture() throws Exception {
      doTestFixture(getClass().getSimpleName(), new SimplifiedTernary());
   }

}
