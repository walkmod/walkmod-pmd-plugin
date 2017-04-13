package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.junit.Test;
import org.walkmod.pmd.ruleset.java.testsupport.SemanticFixtureTest;


public class CollapsibleIfStatementsFixtureTest extends SemanticFixtureTest {
   @Test
   public void testFixture() throws Exception {
      doTestFixture(getClass().getSimpleName(), new CollapsibleIfStatements());
   }
}
