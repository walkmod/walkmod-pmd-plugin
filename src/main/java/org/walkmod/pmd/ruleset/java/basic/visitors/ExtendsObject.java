/* 
  Copyright (C) 2016 Raquel Pau.
 
  Walkmod is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
 
  Walkmod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public License
  along with Walkmod.  If not, see <http://www.gnu.org/licenses/>.*/
package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
public class ExtendsObject extends PMDRuleVisitor {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Node ctx) {

        if (!n.isInterface()) {
            List<ClassOrInterfaceType> extendsList = n.getExtends();

            if (extendsList != null) {
                ClassOrInterfaceType extendsElem = extendsList.get(0);
                SymbolData sd = extendsElem.getSymbolData();
                if (sd != null) {
                    Class<?> clazz = sd.getClazz();
                    if (clazz.getName().equals(Object.class.getName())) {
                        ((ClassOrInterfaceDeclaration) n).setExtends(null);
                    }
                }
            }
        }
        super.visit(n, ctx);
    }
}
