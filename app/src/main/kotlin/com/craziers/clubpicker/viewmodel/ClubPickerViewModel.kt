package com.craziers.clubpicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.craziers.clubpicker.data.Club
import com.craziers.clubpicker.data.ClubRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClubPickerViewModel(repository: ClubRepository) : ViewModel() {
    private val allClubs: List<Club> = repository.getClubs()

    private val _detectedFaces = MutableStateFlow<List<MappedFace>>(emptyList())
    val detectedFaces: StateFlow<List<MappedFace>> = _detectedFaces.asStateFlow()

    private val _assignedClubs = MutableStateFlow<Map<Int, Club>>(emptyMap())
    val assignedClubs: StateFlow<Map<Int, Club>> = _assignedClubs.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _shuffleClubs = MutableStateFlow<Map<Int, Club>>(emptyMap())
    val shuffleClubs: StateFlow<Map<Int, Club>> = _shuffleClubs.asStateFlow()

    private val _history = MutableStateFlow<List<Club>>(emptyList())
    val history: StateFlow<List<Club>> = _history.asStateFlow()

    // Filters
    private val _selectedNations = MutableStateFlow<Set<String>>(emptySet())
    val selectedNations: StateFlow<Set<String>> = _selectedNations.asStateFlow()

    private val _selectedLeagues = MutableStateFlow<Set<String>>(emptySet())
    val selectedLeagues: StateFlow<Set<String>> = _selectedLeagues.asStateFlow()

    private val _selectedStars = MutableStateFlow<Set<Float>>(emptySet())
    val selectedStars: StateFlow<Set<Float>> = _selectedStars.asStateFlow()

    private val _filteredClubs = MutableStateFlow(allClubs)
    val filteredClubs: StateFlow<List<Club>> = _filteredClubs.asStateFlow()

    val availableNations: List<String> = allClubs.map { it.nation }.distinct().sorted()
    
    private val _availableLeagues = MutableStateFlow(allClubs.map { it.league }.distinct().sorted())
    val availableLeagues: StateFlow<List<String>> = _availableLeagues.asStateFlow()
    
    val availableStars: List<Float> = allClubs.map { it.starRating }.distinct().sortedDescending()

    private fun applyFilters() {
        val validLeagues = if (_selectedNations.value.isNotEmpty()) {
            allClubs.filter { it.nation in _selectedNations.value }.map { it.league }.distinct().sorted()
        } else {
            allClubs.map { it.league }.distinct().sorted()
        }
        _availableLeagues.value = validLeagues

        val retainedLeagues = _selectedLeagues.value.intersect(validLeagues.toSet())
        if (retainedLeagues != _selectedLeagues.value) {
            _selectedLeagues.value = retainedLeagues
        }

        var current = allClubs

        if (_selectedNations.value.isNotEmpty()) {
            current = current.filter { it.nation in _selectedNations.value }
        }
        if (_selectedLeagues.value.isNotEmpty()) {
            current = current.filter { it.league in _selectedLeagues.value }
        }
        if (_selectedStars.value.isNotEmpty()) {
            current = current.filter { it.starRating in _selectedStars.value }
        }

        _filteredClubs.value = current
    }

    fun toggleNation(nation: String) {
        _selectedNations.value = if (_selectedNations.value.contains(nation)) {
            _selectedNations.value - nation
        } else {
            _selectedNations.value + nation
        }
        applyFilters()
    }

    fun toggleLeague(league: String) {
        _selectedLeagues.value = if (_selectedLeagues.value.contains(league)) {
            _selectedLeagues.value - league
        } else {
            _selectedLeagues.value + league
        }
        applyFilters()
    }

    fun toggleStar(star: Float) {
        _selectedStars.value = if (_selectedStars.value.contains(star)) {
            _selectedStars.value - star
        } else {
            _selectedStars.value + star
        }
        applyFilters()
    }

    fun onFacesDetected(faces: List<MappedFace>) {
        _detectedFaces.value = faces
    }

    fun clearHistory() {
        _history.value = emptyList()
    }

    fun startSpin() {
        val currentClubs = _filteredClubs.value
        if (_isSpinning.value || currentClubs.isEmpty() || _detectedFaces.value.isEmpty()) return
        
        _isSpinning.value = true
        _assignedClubs.value = emptyMap()
        
        viewModelScope.launch {
            val faces = _detectedFaces.value
            val faceIds = faces.map { it.id }
            
            val spinDurationMs = 2500L
            val intervalMs = 80L
            var elapsedMs = 0L

            while (elapsedMs < spinDurationMs) {
                val currentShuffle = mutableMapOf<Int, Club>()
                for (id in faceIds) {
                    currentShuffle[id] = currentClubs.random()
                }
                _shuffleClubs.value = currentShuffle
                delay(intervalMs)
                elapsedMs += intervalMs
            }

            val finalAssignments = mutableMapOf<Int, Club>()
            val availableClubs = currentClubs.toMutableList()
            for (id in faceIds) {
                if (availableClubs.isEmpty()) break
                val selected = availableClubs.random()
                finalAssignments[id] = selected
                availableClubs.remove(selected)
            }

            _shuffleClubs.value = emptyMap()
            _assignedClubs.value = finalAssignments
            _history.value = finalAssignments.values.toList() + _history.value
            _isSpinning.value = false
        }
    }
}

