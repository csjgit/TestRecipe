package com.test;

import org.openrewrite.*;
import org.openrewrite.text.PlainText;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class XmlCdataToJavaRecipe extends Recipe {

    @Override
    public String getDisplayName() {
        return "Extract Java classes from XML <node><![CDATA[]]>";
    }

    @Override
    public String getDescription() {
        return "Parses XML with <node><![CDATA[...]]> and generates .java files for each class.";
    }

    @Override
    public List<Result> run(List<SourceFile> before, ExecutionContext ctx) {
        List<Result> results = new ArrayList<>();

        for (SourceFile sf : before) {
            if (!(sf instanceof PlainText)) {
                continue;
            }

            PlainText pt = (PlainText) sf;
            String content = pt.getText();

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setCoalescing(true); // merge CDATA + text
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

                NodeList nodes = doc.getElementsByTagName("node");

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    String javaCode = node.getTextContent().trim();

                    // extract class name
                    String className = "Generated" + i;
                    int idx = javaCode.indexOf("class ");
                    if (idx >= 0) {
                        int end = javaCode.indexOf(" ", idx + 6);
                        if (end > 0) {
                            className = javaCode.substring(idx + 6, end).trim();
                        }
                    }

                    PlainText javaFile = PlainText.builder()
                            .id(Tree.randomId())
                            .sourcePath(Paths.get(className + ".java"))
                            .text(javaCode)
                            .build();

                    // ✅ null "before" means it's a new file
                    results.add(new Result(null, javaFile, null));
                }

            } catch (Exception e) {
                ctx.getOnError().accept(e);
            }
        }

        return results;
    }
}
