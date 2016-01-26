package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.List;

import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

@RequiresSemanticAnalysis
public class ExtendsObject<T> extends AbstractPMDRuleVisitor<T> {

   @Override
   public void visit(ClassOrInterfaceDeclaration n, T ctx) {
      if (!n.isInterface()) {
         List<ClassOrInterfaceType> extendsList = n.getExtends();

         if (extendsList != null) {
            ClassOrInterfaceType extendsElem = extendsList.get(0);
            SymbolData sd = extendsElem.getSymbolData();
            if(sd != null){
               Class<?> clazz = sd.getClazz();
               if(clazz.getName().equals(Object.class.getName())){
                  n.setExtends(null);
               }
            }
         }
      }
      super.visit(n, ctx);
   }
}
