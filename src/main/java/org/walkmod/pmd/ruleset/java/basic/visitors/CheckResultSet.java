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

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.SymbolDefinition;
import org.walkmod.javalang.ast.SymbolReference;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.body.VariableDeclarator;
import org.walkmod.javalang.ast.expr.MethodCallExpr;
import org.walkmod.javalang.ast.expr.VariableDeclarationExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.ast.stmt.ExpressionStmt;
import org.walkmod.javalang.ast.stmt.IfStmt;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.ast.type.Type;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.pmd.visitors.Modification;
import org.walkmod.pmd.visitors.PMDRuleVisitor;

@RequiresSemanticAnalysis
@Modification
public class CheckResultSet extends PMDRuleVisitor {

    @Override
    public void visit(MethodDeclaration md, Node node) {
        super.visit(md, node);
        MethodDeclaration aux = (MethodDeclaration) node;

        List<Parameter> params = md.getParameters();
        List<Parameter> auxParams = aux.getParameters();
        if (params != null) {

            Iterator<Parameter> it = params.iterator();
            Iterator<Parameter> it2 = auxParams.iterator();

            while (it.hasNext()) {
                Parameter current = it.next();
                Parameter currentAux = it2.next();

                Type type = current.getType();
                SymbolData sd = type.getSymbolData();

                if (sd != null) {
                    if (sd.getClazz().isAssignableFrom(ResultSet.class)) {
                        updateStmts(currentAux);
                    }
                }
            }
        }

    }

    private void updateStmts(SymbolDefinition symbolDef) {
        List<SymbolReference> usages = symbolDef.getUsages();
        if (usages != null) {
            Iterator<SymbolReference> itUsages = usages.iterator();
            boolean updated = false;
            while (itUsages.hasNext() && !updated) {
                SymbolReference usage = itUsages.next();
                Node parent = ((Node) usage).getParentNode();
                if (parent instanceof MethodCallExpr) {
                    MethodCallExpr mce = (MethodCallExpr) parent;
                    if (mce.getName().equals("next")) {
                        Node grandParent = mce.getParentNode();
                        if (grandParent instanceof ExpressionStmt) {
                            Node grandGrandParent = grandParent.getParentNode();
                            if (grandGrandParent instanceof BlockStmt) {
                                if (itUsages.hasNext()) {
                                    BlockStmt block = (BlockStmt) grandGrandParent;
                                    List<Statement> stmts = new LinkedList<Statement>(block.getStmts());
                                    List<Statement> pendingBock = new LinkedList<Statement>();
                                    Iterator<Statement> itStmt = stmts.iterator();
                                    boolean removed = false;
                                    while (!removed && itStmt.hasNext()) {
                                        Statement currentStmt = itStmt.next();
                                        if (currentStmt == grandParent) {
                                            if (itStmt.hasNext()) {
                                                itStmt.remove();
                                                while (itStmt.hasNext()) {
                                                    pendingBock.add(itStmt.next());
                                                    itStmt.remove();
                                                }
                                                removed = true;
                                            }
                                        }
                                    }
                                    if (removed) {
                                        stmts.add(new IfStmt(mce, new BlockStmt(pendingBock), null));
                                    }
                                    block.setStmts(stmts);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visit(VariableDeclarationExpr vd, Node ctx) {
        super.visit(vd, ctx);

        VariableDeclarationExpr aux = (VariableDeclarationExpr) ctx;
        Type type = vd.getType();
        SymbolData sd = type.getSymbolData();

        if (sd != null) {
            if (sd.getClazz().isAssignableFrom(ResultSet.class)) {
                List<VariableDeclarator> vars = vd.getVars();
                List<VariableDeclarator> vars2 = aux.getVars();

                if (vars != null) {
                    Iterator<VariableDeclarator> it = vars2.iterator();

                    while (it.hasNext()) {
                        VariableDeclarator currentVar = it.next();

                        updateStmts(currentVar);
                    }
                }
            }
        }

    }
}
