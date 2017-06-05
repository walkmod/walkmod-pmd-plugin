/*
 * Copyright (C) 2016 Raquel Pau.
 *
 * Walkmod is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Walkmod is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Walkmod. If not, see <http://www.gnu.org/licenses/>.
 */
package org.walkmod.pmd.ruleset.java.basic.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ParseException;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.body.VariableDeclaratorId;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.pmd.visitors.Addition;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Addition
public class OverrideBothEqualsAndHashcode extends PMDRuleVisitor {

    public void visit(ClassOrInterfaceDeclaration coid, Node ctx) {

        ClassOrInterfaceDeclaration result = (ClassOrInterfaceDeclaration) ctx;
        if (!coid.isInterface()) {

            List<BodyDeclaration> members = coid.getMembers();
            List<BodyDeclaration> resultMembers = result.getMembers();

            boolean hasEquals = false;
            boolean hasHashCode = false;
            if (members != null) {
                Iterator<BodyDeclaration> it = members.iterator();
                Iterator<BodyDeclaration> itAux = resultMembers.iterator();
                while (it.hasNext() && (!hasEquals || !hasHashCode)) {
                    BodyDeclaration bd = it.next();
                    BodyDeclaration bdAux = itAux.next();
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
                        bd.accept(this, bdAux);
                    }
                }

                if (hasEquals && !hasHashCode) {
                    MethodDeclaration md = new MethodDeclaration();
                    md.setName("hashCode");
                    md.setModifiers(ModifierSet.PUBLIC);
                    md.setType(new ClassOrInterfaceType("int"));
                    BlockStmt body = null;
                    try {
                        body = (BlockStmt) ASTManager.parse(BlockStmt.class, "{ return super.hashCode(); }");
                    } catch (ParseException e) {
                        throw new RuntimeException("Error generating hashCode method");
                    }
                    md.setBody(body);

                    resultMembers.add(md);
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
                        body = (BlockStmt) ASTManager.parse(BlockStmt.class, "{ return super.equals(o); }");
                    } catch (ParseException e) {
                        throw new RuntimeException("Error generating equals method");
                    }
                    md.setBody(body);
                    resultMembers.add(md);
                }
            }
        }
    }
}
