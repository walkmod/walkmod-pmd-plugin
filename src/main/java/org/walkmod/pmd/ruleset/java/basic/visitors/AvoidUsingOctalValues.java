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

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.IntegerLiteralExpr;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Modification
public class AvoidUsingOctalValues extends PMDRuleVisitor {

    @Override
    public void visit(IntegerLiteralExpr n, Node ctx) {
        super.visit(n, ctx);
        IntegerLiteralExpr aux = (IntegerLiteralExpr) ctx;
        String s = aux.getValue();

        if (s.startsWith("0") && s.length() > 1) {
            char[] digits = s.toCharArray();

            int value = 0;
            int power = 1;
            boolean isOctal = true;
            for (int i = digits.length - 1; i >= 0 && isOctal; i--) {
                isOctal = Character.isDigit(digits[i]);
                if (isOctal) {
                    Integer digit = Integer.parseInt(Character.toString(digits[i]));
                    isOctal = digit <= 7;
                    value = value + digit * power;
                    power = power * 8;
                }
            }
            if (isOctal) {
                aux.getParentNode().replaceChildNode(aux, new IntegerLiteralExpr(Integer.toString(value)));

            }
        }

    }

}
