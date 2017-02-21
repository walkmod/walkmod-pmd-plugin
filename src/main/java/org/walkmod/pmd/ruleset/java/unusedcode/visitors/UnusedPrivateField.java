package org.walkmod.pmd.ruleset.java.unusedcode.visitors;

import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.FieldDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

public class UnusedPrivateField extends PMDRuleVisitor {

    @Override
    public void visit(FieldDeclaration n, Node ctx) {
        super.visit(n, ctx);
        FieldDeclaration vde = (FieldDeclaration) ctx;

        List<VariableDeclarator> vars = vde.getVariables();

        if (vars == null || vars.isEmpty()) {

            vde.remove();

        }
    }

    @Override
    public void visit(VariableDeclarator n, Node ctx) {
        super.visit(n, ctx);
        if (n.getParentNode() instanceof FieldDeclaration) {

            FieldDeclaration fd = (FieldDeclaration) n.getParentNode();
            if (ModifierSet.isPrivate(fd.getModifiers())) {
                List<SymbolReference> usages = n.getUsages();

                if (usages == null || usages.isEmpty()) {
                    VariableDeclarator aux = (VariableDeclarator) ctx;
                    aux.remove();
                }
            }
        }
    }
}
