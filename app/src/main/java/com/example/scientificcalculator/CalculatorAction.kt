package com.example.scientificcalculator

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    object Decimal : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction() // For binary operations (+, -, *, /, ^)
    data class UnaryOperation(val operation: CalculatorOperation) : CalculatorAction() // For unary operations (sqrt, log, ln, exp, !)
    object Calculate : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    data class Trigonometric(val func: TrigonometricFunction) : CalculatorAction()
    object Pi : CalculatorAction()
    object E : CalculatorAction()
    object Negate : CalculatorAction()
}
