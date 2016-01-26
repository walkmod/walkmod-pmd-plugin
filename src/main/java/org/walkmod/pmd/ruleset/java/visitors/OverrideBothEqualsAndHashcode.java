package org.walkmod.pmd.ruleset.java.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ParseException;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.body.VariableDeclaratorId;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.pmd.visitors.AbstactPMDRuleVisitor;

public class OverrideBothEqualsAndHashcode<T> extends AbstactPMDRuleVisitor<T> {

   public void visit(ClassOrInterfaceDeclaration coid, T ctx) {
      if (!coid.isInterface()) {
         List<BodyDeclaration> members = coid.getMembers();
         boolean hasEquals = false;
         boolean hasHashCode = false;
         if (members != null) {
            Iterator<BodyDeclaration> it = members.iterator();
            while (it.hasNext() && (!hasEquals || !hasHashCode)) {
               BodyDeclaration bd = it.next();
               if (bd instanceof MethodDeclaration) {
                  MethodDeclaration md = ((MethodDeclaration) bd);
                  List<Parameter> params = md.getParameters();
                  String name = md.getName();
                  if (name.equals("equals") && params != null && params.size() == 1) {
                     Parameter param = params.get(0);
                     Type type = param.getType();
                     String typeName = type.toString();
                     if (typeName.equals("Object") || typeName.equals("java.lang.Object")) {
                        hasEquals = true;
                     }
                  } else if (name.equals("hashCode") && (params == null || params.isEmpty())) {
                     hasHashCode = true;
                  }
               } else {
                  bd.accept(this, ctx);
               }
            }

            if (hasEquals && !hasHashCode) {
               MethodDeclaration md = new MethodDeclaration();
               md.setName("hashCode");
               md.setModifiers(ModifierSet.PUBLIC);
               md.setType(new ClassOrInterfaceType("int"));
               BlockStmt body = null;
               try {
                  body = (BlockStmt) ASTManager.parse(BlockStmt.class, "{ super.hashCode(); }");
               } catch (ParseException e) {
                  throw new RuntimeException("Error generating hashCode method");
               }
               md.setBody(body);
               members.add(md);
            } else if (!hasEquals && hasHashCode) {
               MethodDeclaration md = new MethodDeclaration();
               md.setName("equals");
               md.setModifiers(ModifierSet.PUBLIC);
               md.setType(new ClassOrInterfaceType("boolean"));
               List<Parameter> params = new LinkedList<Parameter>();
               Parameter param = new Parameter(new ClassOrInterfaceType("Object"), new VariableDeclaratorId("o"));
               params.add(param);
               md.setParameters(params);
               BlockStmt body = null;
               try {
                  body = (BlockStmt) ASTManager.parse(BlockStmt.class, "{ super.equals(o); }");
               } catch (ParseException e) {
                  throw new RuntimeException("Error generating equals method");
               }
               md.setBody(body);
               members.add(md);
            }

         }
      }
   }
}
