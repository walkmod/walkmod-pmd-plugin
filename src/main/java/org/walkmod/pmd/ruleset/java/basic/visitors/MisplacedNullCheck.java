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

import org.walkmod.javalang.ast.FieldSymbolData;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolDataAware;
import org.walkmod.javalang.ast.SymbolDefinition;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.BinaryExpr.Operator;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.NullLiteralExpr;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;

@RequiresSemanticAnalysis
public class MisplacedNullCheck<T> extends VoidVisitorAdapter<T> {

   @Override
   public void visit(BinaryExpr n, T ctx) {
      Operator op = n.getOperator();
      if (op.equals(Operator.notEquals) || op.equals(Operator.equals)) {
         Expression right = n.getRight();
         Expression left = n.getLeft();
         Node parent = n.getParentNode();

         if (parent instanceof BinaryExpr) {
            BinaryExpr bo = (BinaryExpr) parent;
            if (right instanceof NullLiteralExpr) {

               if (left instanceof NameExpr) {

                  if (bo.getRight() == n) {
                     ReorderCondition visitor = new ReorderCondition((NameExpr) left);
                     bo.getLeft().accept(visitor, bo);
                  }

               } else if (left instanceof FieldAccessExpr) {
                  if (bo.getRight() == n) {
                     ReorderCondition visitor = new ReorderCondition((FieldAccessExpr) left);
                     bo.getLeft().accept(visitor, bo);
                  }
               }
            } else if (left instanceof NullLiteralExpr) {

               if (right instanceof NameExpr) {

                  if (bo.getRight() == n) {
                     ReorderCondition visitor = new ReorderCondition((NameExpr) right);
                     bo.getLeft().accept(visitor, bo);
                  }

               } else if (right instanceof FieldAccessExpr) {
                  if (bo.getRight() == n) {
                     ReorderCondition visitor = new ReorderCondition((FieldAccessExpr) right);
                     bo.getLeft().accept(visitor, bo);
                  }
               }
            }
         }
      } else {
         super.visit(n, ctx);
      }
   }

   private class ReorderCondition extends VoidVisitorAdapter<BinaryExpr> {

      private SymbolReference referredExpression;

      public ReorderCondition(NameExpr referredExpression) {
         this.referredExpression = referredExpression;
      }

      public ReorderCondition(FieldAccessExpr referredExpression) {
         this.referredExpression = referredExpression;
      }

      @Override
      public void visit(NameExpr n, BinaryExpr ctx) {

         replace(n, ctx);
      }

      private void replace(SymbolReference n, BinaryExpr ctx) {

         SymbolDefinition sd = n.getSymbolDefinition();

         boolean valid = false;

         if (sd != null && sd == referredExpression.getSymbolDefinition()) {
            SymbolReference srParent = null;
            SymbolReference srParentRef = null;
            Node parentNode = null;

            if (n instanceof FieldAccessExpr) {
               parentNode = ((FieldAccessExpr) n).getScope();
               if (parentNode != null) {
                  if (parentNode instanceof SymbolReference) {
                     srParent = (SymbolReference) parentNode;
                  }
               }
            }

            if (referredExpression instanceof FieldAccessExpr) {
               parentNode = ((FieldAccessExpr) referredExpression).getScope();
               if (parentNode != null) {
                  if (parentNode instanceof SymbolReference) {
                     srParentRef = (SymbolReference) parentNode;
                  }
               }
            }

            valid = (srParent == null && srParentRef == null)
                  || (srParent != null && srParentRef != null && srParent.getSymbolDefinition() == srParentRef
                        .getSymbolDefinition());

         }
         if (sd == null && referredExpression.getSymbolDefinition() == null) {
            if (n instanceof SymbolDataAware && referredExpression instanceof SymbolDataAware) {
               SymbolDataAware<?> sda = (SymbolDataAware<?>) n;
               SymbolDataAware<?> sda2 = (SymbolDataAware<?>) referredExpression;
               if (sda.getSymbolData() instanceof FieldSymbolData && sda2.getSymbolData() instanceof FieldSymbolData) {
                  FieldSymbolData fsd = (FieldSymbolData) sda.getSymbolData();
                  FieldSymbolData fsd2 = (FieldSymbolData) sda2.getSymbolData();
                  valid = (fsd.getField().equals(fsd2.getField()));
               }
            }
         }

         if (valid) {

            Expression left = ctx.getLeft();
            Expression right = ctx.getRight();
            ctx.setRight(left);
            ctx.setLeft(right);

         }
      }

      @Override
      public void visit(FieldAccessExpr n, BinaryExpr ctx) {

         replace(n, ctx);

      }
   }
}
