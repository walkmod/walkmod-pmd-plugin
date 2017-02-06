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
package org.walkmod.pmd.visitors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.javalang.visitors.CloneVisitor;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;

@RequiresSemanticAnalysis
public class PMDVisitor extends VoidVisitorAdapter<VisitorContext> {

    private String configurationfile;

    private RuleSet rules = new RuleSet();

    private List<Rule> fixingRules = new LinkedList<Rule>();

    private List<PMDRuleVisitor> visitors = null;

    private boolean splitExecution = false;

    private void parseCfg(String config) throws Exception {

        RuleSetFactory factory = new RuleSetFactory();
        rules = factory.createRuleSet(config);

    }

    @Override
    public void visit(CompilationUnit cu, VisitorContext ctx) {

        if (rules != null) {
            if (visitors == null) {
                visitors = new LinkedList<PMDRuleVisitor>();
                for (Rule rule : rules.getRules()) {

                    Object o = null;
                    if (rule.getLanguage().getName().toLowerCase().equals("java")) {
                        try {
                           
                            Class<?> c = Class.forName("org.walkmod.pmd.ruleset.java."
                                    + rule.getRuleSetName().toLowerCase() + ".visitors." + rule.getName(),true,ctx.getClassLoader());
                            o = c.newInstance();
                        } catch (Exception e) {
                        }
                    }
                    if (o instanceof PMDRuleVisitor) {
                        visitors.add((PMDRuleVisitor) o);
                        fixingRules.add(rule);
                    }

                }

            }
            Iterator<Rule> it = fixingRules.iterator();
            for (PMDRuleVisitor visitor : visitors) {
                if (splitExecution) {

                    CompilationUnit aux;

                    aux = (CompilationUnit) new CloneVisitor().visit(cu, ctx);

                    visitor.visit(cu, aux);
                    if (it.hasNext()) {
                        Rule rule = it.next();
                        Map<String, Object> data = new HashMap<String, Object>();
                        data.put("cause", "pmd.java." + rule.getRuleSetName() + "." + rule.getName());
                        aux.setData(data);
                    }
                    ctx.addResultNode(aux);

                } else {
                    visitor.visit(cu, null);
                }
            }

        }
    }

    public RuleSet getRules() {
        return rules;
    }

    public void setConfigurationFile(String configurationFile) throws Exception {
        this.configurationfile = configurationFile;
        parseCfg(configurationfile);
    }

    public void setVisitors(List<PMDRuleVisitor> list) {
        this.visitors = list;
    }

    public void setSplitExecution(boolean splitExecution) {
        this.splitExecution = splitExecution;
    }

}
