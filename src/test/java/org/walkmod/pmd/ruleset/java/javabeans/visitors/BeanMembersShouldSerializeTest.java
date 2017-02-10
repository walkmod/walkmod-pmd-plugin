package org.walkmod.pmd.ruleset.java.javabeans.visitors;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.actions.Action;
import org.walkmod.javalang.actions.ActionsApplier;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.body.FieldDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.javalang.walkers.ChangeLogVisitor;
import org.walkmod.walkers.VisitorContext;

public class BeanMembersShouldSerializeTest extends SemanticTest {

    @Test
    public void should_add_transient_when_there_is_only_a_getter() throws Exception {
        CompilationUnit cu = compile(
                "public class Foo{ private String name; public String getName(){ return name; } }");

        BeanMembersShouldSerialize visitor = new BeanMembersShouldSerialize();
        visitor.visit(cu, cu);

        FieldDeclaration fd = (FieldDeclaration) cu.getTypes().get(0).getMembers().get(0);
        Assert.assertTrue(ModifierSet.isTransient(fd.getModifiers()));

    }
    
    @Test
    public void should_not_add_transient_when_there_are_getter_and_setter() throws Exception{
        CompilationUnit cu = compile(
                "public class Foo{ private String name; public String getName(){ return name; } public void setName(String name){ this.name = name; } }");

        BeanMembersShouldSerialize visitor = new BeanMembersShouldSerialize();
        visitor.visit(cu, cu);

        FieldDeclaration fd = (FieldDeclaration) cu.getTypes().get(0).getMembers().get(0);
        Assert.assertFalse(ModifierSet.isTransient(fd.getModifiers()));
    }
    
    @Test
    public void should_respect_javadoc() throws Exception{
        
        File src = new File("src/test/resources/examples/bugOnTransientRendering.txt");
        CompilationUnit cu = ASTManager.parse(src);
        BeanMembersShouldSerialize visitor = new BeanMembersShouldSerialize();
        visitor.visit(cu, cu);
        
        ChangeLogVisitor clog = new ChangeLogVisitor();
        VisitorContext vc = new VisitorContext();
        vc.put(ChangeLogVisitor.NODE_TO_COMPARE_KEY, cu);
        
        CompilationUnit original =ASTManager.parse(src);
        
        clog.visit(original, vc);
        
        List<Action> actions = clog.getActionsToApply();
        
        ActionsApplier ap = new ActionsApplier();
        ap.setActionList(actions);
        ap.setText(src);
        ap.execute();
        
        System.out.println(ap.getModifiedText());
    }
}
