package org.walkmod.pmd.ruleset.java.testsupport;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.javalang.visitors.VoidVisitor;

/**
 *
 */
public class SemanticFixtureTest extends SemanticTest {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    protected void doTestFixture(String suite, final VoidVisitor<Node> visitor) throws Exception {
       final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
       final String fixtureDir = suite.toLowerCase().replace("fixturetest", "");

       for (Resource r : resolver.getResources("fixture/pmd/" + fixtureDir + "/in/*.java")) {
          final File inFile = r.getFile();
          final File outFile = new File(new File(inFile.getParentFile().getParentFile(), "out"), inFile.getName());
          final String in = FileUtils.readFileToString(inFile, UTF8);
          final String out = FileUtils.readFileToString(outFile, UTF8);

          CompilationUnit cu = compile(in);
          visitor.visit(cu, cu);
          final String transformed = cu.getPrettySource(' ', 0, 4);
          assertEquals(">" + out + "<", ">" + transformed + "<");
       }
    }
}
