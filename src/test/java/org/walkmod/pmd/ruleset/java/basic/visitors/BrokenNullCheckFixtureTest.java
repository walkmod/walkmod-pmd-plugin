package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.junit.Test;
import org.walkmod.javalang.ast.Node;
import org.walkmod.pmd.ruleset.java.testsupport.SemanticFixtureTest;


public class BrokenNullCheckFixtureTest extends SemanticFixtureTest {
   @Test
   public void testFixture() throws Exception {
      doTestFixture(getClass().getSimpleName(), new BrokenNullCheck<Node>());
   }
}
