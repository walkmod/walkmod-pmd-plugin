package org.walkmod.pmd.ruleset.java.basic.visitors;

import org.walkmod.javalang.ast.expr.IntegerLiteralExpr;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

public class AvoidUsingOctalValues<T> extends AbstractPMDRuleVisitor<T> {

   @Override
   public void visit(IntegerLiteralExpr n, T ctx) {

      String s = n.getValue();

      if (s.startsWith("0") && s.length() > 1) {
         char[] digits = s.toCharArray();

         int value = 0;
         int power = 1;
         for (int i = digits.length - 1; i >= 0; i--) {
            Integer digit = Integer.parseUnsignedInt(Character.toString(digits[i]));
            value = value + digit * power;
            power = power * 8;
         }

         n.setValue(Integer.toString(value));
      }

      super.visit(n, null);
   }
}
