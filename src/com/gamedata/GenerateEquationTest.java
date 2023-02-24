package com.gamedata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenerateEquationTest {

    private final int testCount = 1000;

    boolean checkOperatorValidity(String equation) {
        for (int i = 0; i < equation.length() - 1; i++) {
            if (
                    !Character.isDigit(equation.charAt(i))  // Find an operator
                            && (( // Detect double operator
                            !(Character.isDigit(equation.charAt(i + 1))  // Check if its next to another one
                                    || equation.charAt(i + 1) == '-')) // Exclude negative number after equals sign

                    )
            ) {
                return false;
            }
        }

        return true;
    }

    boolean checkLeadingZero(String equation) {
        for (int i = 0; i < equation.length() - 1; i++) {
            if (
                // Detect integer with leading 0
                    i < equation.length() - 2 // Avoid index error
                            && !Character.isDigit(equation.charAt(i)) // Check for non digit trailing character
                            && equation.charAt(i + 1) == '0'    // Check for leading 0
                            && Character.isDigit(equation.charAt(i + 2))  // Find a trailing digit

            ) {
                return false;
            }
        }

        return true;
    }

    @Test
    void operatorStructureCheck() {
        for (int i = 0; i < testCount; i++) {

            String equation = Game.generateEquation();

            assertAll(
                    () -> assertTrue(equation.contains("="), "There is no equation sign"),
                    () -> assertFalse(!equation.contains("+") &&
                            !equation.contains("-") &&
                            !equation.contains("/") &&
                            !equation.contains("*"), "There is no operator"),
                    () -> assertTrue(checkOperatorValidity(equation), "There are consecutive operators")
            );
        }
    }

    @Test
    void digitStructureCheck() {
        for (int i = 0; i < testCount; i++) {

            String equation = Game.generateEquation();

            assertAll(
                    () -> assertFalse(!Character.isDigit(equation.charAt(0)) && equation.charAt(0) != '-', "There is no digit or minus sign at the beginning"),
                    () -> assertTrue(Character.isDigit(equation.charAt(equation.length() - 1)), "There is no digit at the end"),
                    () -> assertTrue(checkLeadingZero(equation), "There are leading zeroes")
            );
        }
    }

    @Test
    void integerResultCheck() {
        for (int i = 0; i < testCount; i++) {

            String equation = Game.generateEquation();

            assertAll(
                    () -> assertFalse(Game.evaluateExpression(equation.split("=")[0]) % 1 != 0, "Expression at left hand side does not resolve to an integer")
            );
        }
    }

    @Test
    void equalityCheck() {
        for (int i = 0; i < testCount; i++) {

            String equation = Game.generateEquation();

            assertAll(
                    () -> assertEquals(String.valueOf((int) Game.evaluateExpression(equation.split("=")[0])), equation.split("=")[1], "Left hand side doesn't resolve to right side")
            );
        }
    }


}