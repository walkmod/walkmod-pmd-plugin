package org.walkmod.pmd.ruleset.java.unusedcode.visitors;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.PMDRuleVisitor;
import org.walkmod.pmd.visitors.Removal;

import java.util.List;

@RequiresSemanticAnalysis
@Removal
public class UnusedPrivateMethod extends PMDRuleVisitor{

    @Override
    public void visit(MethodDeclaration n, Node ctx) {
        
        if(ModifierSet.isPrivate(n.getModifiers())){

            boolean isRemovable = true;
            if ("readObject".equals(n.getName())){
                List<Parameter> params = n.getParameters();
                if(params != null && params.size() == 1){
                    Type type = params.get(0).getType();
                    SymbolData sd = type.getSymbolData();
                    isRemovable = !(sd != null && "java.io.ObjectInputStream".equals(sd.getName()));
                }
            }
            else if ("writeObject".equals(n.getName())){
                List<Parameter> params = n.getParameters();
                if(params != null && params.size() == 1){
                    Type type = params.get(0).getType();
                    SymbolData sd = type.getSymbolData();
                    isRemovable = !(sd != null && "java.io.ObjectOutputStream".equals(sd.getName()));
                }
            }

            if(isRemovable) {
                List<SymbolReference> usages = n.getUsages();

                if (usages == null || usages.isEmpty()) {
                    n.remove();
                }
            }
        }
    }
}
