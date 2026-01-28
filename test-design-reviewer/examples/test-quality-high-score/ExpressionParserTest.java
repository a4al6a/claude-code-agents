package com.example.parser;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExpressionParser following Dave Farley's testing principles.
 *
 * Each test:
 * - Has a descriptive name that documents behavior
 * - Tests exactly one thing
 * - Is independent and isolated
 * - Runs fast without external dependencies
 */
@DisplayName("Expression Parser")
class ExpressionParserTest {

    // =========================================================================
    // SINGLE NUMBER PARSING
    // =========================================================================

    @Nested
    @DisplayName("when parsing a single number")
    class SingleNumberParsing {

        @Test
        @DisplayName("returns the integer value")
        void parsingSingleInteger_returnsItsValue() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("42");

            assertEquals(42.0, result);
        }

        @Test
        @DisplayName("returns the decimal value")
        void parsingSingleDecimal_returnsItsValue() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("3.14");

            assertEquals(3.14, result, 0.001);
        }

        @Test
        @DisplayName("handles negative integers")
        void parsingNegativeInteger_returnsNegativeValue() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("-7");

            assertEquals(-7.0, result);
        }

        @Test
        @DisplayName("handles negative decimals")
        void parsingNegativeDecimal_returnsNegativeValue() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("-2.5");

            assertEquals(-2.5, result, 0.001);
        }
    }

    // =========================================================================
    // ADDITION
    // =========================================================================

    @Nested
    @DisplayName("when adding numbers")
    class Addition {

        @Test
        @DisplayName("adds two positive integers")
        void addingTwoPositiveIntegers_returnsTheirSum() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("3+5");

            assertEquals(8.0, result);
        }

        @Test
        @DisplayName("adds multiple numbers left to right")
        void addingMultipleNumbers_evaluatesLeftToRight() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("1+2+3+4");

            assertEquals(10.0, result);
        }

        @Test
        @DisplayName("adds decimal numbers")
        void addingDecimals_returnsPreciseSum() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("1.5+2.5");

            assertEquals(4.0, result, 0.001);
        }
    }

    // =========================================================================
    // SUBTRACTION
    // =========================================================================

    @Nested
    @DisplayName("when subtracting numbers")
    class Subtraction {

        @Test
        @DisplayName("subtracts second number from first")
        void subtractingTwoNumbers_returnsDifference() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("10-3");

            assertEquals(7.0, result);
        }

        @Test
        @DisplayName("can produce negative results")
        void subtractingLargerFromSmaller_returnsNegative() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("3-10");

            assertEquals(-7.0, result);
        }

        @Test
        @DisplayName("chains subtractions left to right")
        void chainingSubtractions_evaluatesLeftToRight() {
            ExpressionParser parser = new ExpressionParser();

            // 10 - 3 - 2 = 7 - 2 = 5 (not 10 - 1 = 9)
            double result = parser.parse("10-3-2");

            assertEquals(5.0, result);
        }
    }

    // =========================================================================
    // MULTIPLICATION
    // =========================================================================

    @Nested
    @DisplayName("when multiplying numbers")
    class Multiplication {

        @Test
        @DisplayName("multiplies two positive integers")
        void multiplyingTwoPositiveIntegers_returnsProduct() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("6*7");

            assertEquals(42.0, result);
        }

        @Test
        @DisplayName("multiplying by zero returns zero")
        void multiplyingByZero_returnsZero() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("999*0");

            assertEquals(0.0, result);
        }

        @Test
        @DisplayName("multiplies decimal numbers")
        void multiplyingDecimals_returnsPreciseProduct() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("2.5*4");

            assertEquals(10.0, result, 0.001);
        }
    }

    // =========================================================================
    // DIVISION
    // =========================================================================

    @Nested
    @DisplayName("when dividing numbers")
    class Division {

        @Test
        @DisplayName("divides first number by second")
        void dividingTwoNumbers_returnsQuotient() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("20/4");

            assertEquals(5.0, result);
        }

        @Test
        @DisplayName("returns decimal when division is not exact")
        void dividingWithRemainder_returnsDecimal() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("7/2");

            assertEquals(3.5, result, 0.001);
        }

        @Test
        @DisplayName("throws ArithmeticException when dividing by zero")
        void dividingByZero_throwsArithmeticException() {
            ExpressionParser parser = new ExpressionParser();

            ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> parser.parse("5/0")
            );

            assertEquals("Division by zero", exception.getMessage());
        }
    }

    // =========================================================================
    // OPERATOR PRECEDENCE
    // =========================================================================

    @Nested
    @DisplayName("operator precedence")
    class OperatorPrecedence {

        @Test
        @DisplayName("multiplication has higher precedence than addition")
        void multiplicationBeforeAddition() {
            ExpressionParser parser = new ExpressionParser();

            // 2 + 3 * 4 = 2 + 12 = 14 (not 5 * 4 = 20)
            double result = parser.parse("2+3*4");

            assertEquals(14.0, result);
        }

        @Test
        @DisplayName("division has higher precedence than subtraction")
        void divisionBeforeSubtraction() {
            ExpressionParser parser = new ExpressionParser();

            // 10 - 6 / 2 = 10 - 3 = 7 (not 4 / 2 = 2)
            double result = parser.parse("10-6/2");

            assertEquals(7.0, result);
        }

        @Test
        @DisplayName("multiplication and division have equal precedence, evaluated left to right")
        void multiplicationAndDivision_evaluatedLeftToRight() {
            ExpressionParser parser = new ExpressionParser();

            // 12 / 3 * 2 = 4 * 2 = 8 (not 12 / 6 = 2)
            double result = parser.parse("12/3*2");

            assertEquals(8.0, result);
        }

        @Test
        @DisplayName("addition and subtraction have equal precedence, evaluated left to right")
        void additionAndSubtraction_evaluatedLeftToRight() {
            ExpressionParser parser = new ExpressionParser();

            // 10 - 5 + 3 = 5 + 3 = 8 (not 10 - 8 = 2)
            double result = parser.parse("10-5+3");

            assertEquals(8.0, result);
        }
    }

    // =========================================================================
    // PARENTHESES
    // =========================================================================

    @Nested
    @DisplayName("parentheses")
    class Parentheses {

        @Test
        @DisplayName("override normal precedence")
        void parenthesesOverridePrecedence() {
            ExpressionParser parser = new ExpressionParser();

            // (2 + 3) * 4 = 5 * 4 = 20
            double result = parser.parse("(2+3)*4");

            assertEquals(20.0, result);
        }

        @Test
        @DisplayName("can be nested")
        void nestedParenthesesAreEvaluatedInnerFirst() {
            ExpressionParser parser = new ExpressionParser();

            // ((1 + 2) * (3 + 4)) = (3 * 7) = 21
            double result = parser.parse("((1+2)*(3+4))");

            assertEquals(21.0, result);
        }

        @Test
        @DisplayName("can contain complex expressions")
        void parenthesesCanContainComplexExpressions() {
            ExpressionParser parser = new ExpressionParser();

            // (10 - 4 / 2) * 3 = (10 - 2) * 3 = 8 * 3 = 24
            double result = parser.parse("(10-4/2)*3");

            assertEquals(24.0, result);
        }

        @Test
        @DisplayName("missing closing parenthesis throws exception")
        void missingClosingParenthesis_throwsException() {
            ExpressionParser parser = new ExpressionParser();

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("(1+2")
            );

            assertTrue(exception.getMessage().contains("parenthesis"));
        }
    }

    // =========================================================================
    // WHITESPACE HANDLING
    // =========================================================================

    @Nested
    @DisplayName("whitespace handling")
    class WhitespaceHandling {

        @Test
        @DisplayName("ignores spaces between tokens")
        void spacesAreIgnored() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("  2  +  3  ");

            assertEquals(5.0, result);
        }

        @Test
        @DisplayName("ignores tabs between tokens")
        void tabsAreIgnored() {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse("2\t*\t3");

            assertEquals(6.0, result);
        }
    }

    // =========================================================================
    // ERROR HANDLING
    // =========================================================================

    @Nested
    @DisplayName("error handling")
    class ErrorHandling {

        @ParameterizedTest(name = "rejects {0}")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("rejects null, empty, and whitespace-only input")
        void rejectsInvalidInput(String input) {
            ExpressionParser parser = new ExpressionParser();

            assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(input)
            );
        }

        @Test
        @DisplayName("rejects expressions with trailing operator")
        void rejectsTrailingOperator() {
            ExpressionParser parser = new ExpressionParser();

            assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("5+")
            );
        }

        @Test
        @DisplayName("rejects expressions with leading operator (except minus)")
        void rejectsLeadingOperator() {
            ExpressionParser parser = new ExpressionParser();

            assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("+5")
            );
        }

        @Test
        @DisplayName("rejects invalid characters")
        void rejectsInvalidCharacters() {
            ExpressionParser parser = new ExpressionParser();

            assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("2+a")
            );
        }
    }

    // =========================================================================
    // COMPLEX EXPRESSIONS (Integration Tests)
    // =========================================================================

    @Nested
    @DisplayName("complex expressions")
    class ComplexExpressions {

        @ParameterizedTest(name = "{0} = {1}")
        @CsvSource({
            "1+2*3-4/2, 5.0",
            "(1+2)*(3-1), 6.0",
            "10/2+3*4-5, 12.0",
            "((2+3)*4-10)/2, 5.0",
            "-5+10, 5.0",
            "(-5)*(-2), 10.0"
        })
        @DisplayName("evaluates complex expressions correctly")
        void complexExpressionsAreEvaluatedCorrectly(String expression, double expected) {
            ExpressionParser parser = new ExpressionParser();

            double result = parser.parse(expression);

            assertEquals(expected, result, 0.001);
        }
    }
}
