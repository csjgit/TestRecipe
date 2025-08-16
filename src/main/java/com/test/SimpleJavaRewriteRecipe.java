package com.test;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.Space;

public class SimpleJavaRewriteRecipe extends Recipe {
    @Override
    public String getDisplayName() {
        return "Simple Java Rewrite Recipe";
    }

    @Override
    public String getDescription() {
        return "Appends a comment to every Java class declaration.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx) {
                // Visit the superclass first
                J.ClassDeclaration cd = super.visitClassDeclaration(classDecl, ctx);

                // Add the comment as prefix space
                return cd.withPrefix(
                        Space.format("/* Modified by OpenRewrite */\n")
                );
            }
        };
    }
}