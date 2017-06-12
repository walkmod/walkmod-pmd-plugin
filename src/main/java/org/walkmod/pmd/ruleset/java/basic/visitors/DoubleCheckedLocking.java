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

import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.expr.BinaryExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.NullLiteralExpr;
import org.walkmod.javalang.ast.expr.QualifiedNameExpr;
import org.walkmod.javalang.ast.expr.ThisExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.ReturnStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.stmt.SynchronizedStmt;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@Modification
public class DoubleCheckedLocking extends PMDRuleVisitor {

    private class CheckSameObjectField<T> extends VoidVisitorAdapter<T> {

        private String name = null;

        public void visit(QualifiedNameExpr n, T ctx) {
            Expression qualifier = n.getQualifier();
            if (qualifier instanceof ThisExpr) {
                name = n.getName();
            }
        }

        public void visit(NameExpr n, T ctx) {
            name = n.getName();
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public void visit(MethodDeclaration md, Node ctx) {

        md = (MethodDeclaration) ctx;
        BlockStmt block = md.getBody();
        if (block != null) {
            List<Statement> stmts = block.getStmts();
            if (stmts != null) {
                if (stmts.size() == 2) {
                    Statement firstStmt = stmts.get(0);
                    if (firstStmt instanceof IfStmt) {
                        Statement lastStmt = stmts.get(1);
                        if (lastStmt instanceof ReturnStmt) {
                            IfStmt ifStmt = (IfStmt) firstStmt;
                            Expression condition = ifStmt.getCondition();
                            if (condition instanceof BinaryExpr) {
                                BinaryExpr be = (BinaryExpr) condition;
                                if (be.getOperator().equals(BinaryExpr.Operator.equals)) {
                                    if (be.getRight() instanceof NullLiteralExpr) {
                                        Expression leftExpr = be.getLeft();
                                        ReturnStmt returnStmt = (ReturnStmt) lastStmt;
                                        Expression returnedExpr = returnStmt.getExpr();

                                        CheckSameObjectField<Node> visitor1 = new CheckSameObjectField<Node>();
                                        leftExpr.accept(visitor1, ctx);
                                        String variableNameOfCheckingNull = visitor1.getName();

                                        visitor1 = new CheckSameObjectField<Node>();
                                        returnedExpr.accept(visitor1, ctx);
                                        String variableNameOfReturnExpression = visitor1.getName();

                                        if (variableNameOfCheckingNull != null
                                                && variableNameOfReturnExpression != null
                                                && variableNameOfCheckingNull.equals(variableNameOfReturnExpression)) {

                                            Statement thenStmt = ifStmt.getThenStmt();
                                            if (thenStmt instanceof BlockStmt) {
                                                BlockStmt thenBlock = (BlockStmt) thenStmt;
                                                List<Statement> blockStmts = thenBlock.getStmts();
                                                if (blockStmts != null && blockStmts.size() == 1) {

                                                    SynchronizedStmt synchStmt = new SynchronizedStmt();
                                                    BlockStmt innerBlock = new BlockStmt();
                                                    List<Statement> innerStmts = new LinkedList<Statement>();
                                                    BlockStmt newBlock = new BlockStmt();
                                                    List<Statement> newStmts = new LinkedList<Statement>();
                                                    newStmts.add(blockStmts.get(0));
                                                    newBlock.setStmts(newStmts);

                                                    IfStmt newIf = new IfStmt(
                                                            new BinaryExpr(new NameExpr(variableNameOfReturnExpression),
                                                                    new NullLiteralExpr(), BinaryExpr.Operator.equals),
                                                            newBlock, null);
                                                    innerStmts.add(newIf);
                                                    innerBlock.setStmts(innerStmts);
                                                    synchStmt.setBlock(innerBlock);
                                                    synchStmt.setExpr(new ThisExpr());
                                                    blockStmts.clear();
                                                    blockStmts.add(synchStmt);
                                                }

                                            } else {
                                                BlockStmt blockStmt = new BlockStmt();
                                                List<Statement> stmtList = new LinkedList<Statement>();
                                                SynchronizedStmt synchStmt = new SynchronizedStmt();
                                                BlockStmt innerBlock = new BlockStmt();
                                                List<Statement> innerStmts = new LinkedList<Statement>();
                                                IfStmt newIf = new IfStmt(
                                                        new BinaryExpr(new NameExpr(variableNameOfReturnExpression),
                                                                new NullLiteralExpr(), BinaryExpr.Operator.equals),
                                                        thenStmt, null);
                                                innerStmts.add(newIf);
                                                innerBlock.setStmts(innerStmts);
                                                synchStmt.setBlock(innerBlock);
                                                synchStmt.setExpr(new ThisExpr());
                                                stmtList.add(synchStmt);
                                                blockStmt.setStmts(stmtList);
                                                ifStmt.setThenStmt(blockStmt);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
