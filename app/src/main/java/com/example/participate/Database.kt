package com.example.participate

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Entity: Represents streak data
@Entity(tableName = "streak_data")
data class StreakData(
    @PrimaryKey val id: Int = 1, // Only one row needed
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalDaysOpened: Int = 0,
    val lastOpenedDate: String = "" // Format: "yyyy-MM-dd"
)

// Entity: Represents daily activity (for calendar)
@Entity(tableName = "daily_activity")
data class DailyActivity(
    @PrimaryKey val date: String, // Format: "yyyy-MM-dd"
    val timestamp: Long
)

// Entity: Represents an unlocked achievement
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val unlockedDate: Long,
    val isUnlocked: Boolean = false
)

// DAO: Data Access Object
@Dao
interface AppDao {
    // Streak operations
    @Query("SELECT * FROM streak_data WHERE id = 1")
    suspend fun getStreakData(): StreakData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStreakData(streakData: StreakData)

    @Query("SELECT * FROM streak_data WHERE id = 1")
    fun getStreakDataFlow(): Flow<StreakData?>

    // Daily activity operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyActivity(activity: DailyActivity)

    @Query("SELECT * FROM daily_activity ORDER BY date DESC")
    fun getAllDailyActivities(): Flow<List<DailyActivity>>

    @Query("SELECT COUNT(*) FROM daily_activity")
    suspend fun getTotalDaysCount(): Int

    // Achievement operations
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedDate DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE id = :achievementId")
    suspend fun getAchievement(achievementId: String): Achievement?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Update
    suspend fun updateAchievement(achievement: Achievement)
}

// Database
@Database(
    entities = [StreakData::class, DailyActivity::class, Achievement::class],
    version = 2, // Incremented version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "participate_database"
                )
                    .fallbackToDestructiveMigration() // For development - removes old data
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}