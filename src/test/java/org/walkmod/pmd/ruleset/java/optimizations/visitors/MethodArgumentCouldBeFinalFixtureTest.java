package org.walkmod.pmd.ruleset.java.optimizations.visitors;

import org.junit.Test;

import org.walkmod.pmd.ruleset.java.testsupport.SemanticFixtureTest;

public class MethodArgumentCouldBeFinalFixtureTest extends SemanticFixtureTest {

    @Test
    public void testFixture() throws Exception {
        doTestFixture(getClass().getSimpleName(), new MethodArgumentCouldBeFinal());
    }
}
