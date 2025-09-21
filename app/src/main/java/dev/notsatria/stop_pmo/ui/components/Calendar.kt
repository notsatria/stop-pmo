package dev.notsatria.stop_pmo.ui.components

import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.DummyData

private const val CALENDAR_ROWS = 5
private const val CALENDAR_COLUMNS = 7

data class CalendarInput(
    val day: Int,
    val relapseList: List<RelapseEvent> = emptyList()
)

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    calendarInput: List<CalendarInput>,
    onDayClick: (Int) -> Unit,
    strokeWidth: Float = 1f,
) {
    val theme = LocalTheme.current
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val canvasHeight = size.height
            val canvasWidth = size.width
            val cellHeight = canvasHeight / CALENDAR_ROWS
            val cellWidth = canvasWidth / CALENDAR_COLUMNS

            drawRoundRect(
                theme.buttonPrimary,
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(
                    width = strokeWidth
                )
            )

            for (row in 1 until CALENDAR_ROWS) {
                drawLine(
                    color = theme.buttonPrimary,
                    start = Offset(0f, (row * cellHeight)),
                    end = Offset(canvasWidth, row * cellHeight),
                    strokeWidth = strokeWidth
                )
            }
            for (col in 1 until CALENDAR_COLUMNS) {
                drawLine(
                    color = theme.buttonPrimary,
                    start = Offset(col * cellWidth, 0f),
                    end = Offset(col * cellWidth, canvasHeight),
                    strokeWidth = strokeWidth
                )
            }

            for (index in calendarInput.indices) {
                val input = calendarInput[index]
                val row = index / CALENDAR_COLUMNS
                val col = index % CALENDAR_COLUMNS

                val x = col * cellWidth + cellWidth / 2
                val y = row * cellHeight + cellHeight / 2

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        input.day.toString(),
                        x,
                        y,
                        TextPaint().apply {
                            textSize = 20.sp.toPx()
                            color = theme.buttonPrimary.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                if (input.relapseList.isNotEmpty()) {
                    // Draw a small circle to indicate tasks
                    drawCircle(
                        color = theme.buttonPrimary,
                        radius = 5f,
                        center = Offset(x, y + 20f)
                    )
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun Preview() {
    Calendar(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1.3f),
        calendarInput = DummyData.generateCalendarInputList(),
        onDayClick = {}
    )
}