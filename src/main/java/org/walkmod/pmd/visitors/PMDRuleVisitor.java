package org.walkmod.pmd.visitors;

import java.util.Iterator;
import java.util.List;

import org.walkmod.javalang.ast.BlockComment;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.ImportDeclaration;
import org.walkmod.javalang.ast.LineComment;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.PackageDeclaration;
import org.walkmod.javalang.ast.TypeParameter;
import org.walkmod.javalang.ast.body.AnnotationDeclaration;
import org.walkmod.javalang.ast.body.AnnotationMemberDeclaration;
import org.walkmod.javalang.ast.body.BodyDeclaration;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.ConstructorDeclaration;
import org.walkmod.javalang.ast.body.EmptyMemberDeclaration;
import org.walkmod.javalang.ast.body.EmptyTypeDeclaration;
import org.walkmod.javalang.ast.body.EnumConstantDeclaration;
import org.walkmod.javalang.ast.body.EnumDeclaration;
import org.walkmod.javalang.ast.body.FieldDeclaration;
import org.walkmod.javalang.ast.body.InitializerDeclaration;
import org.walkmod.javalang.ast.body.JavadocComment;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.MultiTypeParameter;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.body.TypeDeclaration;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.javalang.ast.body.VariableDeclaratorId;
import org.walkmod.javalang.ast.expr.AnnotationExpr;
import org.walkmod.javalang.ast.expr.ArrayAccessExpr;
import org.walkmod.javalang.ast.expr.ArrayCreationExpr;
import org.walkmod.javalang.ast.expr.ArrayInitializerExpr;
import org.walkmod.javalang.ast.expr.AssignExpr;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.BooleanLiteralExpr;
import org.walkmod.javalang.ast.expr.CastExpr;
import org.walkmod.javalang.ast.expr.CharLiteralExpr;
import org.walkmod.javalang.ast.expr.ClassExpr;
import org.walkmod.javalang.ast.expr.ConditionalExpr;
import org.walkmod.javalang.ast.expr.DoubleLiteralExpr;
import org.walkmod.javalang.ast.expr.EnclosedExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.FieldAccessExpr;
import org.walkmod.javalang.ast.expr.InstanceOfExpr;
import org.walkmod.javalang.ast.expr.IntegerLiteralExpr;
import org.walkmod.javalang.ast.expr.IntegerLiteralMinValueExpr;
import org.walkmod.javalang.ast.expr.LambdaExpr;
import org.walkmod.javalang.ast.expr.LongLiteralExpr;
import org.walkmod.javalang.ast.expr.LongLiteralMinValueExpr;
import org.walkmod.javalang.ast.expr.MarkerAnnotationExpr;
import org.walkmod.javalang.ast.expr.MemberValuePair;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.MethodReferenceExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.NormalAnnotationExpr;
import org.walkmod.javalang.ast.expr.NullLiteralExpr;
import org.walkmod.javalang.ast.expr.ObjectCreationExpr;
import org.walkmod.javalang.ast.expr.QualifiedNameExpr;
import org.walkmod.javalang.ast.expr.SingleMemberAnnotationExpr;
import org.walkmod.javalang.ast.expr.StringLiteralExpr;
import org.walkmod.javalang.ast.expr.SuperExpr;
import org.walkmod.javalang.ast.expr.ThisExpr;
import org.walkmod.javalang.ast.expr.TypeExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.AssertStmt;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.BreakStmt;
import org.walkmod.javalang.ast.stmt.CatchClause;
import org.walkmod.javalang.ast.stmt.ContinueStmt;
import org.walkmod.javalang.ast.stmt.DoStmt;
import org.walkmod.javalang.ast.stmt.EmptyStmt;
import org.walkmod.javalang.ast.stmt.ExplicitConstructorInvocationStmt;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.ast.stmt.ForStmt;
import org.walkmod.javalang.ast.stmt.ForeachStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.LabeledStmt;
import org.walkmod.javalang.ast.stmt.ReturnStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.stmt.SwitchEntryStmt;
import org.walkmod.javalang.ast.stmt.SwitchStmt;
import org.walkmod.javalang.ast.stmt.SynchronizedStmt;
import org.walkmod.javalang.ast.stmt.ThrowStmt;
import org.walkmod.javalang.ast.stmt.TryStmt;
import org.walkmod.javalang.ast.stmt.TypeDeclarationStmt;
import org.walkmod.javalang.ast.stmt.WhileStmt;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.ast.type.IntersectionType;
import org.walkmod.javalang.ast.type.PrimitiveType;
import org.walkmod.javalang.ast.type.ReferenceType;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.javalang.ast.type.VoidType;
import org.walkmod.javalang.ast.type.WildcardType;
import org.walkmod.javalang.visitors.VoidVisitor;

