package org.walkmod.pmd.ruleset.java.braces.visitors;

import org.junit.Test;
import org.walkmod.pmd.ruleset.java.braces.visitors.StatementsMustUseBraces;
import org.walkmod.pmd.ruleset.java.testsupport.SemanticFixtureTest;


public class StatementsMustUseBracesFixtureTest extends SemanticFixtureTest {
   @Test
   public void testFixture() throws Exception {
      doTestFixture(getClass().getSimpleName(), new StatementsMustUseBraces());
   }
}
