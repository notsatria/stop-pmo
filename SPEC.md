# Date/Time Selection for Relapse Logging — Spec

## Context

Currently, when a user logs a relapse, `occurredAt` is always set to `Clock.System.now()` — the exact moment they tap Submit. There's no way to log a relapse that happened earlier (e.g., last night, yesterday). This is unrealistic since users often don't open the app immediately after a relapse. This spec adds a date/time picker step to the `RelapseConfirmationDialog` so users can specify when the relapse actually occurred.

## Overview

- **Flow**: 3-step dialog: CONFIRMATION → DATE_TIME → REASON_INPUT
- **Default**: Pre-fills with current date/time ("now"), user can change or skip
- **Picker**: Material3 `DatePickerDialog` + `TimePickerDialog` (sequential)
- **Constraints**: No future dates allowed, no past limit
- **Precision**: Date + hour + minute (no seconds)
- **Streak**: Calculated from the chosen date, not from "now"
- **Time format**: Respects user's `TIME_FORMAT_24H` DataStore setting
- **Scope**: New relapse logging only — existing relapses are not editable

## Dialog Flow

```
Step 1 — CONFIRMATION (unchanged)
  "Are you sure?"
  [Confirm Relapse] → Step 2  |  [I'm still going] → dismiss

Step 2 — DATE_TIME (new)
  "When did this happen?"
  Tappable row: 📅 icon + formatted date/time + ✏️ edit icon
  Default: current date/time
  Tap → DatePickerDialog → TimePickerDialog (sequential)
  [Next] → Step 3  |  [Back] → Step 1

Step 3 — REASON_INPUT (unchanged)
  "Tell us more (Optional)"
  Text field + [Submit] / [Skip]
```

## UI Implementation

### Step 2 Composable

New composable inside `RelapseConfirmationDialog.kt` (or extracted to a sibling file):

```
Column {
    // Back arrow + "When did this happen?" title (same pattern as Step 3)

    // Tappable date/time row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CalendarMonth, tint = theme.iconPrimary)
        Spacer(Modifier.width(12.dp))
        Text(formattedDateTime, style = bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Default.Edit, tint = theme.textSecondary)
    }

    // Next + Back buttons (same layout as other steps)
}
```

### Material3 Date Picker

```kotlin
if (showDatePicker) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedInstant.toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            // Disable future dates
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= nowMillis
            override fun isSelectableYear(year: Int) = year <= currentYear
        }
    )
    DatePickerDialog(
        onDismissRequest = { showDatePicker = false },
        confirmButton = {
            TextButton(onClick = {
                // Extract selected date, open time picker
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
```

### Material3 Time Picker

```kotlin
if (showTimePicker) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = use24HourFormat  // From SettingsDataStore
    )
    // Use AlertDialog wrapper since Material3 doesn't have TimePickerDialog
    AlertDialog(
        onDismissRequest = { showTimePicker = false },
        confirmButton = {
            TextButton(onClick = {
                // Combine date + time into new Instant
                showTimePicker = false
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
        },
        text = { TimePicker(state = timePickerState) }
    )
}
```

### Date/Time Combination

After both pickers confirm, combine the selected date and time into a single `Instant`:

```kotlin
val selectedInstant = LocalDateTime(
    year, month, dayOfMonth, hour, minute
).toInstant(TimeZone.currentSystemDefault())
```

Store as `selectedInstant` in ViewModel state. Format for display using existing `DateUtils.formatDate(use24Hour)`.

## ViewModel Changes

### DashboardViewModel.kt

Add state for the selected date/time:

```kotlin
var selectedRelapseTime: Instant by mutableStateOf(Clock.System.now())
    private set

fun updateSelectedRelapseTime(instant: Instant) {
    selectedRelapseTime = instant
}
```

Update `submitRelapse()` to use `selectedRelapseTime` instead of `Clock.System.now()`:

```kotlin
fun submitRelapse() {
    viewModelScope.launch {
        val occurredAt = selectedRelapseTime  // Changed from Clock.System.now()
        val streak = (occurredAt - lastRelapseTime).inWholeDays.toInt()
            .coerceAtLeast(0)  // Calculate streak from chosen date
        withContext(Dispatchers.IO) {
            repository.logRelapse(
                occurredAt.toString(),
                streak = streak,
                note = relapseReason.ifBlank { null }
            )
        }
        dismissDialog()
    }
}
```

Update `showConfirmationDialog()` to reset `selectedRelapseTime` to `Clock.System.now()`.

Update `dismissDialog()` to also reset `selectedRelapseTime`.

### RelapseDialogStep enum

