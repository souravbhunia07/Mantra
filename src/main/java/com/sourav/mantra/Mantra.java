package com.sourav.mantra;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Mantra {
    static boolean hadError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: Mantra [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    // Run a code file, provide the path of the file
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if  (hadError) System.exit(65);
    }

    //  reads a line of input from the user on the command line and returns the result
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in); // takes imput
        BufferedReader reader = new BufferedReader(input);

        for(;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false; // If the user makes a mistake, it shouldn’t kill their entire session
        }
    }

    private static void run(String line) {
        Scanner scanner = new Scanner(line);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[Line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}