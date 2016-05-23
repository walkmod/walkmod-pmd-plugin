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
package org.walkmod.pmd.ruleset.java.comments.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.body.JavadocComment;
import org.walkmod.pmd.visitors.AbstractPMDRuleVisitor;

public class CommentsSize<T> extends AbstractPMDRuleVisitor<T> {

   private String violationSuppressRegex; //Suppress violations with messages matching a regular expression

   private int maxLines = 6; //Maximum lines
   private int maxLineLength = 80;// Maximum line length

   @Override
   public void visit(JavadocComment n, T ctx) {

      String content = n.getContent();
      boolean valid = true;
      if (content != null) {
         if (violationSuppressRegex != null) {
            valid = content.matches(violationSuppressRegex);
         }
         if (valid) {
            String[] lines = content.split("\\n");
            List<String> newLines = new LinkedList<String>();
            boolean requiredRewrite = false;
            for (int i = 0; i < lines.length; i++) {
               String line = lines[i].trim();
               if (line.length() > maxLineLength) {
                  char[] letters = line.toCharArray();
                  int accum = 0;
                  int lastWhite = -1;
                  int begin = 0;
                  for (int j = 0; j < letters.length; j++) {
                     if (letters[i] == ' ') {
                        lastWhite = i;
                     }
                     if (accum > maxLineLength) {
                        if (lastWhite != -1) {
                           newLines.add(line.substring(begin, lastWhite - 1));
                           begin = lastWhite + 1;
                           requiredRewrite = true;
                        }
                     }
                  }

               } else {
                  newLines.add(lines[i]);
               }
            }

            String finalContent = "";

            if (newLines.size() > maxLines) {

               Iterator<String> it = newLines.iterator();
               while (it.hasNext()) {
                  String line = it.next();
                  if (line.length() > 0 && !line.trim().equals("*")) {
                     finalContent += line;
                     if (it.hasNext()) {
                        finalContent += "\n";
                     }
                  }
               }

               if (finalContent.length() == 0) {
                  n.remove();
               } else {
                  n.getParentNode().replaceChildNode(n, new JavadocComment(finalContent));
               }
            } else if (requiredRewrite) {
               Iterator<String> it = newLines.iterator();
               while (it.hasNext()) {
                  finalContent += it.next();
                  if (it.hasNext()) {
                     finalContent += "\n";
                  }
               }
               n.getParentNode().replaceChildNode(n, new JavadocComment(finalContent));
            }
         }
      }
   }

   public String getViolationSuppressRegex() {
      return violationSuppressRegex;
   }

   public void setViolationSuppressRegex(String violationSuppressRegex) {
      this.violationSuppressRegex = violationSuppressRegex;
   }

   public int getMaxLines() {
      return maxLines;
   }

   public void setMaxLines(int maxLines) {
      this.maxLines = maxLines;
   }

   public int getMaxLineLength() {
      return maxLineLength;
   }

   public void setMaxLineLength(int maxLineLength) {
      this.maxLineLength = maxLineLength;
   }

}
