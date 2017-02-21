package org.walkmod.pmd.ruleset.java.unusedcode.visitors;

import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
public class UnusedLocalVariable extends PMDRuleVisitor {

    @Override
    public void visit(VariableDeclarationExpr n, Node ctx) {
        super.visit(n, ctx);
        VariableDeclarationExpr vde = (VariableDeclarationExpr) ctx;

        List<VariableDeclarator> vars = vde.getVars();

        if (vars == null || vars.isEmpty()) {
            if (vde.getParentNode() instanceof ExpressionStmt) {
                vde.getParentNode().remove();
            }
        }
    }

    @Override
    public void visit(VariableDeclarator n, Node ctx) {
        super.visit(n, ctx);
        if (n.getParentNode() instanceof VariableDeclarationExpr) {

            List<SymbolReference> usages = n.getUsages();

            if (usages == null || usages.isEmpty()) {
                VariableDeclarator aux = (VariableDeclarator) ctx;
                aux.remove();
            }
        }
    }
}
