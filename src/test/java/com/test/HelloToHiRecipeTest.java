package com.test;

import com.test.HelloToHiRecipe;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.java.Assertions;

class HelloToHiRecipeTest implements RewriteTest {

    @Test
    void replacesHelloWithHi() {
        rewriteRun(
                spec -> spec.recipe(new HelloToHiRecipe()), // Ensure this matches your recipe name
                Assertions.java(
                        """
                        class MyClass {
                            void say() {
                                System.out.println("Hello");
                            }
                        }
                        """,
                        """
                        class MyClass {
                            void say() {
                                System.out.println("Hi");
                            }
                        }
                        """
                )
        );
    }
}
