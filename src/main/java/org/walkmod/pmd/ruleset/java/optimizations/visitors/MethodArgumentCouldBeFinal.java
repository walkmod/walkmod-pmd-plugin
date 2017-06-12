package org.walkmod.pmd.ruleset.java.optimizations.visitors;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.expr.AssignExpr;
import org.walkmod.javalang.ast.expr.EnclosedExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
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
                    areFinal = areFinal && !isAssigned(srNode);
                }
            }

            if (areFinal) {
                aux.setModifiers(ModifierSet.addModifier(n.getModifiers(), Modifier.FINAL));
            }
        }
    }

    private boolean isAssigned(Node reference) {
        Node parent = reference.getParentNode();

        while (parent instanceof EnclosedExpr) {
            parent = parent.getParentNode();
        }
        if (parent instanceof AssignExpr) {
            AssignExpr assign = (AssignExpr) parent;
            return assign.getTarget() == reference;
        }
        if (parent instanceof UnaryExpr) {
            UnaryExpr ue = (UnaryExpr) parent;
            UnaryExpr.Operator op = ue.getOperator();

            if (op.equals(UnaryExpr.Operator.posIncrement)
                    || op.equals(UnaryExpr.Operator.posDecrement)
                    || op.equals(UnaryExpr.Operator.preDecrement)
                    || op.equals(UnaryExpr.Operator.posDecrement)) {
                return true;
            }
        }

        return false;
    }
}
