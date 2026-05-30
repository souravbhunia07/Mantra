package com.sourav.mantra;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static com.sourav.mantra.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // Constructor: initializes the scanner with the source code to tokenize.
    Scanner(String source) {
        this.source = source;
    }

    // Scans the entire source code and converts it into a list of tokens.
    // Adds an EOF token at the end to mark the end of input.
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    // Checks whether the scanner has reached the end of the source code.
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Reads the next character and determines which token it represents.
    // Creates the corresponding token and adds it to the token list.
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) { // Comment
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {  // Division
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;

            default:
                Mantra.error(line, "Unexpected character.");
                break;
        }
    }

    // Consumes the current character and moves the scanner forward.
    // Returns the consumed character.
    private char advance() {
        return source.charAt(current++);
    }

    // Adds a token without any literal value.
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // Creates a token from the current lexeme and adds it to the token list.
    // The lexeme is the substring between 'start' and 'current'.
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    // Checks whether the next character matches the expected character. [Lookahead]
    // If it matches, consumes the character by advancing the current position
    // and returns true. Otherwise, leaves the scanner unchanged and returns false.
    // Commonly used for two-character operators such as !=, ==, <=, and >=.
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    // Peeks at the current character without advancing the scanner.
    // Returns '\0' if the end of the source has been reached.
    // Lookahead
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Mantra.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }
}
