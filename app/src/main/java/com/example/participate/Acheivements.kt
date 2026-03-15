package com.example.participate

object AchievementDefinitions {
    val allAchievements = listOf(
        AchievementDef(
            id = "first_project",
            name = "Seedling 🌱",
            description = "Participated in your first project!",
            icon = "🌱",
            threshold = 1
        ),
        AchievementDef(
            id = "five_projects",
            name = "Sprout 🌿",
            description = "Participated in 5 projects",
            icon = "🌿",
            threshold = 5
        ),
        AchievementDef(
            id = "ten_projects",
            name = "Young Tree 🌳",
            description = "Participated in 10 projects",
            icon = "🌳",
            threshold = 10
        ),
        AchievementDef(
            id = "twenty_projects",
            name = "Forest Guardian 🌲",
            description = "Participated in 20 projects!",
            icon = "🌲",
            threshold = 20
        ),
        AchievementDef(
            id = "earth_hero",
            name = "Earth Hero 🌍",
            description = "Participated in 50 projects! You're making a real impact!",
            icon = "🌍",
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