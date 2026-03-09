package com.fittrack.app.domain.suggestion

import com.fittrack.app.domain.model.ExerciseSet
import kotlin.math.roundToLong

object OneRepMaxCalculator {

    /**
     * Estimates 1RM from the set that produces the highest value using
     * the Epley formula: 1RM = weight × (1 + reps / 30).
     * For single-rep sets, 1RM = weight directly.
     */
    fun estimateFromHistory(sets: List<ExerciseSet>): Double {
        return sets
            .filter { it.weight > 0 && it.reps > 0 }
            .maxOfOrNull { estimateFromSet(it.weight, it.reps) }
            ?: 0.0
    }

    fun estimateFromSet(weight: Double, reps: Int): Double {
        if (reps <= 0 || weight <= 0) return 0.0
        if (reps == 1) return weight
        return weight * (1.0 + reps / 30.0)
    }

    /**
     * Given a 1RM, calculates the appropriate working weight for a
     * target rep count (inverse Epley).
     * Result is rounded to the nearest [increment] (default 5 lbs).
     */
    fun weightForReps(oneRepMax: Double, targetReps: Int, increment: Double = 5.0): Double {
        if (oneRepMax <= 0 || targetReps <= 0) return 0.0
        if (targetReps == 1) return roundTo(oneRepMax, increment)
        val raw = oneRepMax / (1.0 + targetReps / 30.0)
        return roundTo(raw, increment)
    }

    fun roundTo(value: Double, increment: Double): Double {
        if (increment <= 0) return value
        return ((value / increment).roundToLong() * increment)
    }
}