```kotlin
enum class RelapseDialogStep {
    CONFIRMATION,
    DATE_TIME,      // New
    REASON_INPUT
}
```

### New ViewModel methods

```kotlin
fun moveToDateTimeStep() { currentDialogStep = RelapseDialogStep.DATE_TIME }
fun moveToReasonStep() { currentDialogStep = RelapseDialogStep.REASON_INPUT }
fun moveBackToDateTime() { currentDialogStep = RelapseDialogStep.DATE_TIME }
```

## Streak Calculation Change

The streak calculation in `submitRelapse()` currently uses `uiState.value.currentStreak` (the live counter). With this change, the streak must be recalculated based on the chosen `selectedRelapseTime`:

```kotlin
// Get the previous relapse time
val lastRelapseTime = repository.lastRelapseTimeFlow().first()

// Calculate streak: days between previous relapse and chosen time
val streak = if (lastRelapseTime != null) {
    (selectedRelapseTime - lastRelapseTime).inWholeDays.toInt().coerceAtLeast(0)
} else {
    0
}
```

This ensures the streak stored on the *previous* relapse record accurately reflects the gap between the two relapses, even if the user picks a past date.

## Dialog Wiring

### DashboardScreen.kt

Update the `DashboardRoute` composable to pass new props:

```kotlin
DashboardScreen(
    // ... existing props ...
    selectedRelapseTime = viewModel.selectedRelapseTime,
    onDateTimeChange = { viewModel.updateSelectedRelapseTime(it) },
    onNavigateToDateTime = { viewModel.moveToDateTimeStep() },
    onBackToDateTime = { viewModel.moveBackToDateTime() },
    timeFormat24H = use24HourFormat,  // from SettingsDataStore
)
```

### RelapseConfirmationDialog

Update the dialog to handle the new step. The `onConfirmRelapse` callback now navigates to DATE_TIME instead of REASON_INPUT. Add the date/time display row and picker dialogs for the DATE_TIME step.

## Files to Modify

| File | Change |
|------|--------|
| `DashboardViewModel.kt` | Add `selectedRelapseTime` state, update `submitRelapse()` to use it, add step navigation methods, recalculate streak from chosen date |
| `RelapseConfirmationDialog.kt` | Add DATE_TIME step with tappable date/time row, DatePickerDialog, TimePickerDialog |
| `DashboardScreen.kt` | Wire new props (selectedRelapseTime, onDateTimeChange, timeFormat24H) through to dialog |
| `DashboardState.kt` | (Optional) Could add `selectedRelapseTime` here instead of ViewModel directly |

## Files NOT Modified

- Entity/DAO/Repository — no schema or query changes. `occurredAt` is already a string, accepts any ISO 8601 value.
- DateUtils — existing `formatDate()` already handles the display format needed.
- Navigation — no new screens or routes.

## Key Design Decisions

1. **No future dates** — `SelectableDates` on `DatePickerState` disables future dates. Prevents illogical entries.
2. **No past limit** — Users can log relapses from any past date. Streak is calculated from the chosen date.
3. **Sequential pickers** — Date first, then time. Matches the mental model ("when" = date first, then time of day).
4. **Streak from chosen date** — `(selectedRelapseTime - previousRelapseTime).inWholeDays` instead of the live counter. Ensures accurate streak bookkeeping.
5. **Defaults to now** — `selectedRelapseTime` initializes to `Clock.System.now()`. User can submit without touching the picker.
6. **24h format** — Uses `SettingsDataStore.timeFormat24HFlow` to set `TimePickerState.is24Hour`.

## Verification

1. **Default flow**: Open dialog → Confirm → see current date/time displayed → Next → reason → Submit. Relapse logged with current timestamp.
2. **Custom date**: Tap the date row → DatePicker opens → select yesterday → TimePicker opens → pick 11:30 PM → Next → Submit. Relapse has yesterday's timestamp.
3. **No future dates**: DatePicker should grey out/disable today+future dates.
4. **Streak recalculation**: If previous relapse was 5 days ago and user picks a date 3 days ago, the streak on the previous relapse should be 3, not 5.
5. **24h format**: If user has 24h enabled, TimePicker shows 24h. If disabled, shows 12h with AM/PM.
6. **Back navigation**: From DATE_TIME step, back arrow goes to CONFIRMATION. From REASON_INPUT, back goes to DATE_TIME.
7. **Display format**: Date/time row shows formatted like "01 May 2026 at 14:30" (or "01 May 2026 at 02:30 PM" in 12h mode).
8. **Build**: `./gradlew assembleDebug` passes.
