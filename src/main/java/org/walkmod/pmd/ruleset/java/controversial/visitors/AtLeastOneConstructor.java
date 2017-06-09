package org.walkmod.pmd.ruleset.java.controversial.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.ConstructorDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.pmd.visitors.Addition;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Addition
public class AtLeastOneConstructor extends PMDRuleVisitor {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Node ctx) {
        super.visit(n, ctx);
        ClassOrInterfaceDeclaration aux = (ClassOrInterfaceDeclaration) ctx;
        if (!n.isInterface()) {
            List<BodyDeclaration> members = aux.getMembers();

            if (members != null) {
                Iterator<BodyDeclaration> it = members.iterator();
                boolean hasConstructor = false;
                while (it.hasNext() && !hasConstructor) {
                    BodyDeclaration next = it.next();

                    hasConstructor = (next instanceof ConstructorDeclaration);
                }
                if (!hasConstructor) {
                    ConstructorDeclaration cd = new ConstructorDeclaration(ModifierSet.PUBLIC, n.getName());
                    cd.setBlock(new BlockStmt(new LinkedList<Statement>()));
                    members.add(0, cd);
                }
            } else {
                members = new LinkedList<BodyDeclaration>();
                ConstructorDeclaration cd = new ConstructorDeclaration(ModifierSet.PUBLIC, n.getName());
                cd.setBlock(new BlockStmt(new LinkedList<Statement>()));
                members.add(0, cd);
                aux.setMembers(members);
            }
        }
    }
}
