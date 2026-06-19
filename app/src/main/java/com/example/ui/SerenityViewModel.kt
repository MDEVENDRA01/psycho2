package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Reflection
import com.example.data.SerenityRepository
import com.example.data.WellnessGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,       // Daily mood check-in
    INSIGHTS,   // Graphs, Goals, Reflections
    LIBRARY,    // Resource items
    SETTINGS    // Settings (profile)
}

class SerenityViewModel(private val repository: SerenityRepository) : ViewModel() {

    // --- Tab Navigation ---
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    fun navigateTo(tab: ScreenTab) {
        _currentTab.value = tab
    }

    // --- DB State observers ---
    val reflections: StateFlow<List<Reflection>> = repository.allReflections
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val goals: StateFlow<List<WellnessGoal>> = repository.allGoals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Home Screen: Active Check-In state ---
    private val _selectedMood = MutableStateFlow("GOOD") // GREAT, GOOD, OKAY, ANXIOUS, BAD
    val selectedMood: StateFlow<String> = _selectedMood.asStateFlow()

    private val _journalText = MutableStateFlow("")
    val journalText: StateFlow<String> = _journalText.asStateFlow()

    private val _selectedTags = MutableStateFlow(setOf("Family"))
    val selectedTags: StateFlow<Set<String>> = _selectedTags.asStateFlow()

    fun setMood(mood: String) {
        _selectedMood.value = mood
    }

    fun setJournalText(text: String) {
        _journalText.value = text
    }

    fun toggleTag(tag: String) {
        val current = _selectedTags.value.toMutableSet()
        if (current.contains(tag)) {
            current.remove(tag)
        } else {
            current.add(tag)
        }
        _selectedTags.value = current
    }

    fun addCustomTag(tag: String) {
        if (tag.isNotBlank()) {
            val current = _selectedTags.value.toMutableSet()
            current.add(tag.trim())
            _selectedTags.value = current
        }
    }

    fun saveMoodEntry(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val tagsString = _selectedTags.value.joinToString(",")
            val entry = Reflection(
                mood = _selectedMood.value,
                note = _journalText.value.trim(),
                tags = tagsString,
                timestamp = System.currentTimeMillis()
            )
            repository.insertReflection(entry)
            
            // Increment wellness goals optionally or reset check-in text
            _journalText.value = ""
            // Automatically reset check-in selections
            _selectedMood.value = "GOOD"
            _selectedTags.value = setOf("Family")

            onSuccess()
        }
    }

    fun deleteReflection(id: Int) {
        viewModelScope.launch {
            repository.deleteReflection(id)
        }
    }

    // --- Insights Screen: Goals interactions ---
    fun incrementGoal(goalId: String) {
        viewModelScope.launch {
            val currentGoalsList = goals.value
            val targetGoal = currentGoalsList.find { it.id == goalId }
            if (targetGoal != null) {
                val newValue = (targetGoal.currentValue + 1).coerceAtMost(targetGoal.targetValue)
                repository.updateGoalValue(goalId, newValue)
            }
        }
    }

    fun decrementGoal(goalId: String) {
        viewModelScope.launch {
            val currentGoalsList = goals.value
            val targetGoal = currentGoalsList.find { it.id == goalId }
            if (targetGoal != null) {
                val newValue = (targetGoal.currentValue - 1).coerceAtLeast(0)
                repository.updateGoalValue(goalId, newValue)
            }
        }
    }

    fun completePractice(category: String, context: android.content.Context) {
        viewModelScope.launch {
            val goalId = if (category.contains("sleep", ignoreCase = true)) "sleep" else "meditation"
            val currentGoalsList = goals.value
            val targetGoal = currentGoalsList.find { it.id == goalId }
            if (targetGoal != null) {
                val newValue = (targetGoal.currentValue + 1).coerceAtMost(targetGoal.targetValue)
                repository.updateGoalValue(goalId, newValue)
                android.widget.Toast.makeText(
                    context,
                    "Practice completed! Wellness goal for '${targetGoal.title}' progressed to $newValue/${targetGoal.targetValue}.",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } else {
                android.widget.Toast.makeText(context, "Completed practice in: $category", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- Library filtering ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _libraryFilter = MutableStateFlow("All")
    val libraryFilter: StateFlow<String> = _libraryFilter.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLibraryFilter(filter: String) {
        _libraryFilter.value = filter
    }

    // --- Settings / Switches State ---
    private val _pinLockEnabled = MutableStateFlow(false)
    val pinLockEnabled = _pinLockEnabled.asStateFlow()

    private val _encryptionEnabled = MutableStateFlow(true)
    val encryptionEnabled = _encryptionEnabled.asStateFlow()

    private val _morningReminderEnabled = MutableStateFlow(true)
    val morningReminderEnabled = _morningReminderEnabled.asStateFlow()

    private val _eveningReminderEnabled = MutableStateFlow(false)
    val eveningReminderEnabled = _eveningReminderEnabled.asStateFlow()

    fun togglePinLock(enabled: Boolean) { _pinLockEnabled.value = enabled }
    fun toggleEncryption(enabled: Boolean) { _encryptionEnabled.value = enabled }
    fun toggleMorningReminder(enabled: Boolean) { _morningReminderEnabled.value = enabled }
    fun toggleEveningReminder(enabled: Boolean) { _eveningReminderEnabled.value = enabled }
}

class SerenityViewModelFactory(private val repository: SerenityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SerenityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SerenityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
