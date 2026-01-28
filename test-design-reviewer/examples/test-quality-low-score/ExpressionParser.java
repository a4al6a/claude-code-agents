package com.example.parser;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple arithmetic expression parser that supports:
 * - Basic operations: +, -, *, /
 * - Parentheses for grouping
 * - Integer and decimal numbers
 * - Negative numbers
 */
public class ExpressionParser {

    private String expression;
    private int position;

    public double parse(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }

        this.expression = expression.replaceAll("\\s+", "");
        this.position = 0;

        double result = parseExpression();

        if (position < this.expression.length()) {
            throw new IllegalArgumentException("Unexpected character at position " + position + ": " + this.expression.charAt(position));
        }

        return result;
    }

    private double parseExpression() {
        double left = parseTerm();

        while (position < expression.length()) {
            char operator = expression.charAt(position);

            if (operator != '+' && operator != '-') {
                break;
            }

            position++;
            double right = parseTerm();

            if (operator == '+') {
                left = left + right;
            } else {
                left = left - right;
            }
        }

        return left;
    }

    private double parseTerm() {
        double left = parseFactor();

        while (position < expression.length()) {
            char operator = expression.charAt(position);

            if (operator != '*' && operator != '/') {
                break;
            }

            position++;
            double right = parseFactor();

            if (operator == '*') {
                left = left * right;
            } else {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                left = left / right;
            }
        }

        return left;
    }

    private double parseFactor() {
        // Handle negative numbers
        boolean negative = false;
        if (position < expression.length() && expression.charAt(position) == '-') {
            negative = true;
            position++;
        }

        double result;

        if (position < expression.length() && expression.charAt(position) == '(') {
            position++; // skip '('
            result = parseExpression();

            if (position >= expression.length() || expression.charAt(position) != ')') {
                throw new IllegalArgumentException("Missing closing parenthesis");
            }
            position++; // skip ')'
        } else {
            result = parseNumber();
        }

        return negative ? -result : result;
    }

    private double parseNumber() {
        int start = position;

        while (position < expression.length() &&
               (Character.isDigit(expression.charAt(position)) || expression.charAt(position) == '.')) {
            position++;
        }

        if (start == position) {
            throw new IllegalArgumentException("Expected number at position " + position);
        }

        String numberStr = expression.substring(start, position);

        try {
            return Double.parseDouble(numberStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + numberStr);
        }
    }
}
