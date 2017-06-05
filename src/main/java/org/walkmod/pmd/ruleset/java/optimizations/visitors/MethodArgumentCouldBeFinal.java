package org.walkmod.pmd.ruleset.java.optimizations.visitors;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.common.FinalAnalyzer;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
@Modification
public class MethodArgumentCouldBeFinal extends PMDRuleVisitor {

    @Override
    public void visit(Parameter n, Node context) {

        super.visit(n, context);
        Parameter aux = (Parameter) context;

        if (!ModifierSet.isFinal(n.getModifiers())) {

            List<SymbolReference> usages = n.getUsages();
            boolean areFinal = true;
            if (usages != null) {
                Iterator<SymbolReference> itUsages = usages.iterator();

                while (itUsages.hasNext() && areFinal) {

                    SymbolReference sr = itUsages.next();
                    Node srNode = (Node) sr;
                    areFinal = areFinal && !FinalAnalyzer.isAssigned(srNode);
                }
            }

            if (areFinal) {
                aux.setModifiers(ModifierSet.addModifier(n.getModifiers(), Modifier.FINAL));
            }
        }
    }
}
