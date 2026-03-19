package com.example.participate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.appDao()

    val streakData = dao.getStreakDataFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val dailyActivities = dao.getAllDailyActivities().stateIn(
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

    init {
        // Check streak on app startup
        viewModelScope.launch {
            checkAndUpdateStreak()
        }
    }

    private suspend fun checkAndUpdateStreak() {
        val today = getTodayDateString()
        val currentData = dao.getStreakData()

        if (currentData == null) {
            // First time opening app - Day 1!
            val newData = StreakData(
                currentStreak = 1,
                longestStreak = 1,
                totalDaysOpened = 1,
                lastOpenedDate = today
            )
            dao.updateStreakData(newData)
            dao.insertDailyActivity(DailyActivity(today, System.currentTimeMillis()))

            // Show confetti for first day!
            _showConfetti.value = true

            // Check for Day 1 achievement
            checkAchievements(1)
        } else {
            // Check if this is a new day
            if (currentData.lastOpenedDate != today) {
                val yesterday = getYesterdayDateString()

                val newStreak = if (currentData.lastOpenedDate == yesterday) {
                    // Consecutive day - increment streak
                    currentData.currentStreak + 1
                } else {
                    // Streak broken - reset to 1
                    1
                }

                val newLongest = maxOf(newStreak, currentData.longestStreak)

                val updatedData = currentData.copy(
                    currentStreak = newStreak,
                    longestStreak = newLongest,
                    totalDaysOpened = currentData.totalDaysOpened + 1,
                    lastOpenedDate = today
                )

                dao.updateStreakData(updatedData)
                dao.insertDailyActivity(DailyActivity(today, System.currentTimeMillis()))

                // Show confetti for new day!
                _showConfetti.value = true

                // Check for achievements
                checkAchievements(newStreak)
            }
            // If same day, do nothing
        }
    }

    private suspend fun checkAchievements(currentStreak: Int) {
        AchievementDefinitions.allAchievements.forEach { achievementDef ->
            if (currentStreak >= achievementDef.threshold) {
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

    fun triggerConfetti() {
        _showConfetti.value = true
    }

    fun hideConfetti() {
        _showConfetti.value = false
    }

    fun dismissAchievement() {
        _newAchievement.value = null
    }

    private fun getTodayDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}