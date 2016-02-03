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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ForStmt;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

@RequiresSemanticAnalysis
public class JumbledIncrementer<T> extends AbstractPMDRuleVisitor<T> {

   public void visit(ForStmt n, T ctx) {
      Node parent = n.getParentNode();
      ForStmt parentForStmt = null;
      if (parent instanceof ForStmt) {
         parentForStmt = (ForStmt) parent;
      } else if (parent instanceof BlockStmt) {
         Node grandParent = parent.getParentNode();
         if (grandParent instanceof ForStmt) {
            parentForStmt = (ForStmt) grandParent;
         }
      }
      if (parentForStmt == null) {
         super.visit(n, ctx);
      } else {

         List<Expression> updateExprs = n.getUpdate();
         if (updateExprs != null) {
            List<Expression> initExprs = parentForStmt.getInit();
            List<Expression> thisInitExprs = n.getInit();
            String varToUpdate = null;
            UnaryExpr.Operator operator = null;

            //determining the candidate variable to update
            if (thisInitExprs != null) {
               if (thisInitExprs.size() == 1) {
                  Expression thisInitExpr = thisInitExprs.get(0);
                  if (thisInitExpr instanceof VariableDeclarationExpr) {
                     List<VariableDeclarator> vars = ((VariableDeclarationExpr) thisInitExpr).getVars();
                     if (vars != null) {
                        if (vars.size() == 1) {
                           Type aux = ((VariableDeclarationExpr) thisInitExpr).getType();
                           SymbolData sd = aux.getSymbolData();
                           if (sd != null) {
                              String name = sd.getClazz().getName();
                              if (name.equals("int") || name.equals("long") || name.equals("double")
                                    || name.equals("float")) {
                                 VariableDeclarator var = vars.get(0);
                                 List<SymbolReference> refs = var.getUsages();

                                 if (refs != null) {
                                    Iterator<SymbolReference> it = refs.iterator();
                                    while (varToUpdate == null && it.hasNext()) {
                                       SymbolReference current = it.next();
                                       boolean isUpdated = false;
                                       for (Expression update : updateExprs) {
                                          if (update.contains((Node) current)) {
                                             isUpdated = true;
                                          }
                                       }

                                       if (!isUpdated) {
                                          Expression compare = n.getCompare();
                                          if (compare != null) {
                                             if (compare instanceof BinaryExpr) {
                                                BinaryExpr be = (BinaryExpr) compare;
                                                BinaryExpr.Operator op = be.getOperator();
                                                if (op.equals(BinaryExpr.Operator.greater)
                                                      || op.equals(BinaryExpr.Operator.greaterEquals)) {
                                                   operator = UnaryExpr.Operator.posDecrement;
                                                } else if (op.equals(BinaryExpr.Operator.less)
                                                      || op.equals(BinaryExpr.Operator.lessEquals)) {
                                                   operator = UnaryExpr.Operator.posIncrement;
                                                }
                                             }
                                          }
                                          if (operator != null) {
                                             varToUpdate = var.getId().getName();
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
            //replacing the update expression
            if (initExprs != null && varToUpdate != null) {

               Iterator<Expression> it = initExprs.iterator();
               while (it.hasNext()) {
                  Expression init = it.next();
                  if (init instanceof VariableDeclarationExpr) {
                     List<VariableDeclarator> vars = ((VariableDeclarationExpr) init).getVars();
                     if (vars != null) {
                        Iterator<VariableDeclarator> itVars = vars.iterator();
                        while (itVars.hasNext()) {
                           VariableDeclarator vdr = itVars.next();
                           List<SymbolReference> refs = vdr.getUsages();
                           List<SymbolReference> updatedRefs = new LinkedList<SymbolReference>();
                           if (refs != null) {
                              for (SymbolReference sr : refs) {
                                 Iterator<Expression> itUpExpr = updateExprs.iterator();
                                 boolean updated = false;

                                 while (itUpExpr.hasNext() && !updated) {
                                    Expression updateExpr = itUpExpr.next();
                                    if (updateExpr.contains((Node) sr)) {
                                       itUpExpr.remove();
                                       updated = true;
                                    }
                                 }
                                 if (!updated) {
                                    updatedRefs.add(sr);
                                 } else {
                                    updateExprs.add(new UnaryExpr(new NameExpr(varToUpdate), operator));
                                 }
                              }
                              vdr.setUsages(updatedRefs);
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }
}
