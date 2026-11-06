package com.example.scientificcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scientificcalculator.ui.theme.ScientificCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScientificCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = viewModel<CalculatorViewModel>()
                    val state by viewModel.state.collectAsState()
                    CalculatorScreen(state = state, onAction = viewModel::onAction)
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonSpacing = 8.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        // Display
        Text(
            text = state.display,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 48.sp,
            maxLines = 2,
            softWrap = false,
        )
        Spacer(Modifier.height(buttonSpacing))

        // Calculator Buttons Grid
        val buttons = listOf(
            // Row 1: Scientific Functions
            listOf(
                Pair("AC", CalculatorAction.Clear),
                Pair("DEL", CalculatorAction.Delete),
                Pair("√", CalculatorAction.UnaryOperation(CalculatorOperation.SquareRoot)),
                Pair("^", CalculatorAction.Operation(CalculatorOperation.Power)),
                Pair("÷", CalculatorAction.Operation(CalculatorOperation.Divide))
            ),
            // Row 2: Scientific Functions + Numbers
            listOf(
                Pair("sin", CalculatorAction.Trigonometric(TrigonometricFunction.Sin)),
                Pair("cos", CalculatorAction.Trigonometric(TrigonometricFunction.Cos)),
                Pair("tan", CalculatorAction.Trigonometric(TrigonometricFunction.Tan)),
                Pair("7", CalculatorAction.Number(7)),
                Pair("8", CalculatorAction.Number(8)),
                Pair("9", CalculatorAction.Number(9)),
                Pair("×", CalculatorAction.Operation(CalculatorOperation.Multiply))
            ),
            // Row 3: Scientific Functions + Numbers
            listOf(
                Pair("log", CalculatorAction.UnaryOperation(CalculatorOperation.Log)),
                Pair("ln", CalculatorAction.UnaryOperation(CalculatorOperation.Ln)),
                Pair("eˣ", CalculatorAction.UnaryOperation(CalculatorOperation.Exp)),
                Pair("4", CalculatorAction.Number(4)),
                Pair("5", CalculatorAction.Number(5)),
                Pair("6", CalculatorAction.Number(6)),
                Pair("-", CalculatorAction.Operation(CalculatorOperation.Subtract))
            ),
            // Row 4: Scientific Functions + Numbers
            listOf(
                Pair("π", CalculatorAction.Pi),
                Pair("e", CalculatorAction.E),
                Pair("x!", CalculatorAction.UnaryOperation(CalculatorOperation.Factorial)),
                Pair("1", CalculatorAction.Number(1)),
                Pair("2", CalculatorAction.Number(2)),
                Pair("3", CalculatorAction.Number(3)),
                Pair("+", CalculatorAction.Operation(CalculatorOperation.Add))
            ),
            // Row 5: Numbers + Operators (Equals spans two columns)
            listOf(
                Pair("+/-", CalculatorAction.Negate),
                Pair("0", CalculatorAction.Number(0)),
                Pair(".", CalculatorAction.Decimal),
                Pair("=", CalculatorAction.Calculate)
            )
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                row.forEach { (text, action) ->
                    val currentButtonModifier = if (text == "=") {
                        Modifier.weight(2f) // Occupy 2 units of weight
                    } else {
                        Modifier.weight(1f) // Occupy 1 unit of weight
                    }
                    val buttonColor = when (action) {
                        is CalculatorAction.Operation,
                        is CalculatorAction.UnaryOperation,
                        is CalculatorAction.Calculate,
                        is CalculatorAction.Trigonometric,
                        is CalculatorAction.Clear,
                        is CalculatorAction.Delete,
                        CalculatorAction.Pi,
                        CalculatorAction.E,
                        CalculatorAction.Negate -> MaterialTheme.colorScheme.primary
                        CalculatorAction.Decimal,
                        is CalculatorAction.Number -> MaterialTheme.colorScheme.surface
                    }
                    val textColor = when (action) {
                        is CalculatorAction.Operation,
                        is CalculatorAction.UnaryOperation,
                        is CalculatorAction.Calculate,
                        is CalculatorAction.Trigonometric,
                        is CalculatorAction.Clear,
                        is CalculatorAction.Delete,
                        CalculatorAction.Pi,
                        CalculatorAction.E,
                        CalculatorAction.Negate -> MaterialTheme.colorScheme.onPrimary
                        CalculatorAction.Decimal,
                        is CalculatorAction.Number -> MaterialTheme.colorScheme.onSurface
                    }

                    CalculatorButton(
                        text = text,
                        modifier = currentButtonModifier.aspectRatio(1f),
                        color = buttonColor,
                        textColor = textColor
                    ) {
                        onAction(action)
                    }
                }
            }
        }
    }
}


@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = text, fontSize = 28.sp, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    ScientificCalculatorTheme {
        CalculatorScreen(state = CalculatorState(), onAction = {})
    }
}
