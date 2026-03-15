package com.example.participate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.projectDao()

    val allProjects = dao.getAllProjects().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val unlockedAchievements = dao.getUnlockedAchievements().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    private val _newAchievement = MutableStateFlow<Achievement?>(null)
    val newAchievement: StateFlow<Achievement?> = _newAchievement.asStateFlow()

    fun recordParticipation(projectName: String, projectUrl: String) {
        viewModelScope.launch {
            // Add project to database
            val project = ParticipatedProject(
                projectName = projectName,
                projectUrl = projectUrl,
                participationDate = System.currentTimeMillis()
            )
            dao.insertProject(project)

            // Show confetti!
            _showConfetti.value = true

            // Check for new achievements
            checkAchievements()
        }
    }

    private suspend fun checkAchievements() {
        val projectCount = dao.getProjectCount()

        AchievementDefinitions.allAchievements.forEach { achievementDef ->
            if (projectCount >= achievementDef.threshold) {
                val existing = dao.getAchievement(achievementDef.id)
                if (existing == null || !existing.isUnlocked) {
                    val achievement = Achievement(
                        id = achievementDef.id,
                        name = achievementDef.name,
                        description = achievementDef.description,
                        icon = achievementDef.icon,
                        unlockedDate = System.currentTimeMillis(),
                        isUnlocked = true
                    )
                    dao.insertAchievement(achievement)
                    _newAchievement.value = achievement
                }
            }
        }
    }

    fun hideConfetti() {
        _showConfetti.value = false
    }

    fun dismissAchievement() {
        _newAchievement.value = null
    }
}