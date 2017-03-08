package org.walkmod.pmd.ruleset.java.javabeans.visitors;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.FieldDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.pmd.visitors.Addition;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Addition
public class BeanMembersShouldSerialize extends PMDRuleVisitor {

    @Override
    public void visit(FieldDeclaration n, Node node) {
        FieldDeclaration aux = (FieldDeclaration) node;
        int modifiers = aux.getModifiers();

        if (!ModifierSet.isStatic(modifiers) && !ModifierSet.isTransient(modifiers)) {
            List<VariableDeclarator> vds = aux.getVariables();

            if (vds != null && vds.size() == 1) {
                VariableDeclarator vd = vds.get(0);

                String variable = vd.getId().getName();

                Node parentNode = node.getParentNode();

                if (parentNode instanceof ClassOrInterfaceDeclaration) {
                    ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) parentNode;

                    List<BodyDeclaration> members = clazz.getMembers();
                    boolean hasGetter = false;
                    boolean hasSetter = false;
                    Iterator<BodyDeclaration> it = members.iterator();
                    String label = StringUtils.capitalize(variable);
                    while (it.hasNext() && !(hasGetter && hasSetter)) {
                        BodyDeclaration member = it.next();
                        if (member instanceof MethodDeclaration) {
                            String methodName = ((MethodDeclaration) member).getName();
                            if (methodName.equals("get" + label)) {
                                hasGetter = true;
                            }
                            if (methodName.equals("set" + label)) {
                                hasSetter = true;
                            }
                        }
                    }
                    if(!hasGetter || !hasSetter){
                        modifiers = ModifierSet.addModifier(modifiers, ModifierSet.TRANSIENT);
                        aux.setModifiers(modifiers);
                    }

                }
            }
        }

    }

}
