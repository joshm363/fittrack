package com.fittrack.app.ui.equipment

import androidx.lifecycle.ViewModel
import com.fittrack.app.data.local.UserPreferencesManager
import com.fittrack.app.domain.model.Equipment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class GymPreset(val displayName: String, val description: String) {
    FULL_GYM("Full Gym", "All equipment available"),
    HOME_GYM("Home Gym", "Barbell, dumbbells, bodyweight, bands"),
    MINIMAL("Minimal", "Bodyweight and resistance bands only"),
    CUSTOM("Custom", "Pick your own equipment");

    fun equipmentSet(): Set<Equipment> = when (this) {
        FULL_GYM -> Equipment.entries.toSet()
        HOME_GYM -> setOf(
            Equipment.BARBELL, Equipment.DUMBBELL, Equipment.BODYWEIGHT,
            Equipment.KETTLEBELL, Equipment.BAND
        )
        MINIMAL -> setOf(Equipment.BODYWEIGHT, Equipment.BAND)
        CUSTOM -> emptySet()
    }
}

@HiltViewModel
class EquipmentViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _selectedEquipment = MutableStateFlow(preferencesManager.getAvailableEquipment())
    val selectedEquipment: StateFlow<Set<Equipment>> = _selectedEquipment.asStateFlow()

    private val _activePreset = MutableStateFlow(resolvePreset(preferencesManager.getAvailableEquipment()))
    val activePreset: StateFlow<GymPreset> = _activePreset.asStateFlow()

    fun applyPreset(preset: GymPreset) {
        if (preset == GymPreset.CUSTOM) {
            _activePreset.value = GymPreset.CUSTOM
            return
        }
        val equipment = preset.equipmentSet()
        _selectedEquipment.value = equipment
        _activePreset.value = preset
        preferencesManager.setAvailableEquipment(equipment)
    }

    fun toggleEquipment(equipment: Equipment) {
        _selectedEquipment.update { current ->
            val updated = if (equipment in current) current - equipment else current + equipment
            preferencesManager.setAvailableEquipment(updated)
            _activePreset.value = resolvePreset(updated)
            updated
        }
    }

    private fun resolvePreset(equipment: Set<Equipment>): GymPreset = when (equipment) {
        GymPreset.FULL_GYM.equipmentSet() -> GymPreset.FULL_GYM
        GymPreset.HOME_GYM.equipmentSet() -> GymPreset.HOME_GYM
        GymPreset.MINIMAL.equipmentSet() -> GymPreset.MINIMAL
        else -> GymPreset.CUSTOM
    }
}
