package dev.notsatria.stop_pmo.ui.screen.dashboard.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.screen.dashboard.RelapseDialogStep
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.formatDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Date
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun RelapseConfirmationDialog(
    modifier: Modifier = Modifier,
    currentStep: RelapseDialogStep,
    relapseReason: String,
    selectedRelapseTime: Instant,
    use24HourFormat: Boolean,
    onDismiss: () -> Unit,
    onConfirmRelapse: () -> Unit,
    onStillGoing: () -> Unit,
    onNextToReason: () -> Unit,
    onBackToConfirmation: () -> Unit,
    onBackToDateTime: () -> Unit,
    onReasonChange: (String) -> Unit,
    onSubmitRelapse: () -> Unit,
    onDateTimeChange: (Instant) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val theme = LocalTheme.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = theme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                val forward = targetState.ordinal > initialState.ordinal
                if (forward) {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300, easing = LinearEasing)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(300, easing = LinearEasing)
                    )
                } else {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(300, easing = LinearEasing)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300, easing = LinearEasing)
                    )
                }
            },
            label = "RelapseDialogAnimation"
        ) { step ->
            when (step) {
                RelapseDialogStep.CONFIRMATION -> ConfirmationContent(
                    modifier = modifier,
                    onConfirmRelapse = onConfirmRelapse,
                    onStillGoing = onStillGoing
                )

                RelapseDialogStep.DATE_TIME -> DateTimeContent(
                    modifier = modifier,
                    selectedTime = selectedRelapseTime,
                    use24HourFormat = use24HourFormat,
                    onBack = onBackToConfirmation,
                    onNext = onNextToReason,
                    onDateTimeChange = onDateTimeChange
                )

                RelapseDialogStep.REASON_INPUT -> ReasonInputContent(
                    modifier = modifier,
                    relapseReason = relapseReason,
                    onReasonChange = onReasonChange,
                    onBack = onBackToDateTime,
                    onSubmit = onSubmitRelapse,
                    onSkip = onSubmitRelapse
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun DateTimeContent(
    modifier: Modifier = Modifier,
    selectedTime: Instant,
    use24HourFormat: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onDateTimeChange: (Instant) -> Unit
) {
    val theme = LocalTheme.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val timeZone = TimeZone.currentSystemDefault()
    val localDateTime = selectedTime.toLocalDateTime(timeZone)

    val formattedDateTime = selectedTime.toEpochMilliseconds().let { millis ->
        val date = Date(millis)
        date.formatDate(use24HourFormat)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = theme.textPrimary
                )
            }
            Text(
                text = "When did this happen?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Select the date and time of the relapse. Defaults to now if unchanged.",
            fontSize = 14.sp,
            color = theme.textSecondary,
            textAlign = TextAlign.Start,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = theme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { showDatePicker = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = theme.iconPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = formattedDateTime,
                fontSize = 16.sp,
                color = theme.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Change date/time",
                tint = theme.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.buttonPrimary
            )
        ) {
            Text(
                text = "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if (showDatePicker) {
        val nowMillis = System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedTime.toEpochMilliseconds(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= nowMillis
                override fun isSelectableYear(year: Int) =
                    year <= java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val selectedDate = Date(selectedMillis)
                        val cal = java.util.Calendar.getInstance().apply {
                            time = selectedDate
                            set(java.util.Calendar.HOUR_OF_DAY, localDateTime.hour)
                            set(java.util.Calendar.MINUTE, localDateTime.minute)
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }
                        val newInstant = Instant.fromEpochMilliseconds(cal.timeInMillis)
                        onDateTimeChange(newInstant)
                    }
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = localDateTime.hour,
            initialMinute = localDateTime.minute,
            is24Hour = use24HourFormat
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val cal = java.util.Calendar.getInstance().apply {
                        timeInMillis = selectedTime.toEpochMilliseconds()
                        set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(java.util.Calendar.MINUTE, timePickerState.minute)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }
                    val newInstant = Instant.fromEpochMilliseconds(cal.timeInMillis)
                    onDateTimeChange(newInstant)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

@Composable
private fun ConfirmationContent(
    modifier: Modifier = Modifier,
    onConfirmRelapse: () -> Unit,
    onStillGoing: () -> Unit
) {
    val theme = LocalTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = theme.buttonPrimary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_favorite),
                contentDescription = null,
                tint = theme.buttonPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = "Are you sure?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "It's okay to have setbacks. We're here to support you through the process, no matter what.",
            fontSize = 16.sp,
            color = theme.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onConfirmRelapse,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.buttonPrimary
            )
        ) {
            Text(
                text = "Confirm Relapse",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        TextButton(
            onClick = onStillGoing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "I'm still going",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textSecondary
            )
        }

        Text(
            text = "Recovery is a journey, not a destination.",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            color = theme.textSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReasonInputContent(
    modifier: Modifier = Modifier,
    relapseReason: String,
    onReasonChange: (String) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    onSkip: () -> Unit
) {
    val theme = LocalTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 32.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = theme.textPrimary
                )
            }
            Text(
                text = "Tell us more (Optional)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Understanding what triggered this can help you in your recovery journey.",
            fontSize = 14.sp,
            color = theme.textSecondary,
            textAlign = TextAlign.Start,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = relapseReason,
            onValueChange = onReasonChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            placeholder = {
                Text(
                    "What triggered this? How were you feeling?",
                    color = theme.textSecondary.copy(alpha = 0.6f)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = theme.surface,
                unfocusedContainerColor = theme.surface,
                focusedTextColor = theme.textPrimary,
                unfocusedTextColor = theme.textPrimary,
                focusedIndicatorColor = theme.buttonPrimary,
                unfocusedIndicatorColor = theme.textSecondary.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.buttonPrimary
            )
        ) {
            Text(
                text = "Submit",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Skip",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Preview
@Composable
private fun PreviewConfirmation() {
    RelapseConfirmationDialog(
        currentStep = RelapseDialogStep.CONFIRMATION,
        relapseReason = "",
        selectedRelapseTime = Clock.System.now(),
        use24HourFormat = true,
        onDismiss = {},
        onConfirmRelapse = {},
        onStillGoing = {},
        onBackToConfirmation = {},
        onBackToDateTime = {},
        onReasonChange = {},
        onSubmitRelapse = {},
        onDateTimeChange = {},
        onNextToReason = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Preview
@Composable
private fun PreviewReasonInput() {
    RelapseConfirmationDialog(
        currentStep = RelapseDialogStep.REASON_INPUT,
        relapseReason = "",
        selectedRelapseTime = Clock.System.now(),
        use24HourFormat = true,
        onDismiss = {},
        onConfirmRelapse = {},
        onStillGoing = {},
        onBackToConfirmation = {},
        onBackToDateTime = {},
        onReasonChange = {},
        onSubmitRelapse = {},
        onDateTimeChange = {},
        onNextToReason = {}
    )
}