package org.walkmod.pmd.ruleset.java.braces.visitors;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.DoStmt;
import org.walkmod.javalang.ast.stmt.ForStmt;
import org.walkmod.javalang.ast.stmt.ForeachStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.stmt.WhileStmt;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

/**
 * https://pmd.github.io/pmd-5.5.5/pmd-java/rules/java/braces.html
 */
@Modification
public class StatementsMustUseBraces extends PMDRuleVisitor {
    private final EnumSet<Flag> flags;

    enum Flag {
        For, If, IfElse, While

    }

    public StatementsMustUseBraces() {
        this.flags = EnumSet.allOf(Flag.class);
    }

    protected StatementsMustUseBraces(Flag flag) {
        this.flags = EnumSet.of(flag);
    }

    @Override
    public void visit(DoStmt n, Node arg) {
        if (flags.contains(Flag.While)) {
            if (!(n.getBody() instanceof BlockStmt)) {
                n.setBody(block(n.getBody()));
            }
        }
        super.visit(n, arg);
    }

    private BlockStmt block(final Statement statement) {
        return new BlockStmt(list(copy(statement)));
    }

    private <T extends Node> T copy(T n) {
        try {
            return (T) n.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private List<Statement> list(Statement statement) {
        final List<Statement> statements = new ArrayList<Statement>(1);
        statements.add(statement);
        return statements;
    }

    @Override
    public void visit(ForeachStmt n, Node arg) {
        if (flags.contains(Flag.For)) {
            if (!(n.getBody() instanceof BlockStmt)) {
                n.setBody(block(n.getBody()));
            }
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ForStmt n, Node arg) {
        if (flags.contains(Flag.For)) {
            if (!(n.getBody() instanceof BlockStmt)) {
                n.setBody(block(n.getBody()));
            }
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(IfStmt n, Node arg) {
        final Statement elseStmt = n.getElseStmt();
        if (elseStmt == null) {
            if (flags.contains(Flag.If)) {
                if (!(n.getThenStmt() instanceof BlockStmt)) {
                    n.setThenStmt(block(n.getThenStmt()));
                }
            }
        } else {
            if (flags.contains(Flag.IfElse)) {
                if (!(n.getThenStmt() instanceof BlockStmt)) {
                    n.setThenStmt(block(n.getThenStmt()));
                }
                if (!(elseStmt instanceof BlockStmt) && !(elseStmt instanceof IfStmt)) {
                    n.setElseStmt(block(elseStmt));
                }
            }
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(WhileStmt n, Node arg) {
        if (flags.contains(Flag.While)) {
            if (!(n.getBody() instanceof BlockStmt)) {
                n.setBody(block(n.getBody()));
            }
        }
        super.visit(n, arg);
    }
}
