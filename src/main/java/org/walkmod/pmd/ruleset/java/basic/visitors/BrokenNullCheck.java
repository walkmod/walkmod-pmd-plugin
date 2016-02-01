package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ParseException;
import org.walkmod.javalang.ast.FieldSymbolData;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolDataAware;
import org.walkmod.javalang.ast.SymbolDefinition;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.BinaryExpr.Operator;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.javalang.ast.expr.EnclosedExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.NullLiteralExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

@RequiresSemanticAnalysis
public class BrokenNullCheck<T> extends AbstractPMDRuleVisitor<T> {

   @Override
   public void visit(BinaryExpr n, T ctx) {
      Operator op = n.getOperator();
      if (op.equals(Operator.notEquals) || op.equals(Operator.equals)) {
         Expression right = n.getRight();
         Expression left = n.getLeft();
         Node parent = n.getParentNode();

         if (parent instanceof Expression) {

            if (right instanceof NullLiteralExpr) {
               FixOperand visitor = null;

               if (left instanceof NameExpr) {
                  visitor = new FixOperand((NameExpr) left);
               } else if (left instanceof FieldAccessExpr) {
                  visitor = new FixOperand((FieldAccessExpr) left);
               }

               if (visitor != null) {
                  Expression lastExpression = null;
                  while (parent instanceof Expression) {
                     lastExpression = (Expression) parent;
                     parent = parent.getParentNode();
                  }
                  BinaryExpr parentBinaryExpr = null;
                  parent = n.getParentNode();

                  if (parent == lastExpression && parent instanceof BinaryExpr) {
                     parentBinaryExpr = (BinaryExpr) parent;
                  } else {
                     while (parent != lastExpression && parentBinaryExpr == null) {
                        if (parent instanceof BinaryExpr) {
                           parentBinaryExpr = (BinaryExpr) parent;
                        } else {
                           parent = parent.getParentNode();
                        }
                     }
                  }
                  if (parentBinaryExpr != null) {
                     lastExpression.accept(visitor, parentBinaryExpr);
                  }
               }
            }
         }
      } else {
         super.visit(n, ctx);
      }
   }

   private class FixOperand extends VoidVisitorAdapter<BinaryExpr> {

      private SymbolReference referredExpression;

      private Expression usageExpr = null;

      public FixOperand(FieldAccessExpr left) {
         referredExpression = left;
      }

      public FixOperand(NameExpr left) {
         referredExpression = left;
      }

      public boolean hasUsageInChild(SymbolReference n) {

         boolean hasUsageInChild = false;
         SymbolDefinition sd = n.getSymbolDefinition();

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

            hasUsageInChild = (srParent == null && srParentRef == null) || (srParent != null && srParentRef != null
                  && srParent.getSymbolDefinition() == srParentRef.getSymbolDefinition());

         }
         if (sd == null && referredExpression.getSymbolDefinition() == null) {
            if (n instanceof SymbolDataAware && referredExpression instanceof SymbolDataAware) {
               SymbolDataAware<?> sda = (SymbolDataAware<?>) n;
               SymbolDataAware<?> sda2 = (SymbolDataAware<?>) referredExpression;
               if (sda.getSymbolData() instanceof FieldSymbolData && sda2.getSymbolData() instanceof FieldSymbolData) {
                  FieldSymbolData fsd = (FieldSymbolData) sda.getSymbolData();
                  FieldSymbolData fsd2 = (FieldSymbolData) sda2.getSymbolData();
                  hasUsageInChild = (fsd.getField().equals(fsd2.getField()));
               }
            }
         }
         if (hasUsageInChild) {
            usageExpr = (Expression) n;
         }
         return hasUsageInChild;
      }

      @Override
      public void visit(NameExpr n, BinaryExpr ctx) {
         if (referredExpression != n) {
            if (!hasUsageInChild(n)) {
               super.visit(n, ctx);
            }
         }
      }

      @Override
      public void visit(FieldAccessExpr n, BinaryExpr ctx) {
         if (referredExpression != n) {
            if (!hasUsageInChild(n)) {
               super.visit(n, ctx);
            }
         }
      }

      private void update(Expression child, BinaryExpr n) {
         if (usageExpr != null) {

            boolean valid = false;
            Operator selectedOperator = Operator.and;
            Operator nullOperator = Operator.notEquals;
            Node ancestor = child.getCommonAncestor(n);
            if (ancestor instanceof BinaryExpr) {
               BinaryExpr be = (BinaryExpr) ancestor;
               if (be.getOperator().equals(Operator.and)) {
                  selectedOperator = Operator.or;
                  nullOperator = Operator.equals;
               }

               Expression node = child;
               while (!valid) {

                  if (n.isAncestorOf(node)) {

                     n.setOperator(selectedOperator);
                     valid = true;

                  } else if (node instanceof EnclosedExpr) {
                     node = ((EnclosedExpr) node).getInner();
                  } else {
                     try {
                        Expression name = (Expression) ASTManager.parse(NameExpr.class, referredExpression.toString());
                        Node parent = child.getParentNode();
                        Expression aux = new EnclosedExpr(new BinaryExpr(
                              new BinaryExpr(name, new NullLiteralExpr(), nullOperator), child, selectedOperator));
                        parent.replaceChildNode(child, aux);

                     } catch (ParseException e) {
                        throw new RuntimeException(e);
                     }
                     valid = true;
                  }

               }
            }

         }

      }

      @Override
      public void visit(BinaryExpr n, BinaryExpr ctx) {
         Expression left = n.getLeft();
         Expression right = n.getRight();
         if (left != null) {
            usageExpr = null;
            left.accept(this, ctx);
            update(left, ctx);
         }

         if (right != null) {
            usageExpr = null;
            right.accept(this, ctx);
            update(right, ctx);

         }
         usageExpr = null;
      }

   }
}
