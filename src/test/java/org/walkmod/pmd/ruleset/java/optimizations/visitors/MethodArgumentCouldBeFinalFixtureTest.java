package org.walkmod.pmd.ruleset.java.optimizations.visitors;

import org.junit.Test;

import org.walkmod.pmd.ruleset.java.testsupport.SemanticFixtureTest;

import javax.lang.model.SourceVersion;

public class MethodArgumentCouldBeFinalFixtureTest extends SemanticFixtureTest {

    @Test
    public void testFixture() throws Exception {
        doTestFixture(getClass().getSimpleName(), "7", new MethodArgumentCouldBeFinal());
    }

    @Test
    public void testFixture8() throws Exception {
        if (SourceVersion.latestSupported().ordinal() >= 8) {
            doTestFixture(getClass().getSimpleName(), "8", new MethodArgumentCouldBeFinal());
        }
    }
}
