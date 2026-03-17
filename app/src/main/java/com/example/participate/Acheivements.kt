package com.example.participate

object AchievementDefinitions {
    val allAchievements = listOf(
        AchievementDef(
            id = "first_project",
            name = "Seedling",
            description = "Joined your first project! Excellent!!",
            icon = "seedling", // Now references drawable resource
            threshold = 1
        ),
        AchievementDef(
            id = "five_projects",
            name = "Sprout",
            description = "Joined 5 projects. Keep up the good work!!",
            icon = "sprout",
            threshold = 5
        ),
        AchievementDef(
            id = "ten_projects",
            name = "Young Tree",
            description = "Joined 10 projects! Amazing!!",
            icon = "youngtree",
            threshold = 10
        ),
        AchievementDef(
            id = "twenty_projects",
            name = "Forest Guardian",
            description = "Joined 20 projects! Nice work!!",
            icon = "forestguardian",
            threshold = 20
        ),
        AchievementDef(
            id = "earth_hero",
            name = "Giant Sequoia",
            description = "50 acts of science! You're a star!!",
            icon = "gianttree",
            threshold = 50
        )
    )
}

data class AchievementDef(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val threshold: Int
)