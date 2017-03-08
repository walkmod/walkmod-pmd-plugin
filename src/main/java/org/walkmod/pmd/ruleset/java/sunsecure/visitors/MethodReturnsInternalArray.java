package org.walkmod.pmd.ruleset.java.sunsecure.visitors;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.FieldDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.ThisExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ReturnStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.type.ReferenceType;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.pmd.visitors.PMDRuleVisitor;
import org.walkmod.pmd.visitors.Modification;

@Modification
public class MethodReturnsInternalArray extends PMDRuleVisitor {

    @Override
    public void visit(FieldDeclaration n, Node node) {
        FieldDeclaration aux = (FieldDeclaration) node;
        int modifiers = aux.getModifiers();

        if (ModifierSet.isPrivate(modifiers)) {

            List<VariableDeclarator> vds = aux.getVariables();

            if (vds != null && vds.size() == 1) {
                VariableDeclarator vd = vds.get(0);
                String variable = vd.getId().getName();
                Node parentNode = node.getParentNode();
                if (isArray(aux.getType()) || vd.getId().getArrayCount() > 0) {
                    if (parentNode instanceof ClassOrInterfaceDeclaration) {

                        ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) parentNode;

                        MethodDeclaration getter = lookupGetter(clazz, variable);

                        if (getter != null && exposesField(getter, variable)) {
                            addCopyExpression(getter, variable);
                        }
                    }
                }

            }
        }
    }

    private void addCopyExpression(MethodDeclaration getter, String variable) {
        List<Statement> stmts = getter.getBody().getStmts();
        Statement stmt = stmts.get(0);
        ReturnStmt returnStmt = (ReturnStmt) stmt;
        try {

            ConditionalExpr expr = (ConditionalExpr) ASTManager.parse(ConditionalExpr.class,
                    variable + "==null?null:java.util.Arrays.copyOf(" + variable + "," + variable + ".length)");
            returnStmt.setExpr(expr);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isArray(Type type) {
        if (type instanceof ReferenceType) {
            ReferenceType rtype = (ReferenceType) type;
            return rtype.getArrayCount() > 0;
        }
        return false;
    }

    private MethodDeclaration lookupGetter(ClassOrInterfaceDeclaration clazz, String variable) {
        if (!clazz.isInterface()) {
            List<BodyDeclaration> members = clazz.getMembers();
            MethodDeclaration getter = null;

            Iterator<BodyDeclaration> it = members.iterator();
            String label = StringUtils.capitalize(variable);
            while (it.hasNext() && getter == null) {
                BodyDeclaration member = it.next();
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration md = (MethodDeclaration) member;
                    if (ModifierSet.isPublic(md.getModifiers())) {
                        String methodName = md.getName();
                        if (methodName.equals("get" + label)) {
                            getter = md;
                        }
                    }

                }
            }
            return getter;
        }
        return null;
    }

    private boolean exposesField(MethodDeclaration md, String variable) {
        BlockStmt block = md.getBody();
        List<Statement> stmts = block.getStmts();
        if (stmts != null && stmts.size() == 1 && (md.getParameters() == null || md.getParameters().isEmpty())) {
            Statement stmt = stmts.get(0);
            if (stmt instanceof ReturnStmt) {
                ReturnStmt returnStmt = (ReturnStmt) stmt;
                Expression expr = returnStmt.getExpr();

                if (expr instanceof NameExpr) {
                    NameExpr nameExpr = (NameExpr) expr;
                    return nameExpr.getName().equals(variable);
                } else if (expr instanceof FieldAccessExpr) {
                    FieldAccessExpr fae = (FieldAccessExpr) expr;
                    if (fae.getField().equals(variable)) {
                        Expression scope = fae.getScope();
                        if (scope == null || scope instanceof ThisExpr) {
                            return true;
                        }
                    }
                }
            }

        }

        return false;
    }

}
