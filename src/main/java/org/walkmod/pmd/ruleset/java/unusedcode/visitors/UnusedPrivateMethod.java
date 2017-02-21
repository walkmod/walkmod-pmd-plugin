package org.walkmod.pmd.ruleset.java.unusedcode.visitors;

import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
public class UnusedPrivateMethod extends PMDRuleVisitor{

    @Override
    public void visit(MethodDeclaration n, Node ctx) {
        
        if(ModifierSet.isPrivate(n.getModifiers())){
            List<SymbolReference> usages = n.getUsages();
            
            if(usages == null || usages.isEmpty()){
                n.remove();
            }
        }
    }
}
