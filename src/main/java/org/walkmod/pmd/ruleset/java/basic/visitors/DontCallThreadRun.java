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

import java.lang.reflect.Method;
import java.util.List;

import org.walkmod.javalang.ast.MethodSymbolData;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
@Modification
public class DontCallThreadRun extends PMDRuleVisitor {

   @Override
   public void visit(MethodCallExpr n, Node ctx) {
       super.visit(n, ctx);
      if (n.getName().equals("run")) {

         List<Expression> args = n.getArgs();
         if (args == null || args.isEmpty()) {
            MethodSymbolData msd = n.getSymbolData();
            if (msd != null) {
               Method m = msd.getMethod();
               if (Thread.class.isAssignableFrom(m.getDeclaringClass())) {
                  ((MethodCallExpr)ctx).setName("start");
               }
            }
         }
      }
     
   }

}
