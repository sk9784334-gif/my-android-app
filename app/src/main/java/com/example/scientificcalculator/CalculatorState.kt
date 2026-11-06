package com.example.scientificcalculator

data class CalculatorState(
    val display: String = "0",
    val operand1: Double? = null,
    val operand2: Double? = null,
    val operation: CalculatorOperation? = null,
    val isEnteringDecimal: Boolean = false,
    val isResultDisplayed: Boolean = false
)
