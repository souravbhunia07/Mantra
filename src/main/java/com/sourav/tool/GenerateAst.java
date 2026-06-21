package com.sourav.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

// Generates AST classes automatically from a list of type definitions.
public class GenerateAst {

    // Entry point for the AST generator tool.
    public static void main(String[] args) throws IOException {

        // Ensure an output directory is provided.
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        // Directory where the generated AST file will be written.
        String outputDir = args[0];

        // Define the Expr AST hierarchy and its subclasses.
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    // Generates the base AST class and all of its nested subclasses.
    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {

        // Build the output file path.
        String path = outputDir + "/" + baseName + ".java";

        // Writer used to generate the Java source file.
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        // Write package declaration.
        writer.println("package com.sourav.mantra;");
        writer.println();

        // Write required imports.
        writer.println("import java.util.List;");
        writer.println();

        // Begin the abstract base AST class.
        writer.println("abstract class " + baseName + " {");

        // Generate each AST subclass.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // Close the base class definition.
        writer.println("}");
        writer.close();
    }

    // Generates a single AST node subclass.
    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) throws IOException {

        // Begin the subclass declaration.
        writer.println(" static class " + className + " extends " + baseName + " {");

        // Generate the constructor.
        writer.println(" " + className + "(" + fieldList + ") {");

        // Extract individual field definitions.
        String[] fields = fieldList.split(", ");

        // Assign constructor parameters to instance fields.
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println(" this." + name + " = " + name + ";");
        }

        // End constructor.
        writer.println("}");

        // Generate field declarations.
        writer.println();
        for (String field : fields) {
            writer.println(" final " + field + ";");
        }

        // End subclass definition.
        writer.println("}");
    }
}