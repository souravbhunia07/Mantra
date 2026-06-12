package com.sourav.mantra;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static com.sourav.mantra.TokenType.*;

public class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

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
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Mantra.error(line, "Unexpected character.");
                }
                break;
        }
    }

    // Scans an identifier or keyword and adds the appropriate token.
    private void identifier() {
        while (isAlphaNumeric(peek())) advance(); //  // Consume all letters, digits, and underscores that form the identifier.

        String text = source.substring(start, current);

        // Check if the lexeme matches a reserved keyword.
        // If found, use the corresponding keyword token type.
        TokenType type = keywords.get(text);

        // Otherwise, treat it as a user-defined identifier.
        if (type == null) type = IDENTIFIER;

        // Add the token to the token list.
        addToken(type);
    }

    // Scans a numeric literal (integer or floating-point number)
    // and adds it as a NUMBER token.
    private void number() {
        while(isDigit(peek())) advance();  // Consume all consecutive digits of the integer part.

        // Look for a decimal part.
        // Check for a decimal point followed by at least one digit.
        // This ensures that "123.45" is parsed as a single number token.
        if(peek() == '.' && isDigit(peekNext())) {
            advance(); // Consume the '.'

            while(isDigit(peek())) advance();
        }

        // Convert the lexeme into a Double and store it as the token's literal value.
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
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

    private char peekNext() {  // Peelk the next character
        if (current + 1 >=  source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {  // Check if the character is a digit
        return c >= '0' && c <= '9';
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
