package com.dailyhydrate.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailyhydrate.app.data.WaterEntry
import com.dailyhydrate.app.data.WaterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/** Immutable snapshot rendered by the home screen. */
data class HomeUiState(
    val todayEntries: List<WaterEntry> = emptyList(),
    val totalMl: Int = 0,
    val goalMl: Int = WaterRepository.DEFAULT_GOAL_ML,
    val progress: Float = 0f
) {
    val percentLabel: String get() = "${(progress * 100).toInt()}%"
    val isGoalReached: Boolean get() = totalMl >= goalMl
}

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WaterRepository(application)

    val uiState: StateFlow<HomeUiState> =
        combine(repository.entries, repository.goalMl) { entries, goal ->
            val today = entries
                .filter { isToday(it.timestampMillis) }
                .sortedByDescending { it.timestampMillis }
            val total = today.sumOf { it.amountMl }
            HomeUiState(
                todayEntries = today,
                totalMl = total,
                goalMl = goal,
                progress = if (goal > 0) (total.toFloat() / goal).coerceIn(0f, 1f) else 0f
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun add(amountMl: Int) = viewModelScope.launch { repository.addEntry(amountMl) }

    fun reset() = viewModelScope.launch { repository.reset() }

    fun updateGoal(ml: Int) = viewModelScope.launch { repository.setGoal(ml) }

    private fun isToday(timestampMillis: Long): Boolean {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        return now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
    }
}
