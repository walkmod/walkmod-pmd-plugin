package org.walkmod.pmd.ruleset.java.braces.visitors;

import org.walkmod.pmd.visitors.Modification;

/**
 * https://pmd.github.io/pmd-5.5.5/pmd-java/rules/java/braces.html
 */
@Modification
public class WhileLoopsMustUseBraces extends StatementsMustUseBraces {

    public WhileLoopsMustUseBraces() {
        super(Flag.While);
    }
}
