package com.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;

@Value
@EqualsAndHashCode(callSuper = false)
public class ReplaceStringRecipe extends Recipe {

    @Option(displayName = "Target string",
            description = "The string to search for.",
            example = "Hello")
    String targetString;

    @Option(displayName = "Replacement string",
            description = "The string to replace with.",
            example = "Hi")
    String replacementString;

    @Option(displayName = "Excluded class suffix",
            description = "Class name suffix to exclude from replacement.",
            required = false,
            example = "Recipe")
    String excludedClassSuffix;

    @JsonCreator
    public ReplaceStringRecipe(@JsonProperty("targetString")String targetString, @JsonProperty("replacementString") String replacementString,
                               @JsonProperty("excludedClassSuffix") String excludedClassSuffix) {
         this.targetString = targetString;
        this.replacementString = replacementString;
        this.excludedClassSuffix = excludedClassSuffix;
    }


    @Override
    public String getDisplayName() {
        return "Replace string in Java files";
    }

    @Override
    public String getDescription() {
        return "Replaces occurrences of a target string literal with a replacement string, " +
                "optionally excluding classes whose names end with a given suffix.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext ctx) {
                // If excludedClassSuffix is provided, skip those classes
                if (excludedClassSuffix != null &&
                        classDecl.getSimpleName().endsWith(excludedClassSuffix)) {
                    return classDecl;
                }
                return super.visitClassDeclaration(classDecl, ctx);
            }

            @Override
            public J.Literal visitLiteral(J.Literal literal, ExecutionContext ctx) {
                J.Literal l = super.visitLiteral(literal, ctx);
                if (l.getValue() instanceof String) {
                    String value = (String) l.getValue();
                    if (targetString.equals(value)) {
                        return l.withValue(replacementString)
                                .withValueSource("\"" + replacementString + "\"");
                    }
                }
                return l;
            }
        };
    }
}
