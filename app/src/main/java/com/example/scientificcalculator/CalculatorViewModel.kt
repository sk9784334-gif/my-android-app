package com.example.scientificcalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.*

const val MAX_DISPLAY_LENGTH = 15

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> clear()
            is CalculatorAction.Operation -> enterOperation(action.operation) // Binary ops
            is CalculatorAction.UnaryOperation -> performUnary(action.operation) // Unary ops
            is CalculatorAction.Calculate -> calculate()
            is CalculatorAction.Delete -> deleteLast()
            is CalculatorAction.Trigonometric -> performTrigonometric(action.func)
            CalculatorAction.Pi -> enterPi()
            CalculatorAction.E -> enterE()
            CalculatorAction.Negate -> negate()
        }
    }

    private fun enterNumber(number: Int) {
        _state.update { currentState ->
            val currentDisplay = currentState.display

            if (currentState.isResultDisplayed || currentDisplay == "Error") {
                currentState.copy(
                    display = number.toString(),
                    isResultDisplayed = false,
                    isEnteringDecimal = false,
                    operand1 = null,
                    operand2 = null,
                    operation = null
                )
            } else if (currentDisplay.length >= MAX_DISPLAY_LENGTH) {
                currentState // Ignore input if max length reached
            } else if (currentDisplay == "0" && number == 0) {
                currentState
            } else if (currentDisplay == "0") {
                currentState.copy(display = number.toString())
            } else {
                currentState.copy(display = currentDisplay + number.toString())
            }
        }
    }

    private fun enterDecimal() {
        _state.update { currentState ->
            if (currentState.isResultDisplayed || currentState.display == "Error") {
                currentState.copy(
                    display = "0.",
                    isResultDisplayed = false,
                    isEnteringDecimal = true,
                    operand1 = null,
                    operand2 = null,
                    operation = null
                )
            } else if (!currentState.display.contains(".")) {
                currentState.copy(display = currentState.display + ".", isEnteringDecimal = true)
            } else {
                currentState
            }
        }
    }

    private fun enterOperation(operation: CalculatorOperation) { // This is for binary ops like +, -, *, /, ^
        _state.update { currentState ->
            try {
                if (currentState.display == "Error") {
                    return@update currentState
                }

                val currentDisplayValue = currentState.display.toDouble()

                if (currentState.operand1 == null || currentState.isResultDisplayed) {
                    // No pending operation, store current value as operand1
                    currentState.copy(
                        operand1 = currentDisplayValue,
                        operation = operation,
                        display = formatNumber(currentDisplayValue), // Display operand1 to indicate it's stored
                        isResultDisplayed = true // Indicate next input should clear display
                    )
                } else {
                    // There's a pending operation, calculate first, then apply new operation
                    val result = performCalculation(
                        currentState.operand1,
                        currentDisplayValue, // Use current display as operand2
                        currentState.operation
                    )
                    currentState.copy(
                        operand1 = result,
                        operation = operation,
                        display = formatNumber(result),
                        isResultDisplayed = true
                    )
                }
            } catch (e: NumberFormatException) {
                currentState.copy(display = "Error")
            } catch (e: ArithmeticException) {
                currentState.copy(display = "Error")
            }
        }
    }

    private fun calculate() {
        _state.update { currentState ->
            if (currentState.operand1 == null || currentState.operation == null || currentState.display == "Error") {
                return@update currentState
            }

            try {
                val operand2 = currentState.display.toDouble()
                val result = performCalculation(currentState.operand1, operand2, currentState.operation)
                currentState.copy(
                    display = formatNumber(result),
                    operand1 = null,
                    operand2 = null, 
                    operation = null,
                    isResultDisplayed = true,
                    isEnteringDecimal = false
                )
            } catch (e: NumberFormatException) {
                currentState.copy(display = "Error")
            } catch (e: ArithmeticException) {
                currentState.copy(display = "Error")
            }
        }
    }

    private fun performCalculation(operand1: Double?, operand2: Double, operation: CalculatorOperation?): Double {
        if (operand1 == null || operation == null) {
            throw IllegalStateException("Invalid state for calculation")
        }
        return when (operation) {
            CalculatorOperation.Add -> operand1 + operand2
            CalculatorOperation.Subtract -> operand1 - operand2
            CalculatorOperation.Multiply -> operand1 * operand2
            CalculatorOperation.Divide -> {
                if (operand2 == 0.0) throw ArithmeticException("Division by zero")
                operand1 / operand2
            }
            CalculatorOperation.Power -> operand1.pow(operand2)
            else -> throw UnsupportedOperationException("Invalid binary operation: $operation") // Should not happen for binary ops
        }
    }

    private fun clear() {
        _state.update {
            CalculatorState()
        }
    }

    private fun deleteLast() {
        _state.update { currentState ->
            if (currentState.display == "Error" || currentState.isResultDisplayed) {
                return@update CalculatorState() // Clear all if error or result
            }
            if (currentState.display.length > 1) {
                currentState.copy(display = currentState.display.dropLast(1))
            } else {
                currentState.copy(display = "0")
            }
        }
    }

    private fun performTrigonometric(func: TrigonometricFunction) {
        _state.update { currentState ->
            try {
                val value = currentState.display.toDouble()
                val result = when (func) {
                    TrigonometricFunction.Sin -> sin(Math.toRadians(value))
                    TrigonometricFunction.Cos -> cos(Math.toRadians(value))
                    TrigonometricFunction.Tan -> tan(Math.toRadians(value))
                }
                currentState.copy(
                    display = formatNumber(result),
                    operand1 = null,
                    operand2 = null,
                    operation = null,
                    isResultDisplayed = true,
                    isEnteringDecimal = false
                )
            } catch (e: NumberFormatException) {
                currentState.copy(display = "Error")
            }
        }
    }

    private fun enterPi() {
        _state.update { currentState ->
            currentState.copy(
                display = formatNumber(PI),
                isResultDisplayed = true,
                isEnteringDecimal = false,
                operand1 = null,
                operand2 = null,
                operation = null
            )
        }
    }

    private fun enterE() {
        _state.update { currentState ->
            currentState.copy(
                display = formatNumber(E),
                isResultDisplayed = true,
                isEnteringDecimal = false,
                operand1 = null,
                operand2 = null,
                operation = null
            )
        }
    }

    private fun negate() {
        _state.update { currentState ->
            try {
                val value = currentState.display.toDouble()
                val negated = -value
                currentState.copy(
                    display = formatNumber(negated),
                    isResultDisplayed = currentState.isResultDisplayed
                )
            } catch (e: NumberFormatException) {
                currentState.copy(display = "Error")
            }
        }
    }

    private fun performUnary(operation: CalculatorOperation) {
        _state.update { currentState ->
            try {
                val value = currentState.display.toDouble()
                val result = when (operation) {
                    CalculatorOperation.SquareRoot -> sqrt(value)
                    CalculatorOperation.Log -> log10(value)
                    CalculatorOperation.Ln -> ln(value)
                    CalculatorOperation.Exp -> exp(value)
                    CalculatorOperation.Factorial -> {
                        if (value < 0 || value != value.toInt().toDouble()) throw IllegalArgumentException("Factorial for non-negative integers only")
                        (1..value.toInt()).fold(1L) { acc, i -> acc * i }.toDouble()
                    }
                    else -> throw IllegalArgumentException("Unsupported unary operation: $operation")
                }
                currentState.copy(
                    display = formatNumber(result),
                    operand1 = null,
                    operand2 = null,
                    this.operation = null,
                    isResultDisplayed = true,
                    isEnteringDecimal = false
                )
            } catch (e: Exception) {
                currentState.copy(display = "Error")
            }
        }
    }

    private fun formatNumber(number: Double): String {
        val string = if (number % 1.0 == 0.0) {
            number.toLong().toString()
        } else {
            val formatted = "%.9f".format(number).trimEnd('0')
            if (formatted.endsWith(".")) formatted.dropLast(1) else formatted
        }
        return if (string.length > MAX_DISPLAY_LENGTH) string.take(MAX_DISPLAY_LENGTH) else string
    }
}
