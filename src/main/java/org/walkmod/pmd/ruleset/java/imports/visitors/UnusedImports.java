package org.walkmod.pmd.ruleset.java.imports.visitors;

import java.util.List;

import org.walkmod.javalang.ast.ImportDeclaration;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.PMDRuleVisitor;
import org.walkmod.pmd.visitors.Removal;

@RequiresSemanticAnalysis
@Removal
public class UnusedImports extends PMDRuleVisitor {

    @Override
    public void visit(ImportDeclaration n, Node ctx) {
        ImportDeclaration aux = (ImportDeclaration) ctx;
        List<SymbolReference> sr = n.getUsages();
        if (sr == null || sr.isEmpty()) {
            aux.getParentNode().removeChild(n);
        }
    }
}
