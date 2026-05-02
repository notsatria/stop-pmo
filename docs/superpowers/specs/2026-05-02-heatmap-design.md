# Analytics Heatmap Design

**Date:** 2026-05-02
**Feature:** GitHub-like activity heatmap + date filter for the Analytics screen

## Overview

Enhance the Analytics screen with a visual heatmap showing daily streak progress over the past 12 months, and add date filter presets (30d / 90d / 6m / 1y / All) to the existing line chart.

## Decisions

| Decision | Choice | Rationale |
|---|---|---|
| Heatmap data model | Hybrid: clean days + relapse markers | Best visual story — shows both progress and setbacks |
| Time range | Rolling 12 months | Always current, like GitHub's contribution graph |
| Layout | StreakSummary → Heatmap → Filter → Line Chart | Most useful info first |
| Cell interaction | Tooltip on tap showing date and streak count | Adds detail without cluttering the default view |
| Date filter | Preset chips (30d / 90d / 6m / 1y / All) | Quick access, no complex date picker needed |
| Rendering approach | Row + Column composables | Simple, composable, 364 cells is trivial for Compose |
| Filtering approach | In-memory in ViewModel | Small data set, no DAO changes needed |

## Data Model

### New data classes

```kotlin
data class HeatmapDay(
    val date: kotlinx.datetime.LocalDate,
    val streakDays: Int,      // 0 if relapse day or no data
    val isRelapse: Boolean,
)

enum class DateFilter(val label: String, val days: Int?) {
    DAYS_30("30d", 30),
    DAYS_90("90d", 90),
    MONTHS_6("6m", 180),
    YEAR_1("1y", 365),
    ALL("All", null);
}
```

### Updated AnalyticsState

```kotlin
data class AnalyticsState(
    val relapseEvents: List<RelapseEvent> = emptyList(),
    val isLoading: Boolean = true,
    val chartData: List<ChartDataPoint> = emptyList(),
    val streakData: List<StreakData> = emptyList(),
    val heatmapData: List<HeatmapDay> = emptyList(),
    val selectedFilter: DateFilter = DateFilter.ALL,
)
```

No Room schema changes. `RelapseEvent.occurredAt` (ISO 8601 string) is parsed to `LocalDate` for heatmap processing.

## ViewModel Changes

### New function: `processHeatmapData()`

- Takes the full list of `RelapseEvent`s
- Date range: today minus 12 months → today (52-53 weeks)
- Builds a `List<HeatmapDay>` with one entry per day
- For each day: checks if a relapse occurred → `isRelapse = true`
- For streak days: calculates days since last relapse → `streakDays`
- Days before the first relapse get `streakDays = 0`

### New function: `onFilterSelected(filter: DateFilter)`

- Updates `selectedFilter` in state
- Reprocesses chart data only (heatmap unaffected)

### Updated `processChartData()`

- Filters events by the selected date range before mapping to `ChartDataPoint`
- Filter logic: compute cutoff date = now minus `filter.days`, keep events after cutoff

### Data flow

```
allRelapseFlow emits
  → calculateStreakData()           → streakData (always all)
  → processHeatmapData()            → heatmapData (always 12 months)
  → processChartData(selectedFilter) → chartData (filtered)
```

## UI Components

### 1. HeatmapCard

A `Card` (matching existing card style: border + cardContainer background) containing:

```
Column
├── Row: "Activity" title + color legend
├── Month labels row ("Jan", "Feb", ... aligned to first week)
└── LazyRow (horizontal scroll)
    └── Row
        ├── Column: day labels (Mon, Wed, Fri on alternating rows)
        └── For each week (~52 columns):
            └── Column (7 cells, one per day)
                └── Box (14dp × 14dp, 2dp gap)
                    ├── Background color based on streakDays
                    └── If isRelapse: red bg + light red border
```

### 2. Heatmap Color Scale

| Streak Days | Color | Hex |
|---|---|---|
| 0 (no data / reset) | Light gray | `theme.divider` |
| 1-7 | Lightest green | `#bbf7d0` |
| 8-14 | Light green | `#86efac` |
| 15-30 | Medium green | `#4ade80` |
| 31-60 | Dark green | `#22c55e` |
| 60+ | Darkest green | `#166534` |
| Relapse day | Red | `#dc2626` bg + `#fca5a5` border |

### 3. Heatmap Tooltip

- Material 3 `Popup` or `DropdownMenu` anchored to tapped cell
- Content: formatted date, streak count, "Relapse" label if applicable
- Auto-dismisses after 2 seconds or on tap outside

### 4. DateFilterChips

```
Row (horizontalArrangement = spacedBy(8.dp), horizontalScroll)
├── FilterChip("30d", selected, onClick)
├── FilterChip("90d", selected, onClick)
├── FilterChip("6m", selected, onClick)
├── FilterChip("1y", selected, onClick)
└── FilterChip("All", selected, onClick)
```

- Material 3 `FilterChip`
- Selected: `theme.buttonPrimary` container, `theme.textPrimary` text
- Unselected: `theme.cardContainer` container, `theme.divider` border, `theme.textSecondary` text
- Default selection: `ALL`

### 5. Layout

```
Scaffold(topBar = CenterTopBar("Analytics"))
└── Column(fillMaxSize, padding 16dp)
    ├── if loading → LoadingIndicator()
    ├── if empty → EmptyStateMessage()
    └── else → Column(verticalArrangement = spacedBy(16dp))
        ├── StreakSummaryCard(streakData)            ← existing
        ├── HeatmapCard(heatmapData)                 ← new
        ├── DateFilterChips(selectedFilter, onSelect) ← new
        └── RelapseChart(chartData)                  ← existing, filtered
```

### 6. Interaction Rules

- `StreakSummaryCard` — shows stats from ALL relapses, unaffected by date filter
- `HeatmapCard` — always shows rolling 12 months, unaffected by date filter
- `DateFilterChips` — controls `RelapseChart` data only
- `RelapseChart` — data filtered by selected preset before rendering
- Loading state covers everything; empty state shows only if zero relapse events

## Files to Modify

| File | Changes |
|---|---|
| `AnalyticsState.kt` | Add `HeatmapDay`, `DateFilter`, update `AnalyticsState` |
| `AnalyticsViewModel.kt` | Add `processHeatmapData()`, `onFilterSelected()`, update `processChartData()` |
| `AnalyticsScreen.kt` | Add `HeatmapCard`, `DateFilterChips`, update layout |

## Files Unchanged

- `RelapseDao.kt` — no new queries
- `RelapseRepository.kt` / `RelapseRepositoryImpl.kt` — no changes
- `RelapseEventEntity.kt` / `RelapseEvent.kt` — no schema changes
- `AppDatabase.kt` — no migration needed
