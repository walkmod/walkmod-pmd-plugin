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

@RequiresSemanticAnalysis(optional = true)
public class PMDVisitor extends VoidVisitorAdapter<VisitorContext> {

    private String configurationfile ="rulesets/java/basic.xml";

    private RuleSet rules = new RuleSet();

    private List<Rule> fixingRules = new LinkedList<Rule>();

    private List<PMDRuleVisitor> visitors = null;

    private boolean splitExecution = false;

    private void parseCfg(String config) throws Exception {

        RuleSetFactory factory = new RuleSetFactory();
        rules = factory.createRuleSet(config);

    }
    
    private String getRuleSetParts(Rule rule){
        String url = rule.getExternalInfoUrl();
        String[] parts = url.split("#");
        int index = parts[0].lastIndexOf("/");
        String package_ = parts[0].substring(index + 1, parts[0].length() - ".html".length());
        String name = parts[1];
        
        return package_+":"+name;
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
                          
                            String[] parts = getRuleSetParts(rule).split(":");
                          
                            Class<?> c = Class.forName("org.walkmod.pmd.ruleset.java." + parts[0] + ".visitors." + parts[1],
                                    true, ctx.getClassLoader());
                            o = c.newInstance();
                            visitors.add((PMDRuleVisitor) o);
                            fixingRules.add(rule);
                        } catch (Exception e) {
                        }
                    }

                }

            }

            Iterator<Rule> it = fixingRules.iterator();
            for (PMDRuleVisitor visitor : visitors) {
                if (splitExecution) {
                    boolean requiresSemanticAnalysis = visitor.getClass()
                            .isAnnotationPresent(RequiresSemanticAnalysis.class);
                    CompilationUnit aux = null;
                    if (cu.withSymbols() || !requiresSemanticAnalysis) {

                        aux = (CompilationUnit) new CloneVisitor().visit(cu, ctx);

                        visitor.visit(cu, aux);
                        ctx.addResultNode(aux);

                    }
                    if (it.hasNext()) {
                        Rule rule = it.next();
                        if (aux != null) {
                            Map<String, Object> data = new HashMap<String, Object>();
                            String[] ruleId = getRuleSetParts(rule).split(":");
                            data.put("cause", "pmd.java." + ruleId[0] + "." + ruleId[1]);
                            aux.setData(data);
                        }
                    }
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
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        parseCfg(configurationfile);
        Thread.currentThread().setContextClassLoader(cloader);
    }

    public void setVisitors(List<PMDRuleVisitor> list) {
        this.visitors = list;
    }

    public void setSplitExecution(boolean splitExecution) {
        this.splitExecution = splitExecution;
    }

}
