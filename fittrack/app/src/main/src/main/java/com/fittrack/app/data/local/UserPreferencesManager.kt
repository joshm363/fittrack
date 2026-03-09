package com.fittrack.app.data.local

import android.content.Context
import com.fittrack.app.domain.model.Equipment
import com.fittrack.app.domain.model.WorkoutStrategy
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("fittrack_prefs", Context.MODE_PRIVATE)

    fun getWorkoutStrategy(): WorkoutStrategy {
        val name = prefs.getString("workout_strategy", null)
        return name?.let {
            try { WorkoutStrategy.valueOf(it) } catch (_: IllegalArgumentException) { null }
        } ?: WorkoutStrategy.PUSH_PULL_LEGS
    }

    fun setWorkoutStrategy(strategy: WorkoutStrategy) {
        prefs.edit().putString("workout_strategy", strategy.name).apply()
    }

    fun getAvailableEquipment(): Set<Equipment> {
        val stored = prefs.getStringSet("available_equipment", null)
            ?: return Equipment.entries.toSet()
        return stored.mapNotNull { name ->
            try { Equipment.valueOf(name) } catch (_: IllegalArgumentException) { null }
        }.toSet()
    }

    fun setAvailableEquipment(equipment: Set<Equipment>) {
        prefs.edit()
            .putStringSet("available_equipment", equipment.map { it.name }.toSet())
            .apply()
    }

    fun hasConfiguredEquipment(): Boolean =
        prefs.contains("available_equipment")

    fun getRestTimerSeconds(): Int =
        prefs.getInt("rest_timer_seconds", 90)

    fun setRestTimerSeconds(seconds: Int) {
        prefs.edit().putInt("rest_timer_seconds", seconds).apply()
    }
}
