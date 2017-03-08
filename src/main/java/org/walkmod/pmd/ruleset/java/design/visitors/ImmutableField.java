package org.walkmod.pmd.ruleset.java.design.visitors;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.FieldDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.javalang.ast.expr.AssignExpr;
import org.walkmod.pmd.visitors.PMDRuleVisitor;
import org.walkmod.pmd.visitors.Modification;

@Modification
public class ImmutableField extends PMDRuleVisitor {

    @Override
    public void visit(FieldDeclaration n, Node node) {
        FieldDeclaration aux = (FieldDeclaration) node;
        int modifiers = aux.getModifiers();
        if (ModifierSet.isPrivate(modifiers)) {
            List<VariableDeclarator> vds = aux.getVariables();

            if (vds != null && vds.size() == 1) {
                VariableDeclarator vd = vds.get(0);
                List<SymbolReference> usages = vd.getUsages();

                if (usages != null) {
                    Iterator<SymbolReference> it = usages.iterator();
                    boolean isUsedFromMethod = false;
                    while (it.hasNext() && !isUsedFromMethod) {
                        SymbolReference next = it.next();
                        Node auxNode = (Node) next;
                       
                        isUsedFromMethod = (isUsedFromMethod(auxNode) && isAssigned(auxNode));
                    }
                    if (!isUsedFromMethod) {
                        int auxModifiers = ModifierSet.addModifier(modifiers, Modifier.FINAL);
                        aux.setModifiers(auxModifiers);
                    }
                }
            }
        }
    }
    
    private boolean isAssigned(Node node){
        if(node == null){
            return false;
        }
        Node parent = node.getParentNode();
        
        if(parent instanceof AssignExpr){
            AssignExpr ae = (AssignExpr) parent;
            return (ae.getTarget() == node);
        }
       
        return false;
    }

    private boolean isUsedFromMethod(Node node) {
        if (node == null) {
            return false;
        }
        if (node instanceof MethodDeclaration) {
            return true;
        } else {
            return isUsedFromMethod(node.getParentNode());
        }

    }
}
