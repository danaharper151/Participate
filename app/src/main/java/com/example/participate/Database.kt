package com.example.participate

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Entity: Represents a participated project
@Entity(tableName = "participated_projects")
data class ParticipatedProject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectName: String,
    val projectUrl: String,
    val participationDate: Long, // Timestamp
    val notes: String = ""
)

// Entity: Represents an unlocked achievement
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String, // e.g., "first_project", "five_projects"
    val name: String,
    val description: String,
    val icon: String, // Emoji or resource name
    val unlockedDate: Long,
    val isUnlocked: Boolean = false
)

// DAO: Data Access Object
@Dao
interface ProjectDao {
    @Query("SELECT * FROM participated_projects ORDER BY participationDate DESC")
    fun getAllProjects(): Flow<List<ParticipatedProject>>

    @Query("SELECT COUNT(*) FROM participated_projects")
    suspend fun getProjectCount(): Int

    @Insert
    suspend fun insertProject(project: ParticipatedProject)

    @Delete
    suspend fun deleteProject(project: ParticipatedProject)

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
@Database(entities = [ParticipatedProject::class, Achievement::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "eco_contributor_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
