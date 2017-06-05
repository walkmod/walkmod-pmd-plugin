package org.walkmod.pmd.ruleset.java.optimizations.visitors;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.common.FinalAnalyzer;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
@Modification
public class LocalVariableCouldBeFinal extends PMDRuleVisitor {

    @Override
    public void visit(VariableDeclarationExpr n, Node context) {
        super.visit(n, context);
        VariableDeclarationExpr aux = (VariableDeclarationExpr) context;
        if (n.getParentNode() instanceof ExpressionStmt) {
            List<VariableDeclarator> vars = n.getVars();
            if (vars != null) {
                if (!ModifierSet.isFinal(n.getModifiers())) {
                    boolean areFinal = true;
                    Iterator<VariableDeclarator> it = vars.iterator();

                    while (it.hasNext() && areFinal) {
                        VariableDeclarator vd = it.next();

                        List<SymbolReference> usages = vd.getUsages();

                        if (usages != null) {
                            Iterator<SymbolReference> itUsages = usages.iterator();

                            while (itUsages.hasNext() && areFinal) {

                                SymbolReference sr = itUsages.next();
                                Node srNode = (Node) sr;
                                areFinal = areFinal && !FinalAnalyzer.isAssigned(srNode);
                            }
                        }
                    }
                    if (areFinal) {
                        aux.setModifiers(ModifierSet.addModifier(n.getModifiers(), Modifier.FINAL));
                    }
                }
            }
        }
    }
}