public class PMDRuleVisitor implements VoidVisitor<Node> {

    public void visit(AnnotationDeclaration n, Node arg) {

        AnnotationDeclaration aux = (AnnotationDeclaration) arg;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        if (n.getAnnotations() != null) {
            List<AnnotationExpr> anns = aux.getAnnotations();
            Iterator<AnnotationExpr> it = anns.iterator();
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        if (n.getMembers() != null) {
            List<BodyDeclaration> auxMembers = aux.getMembers();
            Iterator<BodyDeclaration> it = auxMembers.iterator();
            for (BodyDeclaration member : n.getMembers()) {
                member.accept(this, it.next());
            }
        }
    }

    public void visit(AnnotationMemberDeclaration n, Node arg) {
        AnnotationMemberDeclaration aux = (AnnotationMemberDeclaration) arg;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        if (n.getAnnotations() != null) {
            List<AnnotationExpr> anns = aux.getAnnotations();
            Iterator<AnnotationExpr> it = anns.iterator();
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        n.getType().accept(this, aux.getType());
        if (n.getDefaultValue() != null) {
            n.getDefaultValue().accept(this, aux.getDefaultValue());
        }
    }

    public void visit(ArrayAccessExpr n, Node arg) {
        ArrayAccessExpr aux = (ArrayAccessExpr) arg;
        n.getName().accept(this, aux.getName());
        n.getIndex().accept(this, aux.getIndex());
    }

    public void visit(ArrayCreationExpr n, Node arg) {
        ArrayCreationExpr aux = (ArrayCreationExpr) arg;
        n.getType().accept(this, aux.getType());

        if (n.getDimensions() != null) {
            List<Expression> anns = aux.getDimensions();
            Iterator<Expression> it = anns.iterator();

            for (Expression dim : n.getDimensions()) {
                dim.accept(this, it.next());
            }
        } else {
            ArrayInitializerExpr expr = n.getInitializer();
            if(expr != null) {
                expr.accept(this, aux.getInitializer());
            }
        }
        if (n.getArraysAnnotations() != null) {

            List<List<AnnotationExpr>> anns = aux.getArraysAnnotations();
            Iterator<List<AnnotationExpr>> it = anns.iterator();

            for (List<AnnotationExpr> annList : n.getArraysAnnotations()) {
                List<AnnotationExpr> auxAnnList = it.next();
                if (annList != null) {
                    Iterator<AnnotationExpr> auxIt = auxAnnList.iterator();
                    for (AnnotationExpr ae : annList) {
                        ae.accept(this, auxIt.next());
                    }
                }
            }
        }
    }

    public void visit(ArrayInitializerExpr n, Node arg) {
        ArrayInitializerExpr aux = (ArrayInitializerExpr) arg;
        if (n.getValues() != null) {
            List<Expression> values = aux.getValues();
            Iterator<Expression> itValues = values.iterator();
            for (Expression expr : n.getValues()) {
                expr.accept(this, itValues.next());
            }
        }
    }

    public void visit(AssertStmt n, Node arg) {
        AssertStmt aux = (AssertStmt) arg;
        n.getCheck().accept(this, aux.getCheck());
        if (n.getMessage() != null) {
            n.getMessage().accept(this, aux.getMessage());
        }
    }

    public void visit(AssignExpr n, Node arg) {
        AssignExpr aux = (AssignExpr) arg;
        n.getTarget().accept(this, aux.getTarget());
        n.getValue().accept(this, aux.getValue());
    }

    public void visit(BinaryExpr n, Node arg) {
        BinaryExpr aux = (BinaryExpr) arg;

        n.getLeft().accept(this, aux.getLeft());
        n.getRight().accept(this, aux.getRight());
    }

    public void visit(BlockComment n, Node arg) {
    }

    public void visit(BlockStmt n, Node arg) {
        BlockStmt aux = (BlockStmt) arg;

        if (n.getStmts() != null) {
            List<Statement> auxStmts = aux.getStmts();
            Iterator<Statement> it = auxStmts.iterator();

            for (Statement s : n.getStmts()) {
                s.accept(this, it.next());
            }
        }
    }

    public void visit(BooleanLiteralExpr n, Node arg) {
    }

    public void visit(BreakStmt n, Node arg) {
    }

    public void visit(CastExpr n, Node arg) {
        CastExpr aux = (CastExpr) arg;
        n.getType().accept(this, aux.getType());
        n.getExpr().accept(this, aux.getExpr());
    }

    public void visit(CatchClause n, Node arg) {
        CatchClause aux = (CatchClause) arg;

        n.getExcept().accept(this, aux.getExcept());
        n.getCatchBlock().accept(this, aux.getCatchBlock());
    }

    public void visit(CharLiteralExpr n, Node arg) {
    }

    public void visit(ClassExpr n, Node arg) {
        ClassExpr aux = (ClassExpr) arg;
        n.getType().accept(this, aux.getType());
    }

    public void visit(ClassOrInterfaceDeclaration n, Node arg) {
        ClassOrInterfaceDeclaration aux = (ClassOrInterfaceDeclaration) arg;

        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        if (n.getAnnotations() != null) {
            List<AnnotationExpr> auxAnn = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxAnn.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        if (n.getTypeParameters() != null) {

            List<TypeParameter> auxAnn = aux.getTypeParameters();
            Iterator<TypeParameter> it = auxAnn.iterator();

            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, it.next());
            }
        }
        if (n.getExtends() != null) {

            List<ClassOrInterfaceType> auxAnn = aux.getExtends();
            Iterator<ClassOrInterfaceType> it = auxAnn.iterator();

            for (ClassOrInterfaceType c : n.getExtends()) {
                c.accept(this, it.next());
            }
        }
        if (n.getImplements() != null) {

            List<ClassOrInterfaceType> auxAnn = aux.getImplements();
            Iterator<ClassOrInterfaceType> it = auxAnn.iterator();

            for (ClassOrInterfaceType c : n.getImplements()) {
                c.accept(this, it.next());
            }
        }
        if (n.getMembers() != null) {

            List<BodyDeclaration> auxAnn = aux.getMembers();
            Iterator<BodyDeclaration> it = auxAnn.iterator();

            for (BodyDeclaration member : n.getMembers()) {
                member.accept(this, it.next());
            }
        }
    }

    public void visit(ClassOrInterfaceType n, Node arg) {
        ClassOrInterfaceType aux = (ClassOrInterfaceType) arg;
        if (n.getScope() != null) {

            n.getScope().accept(this, aux.getScope());
        }
        if (n.getTypeArgs() != null) {
            List<Type> members = aux.getTypeArgs();
            Iterator<Type> it = members.iterator();

            for (Type t : n.getTypeArgs()) {
                t.accept(this, it.next());
            }
        }
        if (n.getAnnotations() != null) {

            List<AnnotationExpr> members = aux.getAnnotations();
            Iterator<AnnotationExpr> it = members.iterator();

            for (AnnotationExpr ae : n.getAnnotations()) {
                ae.accept(this, it.next());
            }
        }
    }

    public void visit(CompilationUnit n, Node arg) {
        CompilationUnit aux = n;
        if (arg instanceof CompilationUnit) {
            aux = (CompilationUnit) arg;
        }

        if (n.getPackage() != null) {
            n.getPackage().accept(this, aux.getPackage());
        }
        if (n.getImports() != null) {
            List<ImportDeclaration> imports = aux.getImports();
            Iterator<ImportDeclaration> it = imports.iterator();

            for (ImportDeclaration i : n.getImports()) {
                i.accept(this, it.next());
            }
        }
        if (n.getTypes() != null) {

            List<TypeDeclaration> imports = aux.getTypes();
            Iterator<TypeDeclaration> it = imports.iterator();

            for (TypeDeclaration typeDeclaration : n.getTypes()) {
                typeDeclaration.accept(this, it.next());
            }
        }
    }

    public void visit(ConditionalExpr n, Node arg) {
        ConditionalExpr aux = (ConditionalExpr) arg;

        n.getCondition().accept(this, aux.getCondition());
        n.getThenExpr().accept(this, aux.getThenExpr());
        n.getElseExpr().accept(this, aux.getElseExpr());
    }

    public void visit(ConstructorDeclaration n, Node arg) {

        ConstructorDeclaration aux = (ConstructorDeclaration) arg;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        if (n.getAnnotations() != null) {
            List<AnnotationExpr> anns = aux.getAnnotations();
            Iterator<AnnotationExpr> it = anns.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        if (n.getTypeParameters() != null) {
            List<TypeParameter> anns = aux.getTypeParameters();
            Iterator<TypeParameter> it = anns.iterator();

            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, it.next());
            }
        }
        if (n.getParameters() != null) {
            List<Parameter> anns = aux.getParameters();
            Iterator<Parameter> it = anns.iterator();

            for (Parameter p : n.getParameters()) {
                p.accept(this, it.next());
            }
        }
        if (n.getThrows() != null) {

            List<ClassOrInterfaceType> anns = aux.getThrows();
            Iterator<ClassOrInterfaceType> it = anns.iterator();

            for (ClassOrInterfaceType name : n.getThrows()) {
                name.accept(this, it.next());
            }
        }
        n.getBlock().accept(this, aux.getBlock());
    }

