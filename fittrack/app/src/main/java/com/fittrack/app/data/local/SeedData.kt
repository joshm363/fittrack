package com.fittrack.app.data.local

import com.fittrack.app.data.local.entity.ExerciseEntity
import com.fittrack.app.domain.model.Equipment
import com.fittrack.app.domain.model.MuscleGroup

object SeedData {
    fun getDefaultExercises(): List<ExerciseEntity> = listOf(
        // Chest
        ExerciseEntity(name = "Bench Press", muscleGroup = MuscleGroup.CHEST.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Incline Dumbbell Press", muscleGroup = MuscleGroup.CHEST.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        ExerciseEntity(name = "Cable Fly", muscleGroup = MuscleGroup.CHEST.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Decline Bench Press", muscleGroup = MuscleGroup.CHEST.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Push-Up", muscleGroup = MuscleGroup.CHEST.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        ExerciseEntity(name = "Pec Deck Fly", muscleGroup = MuscleGroup.CHEST.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Dumbbell Fly", muscleGroup = MuscleGroup.CHEST.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        // Back
        ExerciseEntity(name = "Barbell Row", muscleGroup = MuscleGroup.BACK.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Lat Pulldown", muscleGroup = MuscleGroup.BACK.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Seated Cable Row", muscleGroup = MuscleGroup.BACK.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Pull-up", muscleGroup = MuscleGroup.BACK.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        ExerciseEntity(name = "Dumbbell Row", muscleGroup = MuscleGroup.BACK.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        ExerciseEntity(name = "T-Bar Row", muscleGroup = MuscleGroup.BACK.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Face Pull", muscleGroup = MuscleGroup.BACK.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Straight Arm Pulldown", muscleGroup = MuscleGroup.BACK.name, equipment = Equipment.CABLE.name, isCustom = false),
        // Shoulders
        ExerciseEntity(name = "Overhead Press", muscleGroup = MuscleGroup.SHOULDERS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Lateral Raise", muscleGroup = MuscleGroup.SHOULDERS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        ExerciseEntity(name = "Face Pull", muscleGroup = MuscleGroup.SHOULDERS.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Dumbbell Shoulder Press", muscleGroup = MuscleGroup.SHOULDERS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        ExerciseEntity(name = "Cable Lateral Raise", muscleGroup = MuscleGroup.SHOULDERS.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Reverse Pec Deck", muscleGroup = MuscleGroup.SHOULDERS.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Arnold Press", muscleGroup = MuscleGroup.SHOULDERS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        ExerciseEntity(name = "Front Raise", muscleGroup = MuscleGroup.SHOULDERS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        // Biceps
        ExerciseEntity(name = "Barbell Curl", muscleGroup = MuscleGroup.BICEPS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Dumbbell Curl", muscleGroup = MuscleGroup.BICEPS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        ExerciseEntity(name = "Hammer Curl", muscleGroup = MuscleGroup.BICEPS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        ExerciseEntity(name = "Cable Curl", muscleGroup = MuscleGroup.BICEPS.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Preacher Curl", muscleGroup = MuscleGroup.BICEPS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Concentration Curl", muscleGroup = MuscleGroup.BICEPS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        // Triceps
        ExerciseEntity(name = "Tricep Pushdown", muscleGroup = MuscleGroup.TRICEPS.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Skull Crushers", muscleGroup = MuscleGroup.TRICEPS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Overhead Tricep Extension", muscleGroup = MuscleGroup.TRICEPS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        ExerciseEntity(name = "Close Grip Bench Press", muscleGroup = MuscleGroup.TRICEPS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Dips", muscleGroup = MuscleGroup.TRICEPS.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        ExerciseEntity(name = "Tricep Kickback", muscleGroup = MuscleGroup.TRICEPS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        // Quads
        ExerciseEntity(name = "Barbell Squat", muscleGroup = MuscleGroup.QUADS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Leg Press", muscleGroup = MuscleGroup.QUADS.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Leg Extension", muscleGroup = MuscleGroup.QUADS.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Front Squat", muscleGroup = MuscleGroup.QUADS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Hack Squat", muscleGroup = MuscleGroup.QUADS.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Lunges", muscleGroup = MuscleGroup.QUADS.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        // Hamstrings
        ExerciseEntity(name = "Romanian Deadlift", muscleGroup = MuscleGroup.HAMSTRINGS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Leg Curl", muscleGroup = MuscleGroup.HAMSTRINGS.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Stiff Leg Deadlift", muscleGroup = MuscleGroup.HAMSTRINGS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Good Morning", muscleGroup = MuscleGroup.HAMSTRINGS.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Nordic Hamstring Curl", muscleGroup = MuscleGroup.HAMSTRINGS.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        // Glutes
        ExerciseEntity(name = "Hip Thrust", muscleGroup = MuscleGroup.GLUTES.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Cable Kickback", muscleGroup = MuscleGroup.GLUTES.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Glute Bridge", muscleGroup = MuscleGroup.GLUTES.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Sumo Squat", muscleGroup = MuscleGroup.GLUTES.name, equipment = Equipment.BARBELL.name, isCustom = false),
        ExerciseEntity(name = "Bulgarian Split Squat", muscleGroup = MuscleGroup.GLUTES.name, equipment = Equipment.DUMBBELL.name, isCustom = false),
        // Core
        ExerciseEntity(name = "Plank", muscleGroup = MuscleGroup.CORE.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        ExerciseEntity(name = "Cable Crunch", muscleGroup = MuscleGroup.CORE.name, equipment = Equipment.CABLE.name, isCustom = false),
        ExerciseEntity(name = "Hanging Leg Raise", muscleGroup = MuscleGroup.CORE.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        ExerciseEntity(name = "Crunch", muscleGroup = MuscleGroup.CORE.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        ExerciseEntity(name = "Russian Twist", muscleGroup = MuscleGroup.CORE.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        ExerciseEntity(name = "Dead Bug", muscleGroup = MuscleGroup.CORE.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
        ExerciseEntity(name = "Ab Wheel Rollout", muscleGroup = MuscleGroup.CORE.name, equipment = Equipment.OTHER.name, isCustom = false),
        // Calves
        ExerciseEntity(name = "Calf Raise", muscleGroup = MuscleGroup.CALVES.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Standing Calf Raise", muscleGroup = MuscleGroup.CALVES.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Seated Calf Raise", muscleGroup = MuscleGroup.CALVES.name, equipment = Equipment.MACHINE.name, isCustom = false),
        ExerciseEntity(name = "Donkey Calf Raise", muscleGroup = MuscleGroup.CALVES.name, equipment = Equipment.BODYWEIGHT.name, isCustom = false),
    )
}