    public void visit(ContinueStmt n, Node arg) {
    }

    public void visit(DoStmt n, Node arg) {
        DoStmt aux = (DoStmt) arg;

        n.getBody().accept(this, aux.getBody());
        n.getCondition().accept(this, aux.getCondition());
    }

    public void visit(DoubleLiteralExpr n, Node arg) {
    }

    public void visit(EmptyMemberDeclaration n, Node arg) {
        EmptyMemberDeclaration aux = (EmptyMemberDeclaration) arg;

        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
    }

    public void visit(EmptyStmt n, Node arg) {
    }

    public void visit(EmptyTypeDeclaration n, Node arg) {
        EmptyTypeDeclaration aux = (EmptyTypeDeclaration) arg;
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
    }

    public void visit(EnclosedExpr n, Node arg) {
        EnclosedExpr aux = (EnclosedExpr) arg;
        n.getInner().accept(this, aux.getInner());
    }

    public void visit(EnumConstantDeclaration n, Node arg) {
        EnumConstantDeclaration aux = (EnumConstantDeclaration) arg;

        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        if (n.getAnnotations() != null) {
            List<AnnotationExpr> auxAnn = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxAnn.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        if (n.getArgs() != null) {

            List<Expression> auxArgs = aux.getArgs();
            Iterator<Expression> it = auxArgs.iterator();

            for (Expression e : n.getArgs()) {
                e.accept(this, it.next());
            }
        }
        if (n.getClassBody() != null) {

            List<BodyDeclaration> auxArgs = aux.getClassBody();
            Iterator<BodyDeclaration> it = auxArgs.iterator();

            for (BodyDeclaration member : n.getClassBody()) {
                member.accept(this, it.next());
            }
        }
    }

    public void visit(EnumDeclaration n, Node arg) {

        EnumDeclaration aux = (EnumDeclaration) arg;

        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        if (n.getAnnotations() != null) {
            List<AnnotationExpr> auxAnn = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxAnn.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        if (n.getImplements() != null) {

            List<ClassOrInterfaceType> auxAnn = aux.getImplements();
            Iterator<ClassOrInterfaceType> it = auxAnn.iterator();

            for (ClassOrInterfaceType c : n.getImplements()) {
                c.accept(this, it.next());
            }
        }
        if (n.getEntries() != null) {

            List<EnumConstantDeclaration> auxAnn = aux.getEntries();
            Iterator<EnumConstantDeclaration> it = auxAnn.iterator();

            for (EnumConstantDeclaration e : n.getEntries()) {
                e.accept(this, it.next());
            }
        }
        if (n.getMembers() != null) {

            List<BodyDeclaration> auxAnn = aux.getMembers();
            Iterator<BodyDeclaration> it = auxAnn.iterator();

            for (BodyDeclaration member : n.getMembers()) {
                member.accept(this, it.next());
            }
        }
    }

    public void visit(ExplicitConstructorInvocationStmt n, Node arg) {

        ExplicitConstructorInvocationStmt aux = (ExplicitConstructorInvocationStmt) arg;

        if (!n.isThis()) {
            if (n.getExpr() != null) {
                n.getExpr().accept(this, aux.getExpr());
            }
        }
        if (n.getTypeArgs() != null) {

            List<Type> auxTypes = aux.getTypeArgs();
            Iterator<Type> it = auxTypes.iterator();

            for (Type t : n.getTypeArgs()) {
                t.accept(this, it.next());
            }
        }
        if (n.getArgs() != null) {

            List<Expression> args = aux.getArgs();
            Iterator<Expression> it = args.iterator();

            for (Expression e : n.getArgs()) {
                e.accept(this, it.next());
            }
        }
    }

    public void visit(ExpressionStmt n, Node arg) {
        ExpressionStmt aux = (ExpressionStmt) arg;

        n.getExpression().accept(this, aux.getExpression());
    }

    public void visit(FieldAccessExpr n, Node arg) {
        FieldAccessExpr aux = (FieldAccessExpr) arg;

        n.getScope().accept(this, aux.getScope());
    }

    public void visit(FieldDeclaration n, Node arg) {

        FieldDeclaration aux = (FieldDeclaration) arg;

        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        if (n.getAnnotations() != null) {

            List<AnnotationExpr> ann = aux.getAnnotations();
            Iterator<AnnotationExpr> it = ann.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        n.getType().accept(this, aux.getType());
        List<VariableDeclarator> auxVars = aux.getVariables();
        Iterator<VariableDeclarator> it = auxVars.iterator();

        for (VariableDeclarator var : n.getVariables()) {
            var.accept(this, it.next());
        }
    }

    public void visit(ForeachStmt n, Node arg) {

        ForeachStmt aux = (ForeachStmt) arg;

        n.getVariable().accept(this, aux.getVariable());
        n.getIterable().accept(this, aux.getIterable());
        n.getBody().accept(this, aux.getBody());
    }

    public void visit(ForStmt n, Node arg) {
        ForStmt aux = (ForStmt) arg;
        if (n.getInit() != null) {
            List<Expression> exprs = aux.getInit();
            Iterator<Expression> it = exprs.iterator();

            for (Expression e : n.getInit()) {
                e.accept(this, it.next());
            }
        }
        if (n.getCompare() != null) {
            n.getCompare().accept(this, aux.getCompare());
        }
        if (n.getUpdate() != null) {

            List<Expression> exprs = aux.getUpdate();
            Iterator<Expression> it = exprs.iterator();

            for (Expression e : n.getUpdate()) {
                e.accept(this, it.next());
            }
        }
        n.getBody().accept(this, aux.getBody());
    }

    public void visit(IfStmt n, Node arg) {

        IfStmt aux = (IfStmt) arg;

        n.getCondition().accept(this, aux.getCondition());
        n.getThenStmt().accept(this, aux.getThenStmt());
        if (n.getElseStmt() != null) {
            n.getElseStmt().accept(this, aux.getElseStmt());
        }
    }

    public void visit(ImportDeclaration n, Node arg) {
        ImportDeclaration aux = (ImportDeclaration) arg;

        n.getName().accept(this, aux.getName());
    }

    public void visit(InitializerDeclaration n, Node arg) {
        InitializerDeclaration aux = (InitializerDeclaration) arg;

        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        n.getBlock().accept(this, aux.getBlock());
    }

    public void visit(InstanceOfExpr n, Node arg) {
        InstanceOfExpr aux = (InstanceOfExpr) arg;

        n.getExpr().accept(this, aux.getExpr());
        n.getType().accept(this, aux.getType());
    }

    public void visit(IntegerLiteralExpr n, Node arg) {
    }

    public void visit(IntegerLiteralMinValueExpr n, Node arg) {
    }

    public void visit(JavadocComment n, Node arg) {
    }

    public void visit(LabeledStmt n, Node arg) {
        LabeledStmt aux = (LabeledStmt) arg;

        n.getStmt().accept(this, aux.getStmt());
    }

    public void visit(LineComment n, Node arg) {
    }

    public void visit(LongLiteralExpr n, Node arg) {
    }

    public void visit(LongLiteralMinValueExpr n, Node arg) {
    }

    public void visit(MarkerAnnotationExpr n, Node arg) {

        MarkerAnnotationExpr aux = (MarkerAnnotationExpr) arg;

        n.getName().accept(this, aux.getName());
    }

    public void visit(MemberValuePair n, Node arg) {

        MemberValuePair aux = (MemberValuePair) arg;

        n.getValue().accept(this, aux.getValue());
    }

    public void visit(MethodCallExpr n, Node arg) {

        MethodCallExpr aux = (MethodCallExpr) arg;

        if (n.getScope() != null) {
            n.getScope().accept(this, aux.getScope());
        }
        if (n.getTypeArgs() != null) {

            List<Type> types = aux.getTypeArgs();
            Iterator<Type> it = types.iterator();

            for (Type t : n.getTypeArgs()) {
                t.accept(this, it.next());
            }
        }
        if (n.getArgs() != null) {

            List<Expression> exprs = aux.getArgs();
            Iterator<Expression> it = exprs.iterator();

            for (Expression e : n.getArgs()) {
                e.accept(this, it.next());
            }
        }
    }

    public void visit(MethodDeclaration n, Node arg) {

        MethodDeclaration aux = (MethodDeclaration) arg;

        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, aux.getJavaDoc());
        }
        if (n.getAnnotations() != null) {

            List<AnnotationExpr> auxAnn = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxAnn.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        if (n.getTypeParameters() != null) {

            List<TypeParameter> auxtypes = aux.getTypeParameters();
            Iterator<TypeParameter> it = auxtypes.iterator();

            for (TypeParameter t : n.getTypeParameters()) {
                t.accept(this, it.next());
            }
        }
        n.getType().accept(this, aux.getType());

        if (n.getParameters() != null) {

            List<Parameter> auxP = aux.getParameters();
            Iterator<Parameter> it = auxP.iterator();

            for (Parameter p : n.getParameters()) {
                p.accept(this, it.next());
            }
        }
        if (n.getThrows() != null) {

            List<ClassOrInterfaceType> throwsList = aux.getThrows();
            Iterator<ClassOrInterfaceType> it = throwsList.iterator();

            for (ClassOrInterfaceType name : n.getThrows()) {
                name.accept(this, it.next());
            }
        }
        if (n.getBody() != null) {
            n.getBody().accept(this, aux.getBody());
        }
    }

    public void visit(NameExpr n, Node arg) {
    }

    public void visit(NormalAnnotationExpr n, Node arg) {

        NormalAnnotationExpr aux = (NormalAnnotationExpr) arg;

        n.getName().accept(this, aux.getName());
        if (n.getPairs() != null) {

            List<MemberValuePair> list = aux.getPairs();
            Iterator<MemberValuePair> it = list.iterator();

            for (MemberValuePair m : n.getPairs()) {
                m.accept(this, it.next());
            }
        }
    }

    public void visit(NullLiteralExpr n, Node arg) {
    }

    public void visit(ObjectCreationExpr n, Node arg) {

        ObjectCreationExpr aux = (ObjectCreationExpr) arg;

        if (n.getScope() != null) {
            n.getScope().accept(this, aux.getScope());
        }
        if (n.getTypeArgs() != null) {

            List<Type> auxTypes = aux.getTypeArgs();
            Iterator<Type> it = auxTypes.iterator();

            for (Type t : n.getTypeArgs()) {
                t.accept(this, it.next());
            }
        }
        n.getType().accept(this, aux.getType());

        if (n.getArgs() != null) {

            List<Expression> args = aux.getArgs();
            Iterator<Expression> it = args.iterator();

            for (Expression e : n.getArgs()) {
                e.accept(this, it.next());
            }
        }
        if (n.getAnonymousClassBody() != null) {

            List<BodyDeclaration> auxMembers = aux.getAnonymousClassBody();
            Iterator<BodyDeclaration> it = auxMembers.iterator();

            for (BodyDeclaration member : n.getAnonymousClassBody()) {
                member.accept(this, it.next());
            }
        }
    }

    public void visit(PackageDeclaration n, Node arg) {

        PackageDeclaration aux = (PackageDeclaration) arg;

        if (n.getAnnotations() != null) {

            List<AnnotationExpr> auxAnn = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxAnn.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        n.getName().accept(this, aux.getName());
    }

    public void visit(Parameter n, Node arg) {
        Parameter aux = (Parameter) arg;

        if (n.getAnnotations() != null) {

            List<AnnotationExpr> auxAnn = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxAnn.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        if (n.getType() != null) {
            n.getType().accept(this, aux.getType());
        }
        n.getId().accept(this, aux.getId());
    }

    public void visit(PrimitiveType n, Node arg) {
        PrimitiveType aux = (PrimitiveType) arg;

        if (n.getAnnotations() != null) {

            List<AnnotationExpr> ann = aux.getAnnotations();
            Iterator<AnnotationExpr> it = ann.iterator();

            for (AnnotationExpr ae : n.getAnnotations()) {
                ae.accept(this, it.next());
            }
        }
    }

    public void visit(QualifiedNameExpr n, Node arg) {
        QualifiedNameExpr aux = (QualifiedNameExpr) arg;

        n.getQualifier().accept(this, aux.getQualifier());
    }

    public void visit(ReferenceType n, Node arg) {

        ReferenceType aux = (ReferenceType) arg;

        n.getType().accept(this, aux.getType());

        if (n.getAnnotations() != null) {

            List<AnnotationExpr> anns = aux.getAnnotations();
            Iterator<AnnotationExpr> it = anns.iterator();

            for (AnnotationExpr ae : n.getAnnotations()) {
                ae.accept(this, it.next());
            }
        }
        if (n.getArraysAnnotations() != null) {

            List<List<AnnotationExpr>> auxAnn = aux.getArraysAnnotations();
            Iterator<List<AnnotationExpr>> itList = auxAnn.iterator();

            for (List<AnnotationExpr> annList : n.getArraysAnnotations()) {

                List<AnnotationExpr> auxAnnList = itList.next();

                if (annList != null) {
                    Iterator<AnnotationExpr> it2 = auxAnnList.iterator();

                    for (AnnotationExpr ae : annList) {
                        ae.accept(this, it2.next());
                    }
                }
            }
        }
    }

    public void visit(ReturnStmt n, Node arg) {

        ReturnStmt aux = (ReturnStmt) arg;

        if (n.getExpr() != null) {
            n.getExpr().accept(this, aux.getExpr());
        }
    }

    public void visit(SingleMemberAnnotationExpr n, Node arg) {

        SingleMemberAnnotationExpr aux = (SingleMemberAnnotationExpr) arg;

        n.getName().accept(this, aux.getName());
        n.getMemberValue().accept(this, aux.getMemberValue());
    }

    public void visit(StringLiteralExpr n, Node arg) {
    }

    public void visit(SuperExpr n, Node arg) {

        SuperExpr aux = (SuperExpr) arg;

        if (n.getClassExpr() != null) {
            n.getClassExpr().accept(this, aux.getClassExpr());
        }
    }

    public void visit(SwitchEntryStmt n, Node arg) {

        SwitchEntryStmt aux = (SwitchEntryStmt) arg;

        if (n.getLabel() != null) {
            n.getLabel().accept(this, aux.getLabel());
        }
        if (n.getStmts() != null) {
            List<Statement> auxStmts = aux.getStmts();
            Iterator<Statement> it = auxStmts.iterator();

            for (Statement s : n.getStmts()) {
                s.accept(this, it.next());
            }
        }
    }

    public void visit(SwitchStmt n, Node arg) {
        SwitchStmt aux = (SwitchStmt) arg;

        n.getSelector().accept(this, aux.getSelector());
        if (n.getEntries() != null) {

            List<SwitchEntryStmt> list = aux.getEntries();
            Iterator<SwitchEntryStmt> it = list.iterator();

            for (SwitchEntryStmt e : n.getEntries()) {
                e.accept(this, it.next());
            }
        }
    }

    public void visit(SynchronizedStmt n, Node arg) {

        SynchronizedStmt aux = (SynchronizedStmt) arg;

        n.getExpr().accept(this, aux.getExpr());
        n.getBlock().accept(this, aux.getBlock());
    }

    public void visit(ThisExpr n, Node arg) {

        ThisExpr aux = (ThisExpr) arg;

        if (n.getClassExpr() != null) {
            n.getClassExpr().accept(this, aux.getClassExpr());
        }
    }

    public void visit(ThrowStmt n, Node arg) {

        ThrowStmt aux = (ThrowStmt) arg;

        n.getExpr().accept(this, aux.getExpr());
    }

    public void visit(TryStmt n, Node arg) {

        TryStmt aux = (TryStmt) arg;

        List<VariableDeclarationExpr> resources = n.getResources();
        if (resources != null) {
            List<VariableDeclarationExpr> auxRes = aux.getResources();
            if (auxRes != null) {
                Iterator<VariableDeclarationExpr> it = auxRes.iterator();
                for (VariableDeclarationExpr resource : resources) {
                    resource.accept(this, it.next());
                }
            }
        }
        n.getTryBlock().accept(this, aux.getTryBlock());
        if (n.getCatchs() != null) {

            List<CatchClause> auxClauses = aux.getCatchs();
            Iterator<CatchClause> it = auxClauses.iterator();

            for (CatchClause c : n.getCatchs()) {
                c.accept(this, it.next());
            }
        }
        if (n.getFinallyBlock() != null) {
            n.getFinallyBlock().accept(this, aux.getFinallyBlock());
        }
    }

    public void visit(TypeDeclarationStmt n, Node arg) {
        TypeDeclarationStmt aux = (TypeDeclarationStmt) arg;
        n.getTypeDeclaration().accept(this, aux.getTypeDeclaration());
    }

    public void visit(TypeParameter n, Node arg) {
        TypeParameter aux = (TypeParameter) arg;
        if (n.getTypeBound() != null) {

            List<ClassOrInterfaceType> auxTypes = aux.getTypeBound();
            Iterator<ClassOrInterfaceType> it = auxTypes.iterator();

            for (ClassOrInterfaceType c : n.getTypeBound()) {
                c.accept(this, it.next());
            }
        }
        if (n.getAnnotations() != null) {

            List<AnnotationExpr> auxTypes = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxTypes.iterator();

            for (AnnotationExpr ann : n.getAnnotations()) {
                ann.accept(this, it.next());
            }
        }
    }

    public void visit(UnaryExpr n, Node arg) {
        UnaryExpr aux = (UnaryExpr) arg;
        n.getExpr().accept(this, aux.getExpr());
    }

    public void visit(VariableDeclarationExpr n, Node arg) {

        VariableDeclarationExpr aux = (VariableDeclarationExpr) arg;

        if (n.getAnnotations() != null) {
            List<AnnotationExpr> auxTypes = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxTypes.iterator();

            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        n.getType().accept(this, aux.getType());

        List<VariableDeclarator> auxVars = aux.getVars();
        Iterator<VariableDeclarator> it = auxVars.iterator();

        for (VariableDeclarator v : n.getVars()) {
            v.accept(this, it.next());
        }
    }

    public void visit(VariableDeclarator n, Node arg) {

        VariableDeclarator aux = (VariableDeclarator) arg;

        n.getId().accept(this, aux.getId());
        if (n.getInit() != null) {
            n.getInit().accept(this, aux.getInit());
        }
    }

    public void visit(VariableDeclaratorId n, Node arg) {
    }

    public void visit(VoidType n, Node arg) {
        VoidType aux = (VoidType) arg;

        if (n.getAnnotations() != null) {
            List<AnnotationExpr> auxTypes = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxTypes.iterator();

            for (AnnotationExpr ae : n.getAnnotations()) {
                ae.accept(this, it.next());
            }
        }
    }

    public void visit(WhileStmt n, Node arg) {
        WhileStmt aux = (WhileStmt) arg;

        n.getCondition().accept(this, aux.getCondition());
        n.getBody().accept(this, aux.getBody());
    }

    public void visit(WildcardType n, Node arg) {

        WildcardType aux = (WildcardType) arg;

        if (n.getExtends() != null) {
            n.getExtends().accept(this, aux.getExtends());
        }
        if (n.getSuper() != null) {
            n.getSuper().accept(this, aux.getSuper());
        }
        if (n.getAnnotations() != null) {
            List<AnnotationExpr> auxTypes = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxTypes.iterator();

            for (AnnotationExpr ae : n.getAnnotations()) {
                ae.accept(this, it.next());
            }
        }
    }

    @Override
    public void visit(final MultiTypeParameter n, final Node arg) {

        MultiTypeParameter aux = (MultiTypeParameter) arg;

        if (n.getAnnotations() != null) {
            List<AnnotationExpr> auxTypes = aux.getAnnotations();
            Iterator<AnnotationExpr> it = auxTypes.iterator();

            for (final AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, it.next());
            }
        }
        List<Type> types = aux.getTypes();
        Iterator<Type> it = types.iterator();

        for (final Type type : n.getTypes()) {
            type.accept(this, it.next());
        }
        n.getId().accept(this, aux.getId());
    }

    @Override
    public void visit(LambdaExpr n, final Node arg) {
        LambdaExpr aux = (LambdaExpr) arg;

        if (n.getParameters() != null) {
            List<Parameter> params = aux.getParameters();
            Iterator<Parameter> it = params.iterator();

            for (final Parameter a : n.getParameters()) {
                a.accept(this, it.next());
            }
        }
        if (n.getBody() != null) {
            n.getBody().accept(this, aux.getBody());
        }
    }

    public void visit(MethodReferenceExpr n, final Node arg) {
        MethodReferenceExpr aux = (MethodReferenceExpr) arg;

        if (n.getTypeParameters() != null) {
            List<TypeParameter> typeParams = aux.getTypeParameters();
            Iterator<TypeParameter> it = typeParams.iterator();

            for (final TypeParameter a : n.getTypeParameters()) {
                a.accept(this, it.next());
            }
        }
        if (n.getScope() != null) {
            n.getScope().accept(this, aux.getScope());
        }
    }

    public void visit(TypeExpr n, final Node arg) {

        TypeExpr aux = (TypeExpr) arg;

        if (n.getType() != null) {
            n.getType().accept(this, aux.getType());
        }
    }

    public void visit(IntersectionType n, Node arg) {

        IntersectionType aux = (IntersectionType) arg;

        if (n.getAnnotations() != null) {
            List<AnnotationExpr> typeParams = aux.getAnnotations();
            Iterator<AnnotationExpr> it = typeParams.iterator();

            for (final AnnotationExpr ae : n.getAnnotations()) {
                ae.accept(this, it.next());
            }
        }
        if (n.getBounds() != null) {
            List<ReferenceType> typeParams = aux.getBounds();
            Iterator<ReferenceType> it = typeParams.iterator();
            for (final ReferenceType t : n.getBounds()) {
                t.accept(this, it.next());
            }
        }
    }
}
