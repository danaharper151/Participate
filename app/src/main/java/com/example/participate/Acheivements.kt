package com.example.participate

object AchievementDefinitions {
    val allAchievements = listOf(
        AchievementDef(
            id = "day_1",
            name = "Sprout",
            description = "Day One! Welcome, Citizen Scientist!!",
            icon = "sprout_award",
            threshold = 1
        ),
        AchievementDef(
            id = "day_2",
            name = "Seedling",
            description = "2 days in a row! Excellent!!",
            icon = "seedling_award",
            threshold = 2
        ),
        AchievementDef(
            id = "day_5",
            name = "Young Tree",
            description = "5 day streak! Nice Work!!",
            icon = "youngtree_award",
            threshold = 5
        ),
        AchievementDef(
            id = "day_10",
            name = "Forest Guardian",
            description = "10 day streak! You're an absolute Marvel!!",
            icon = "forestguardian_award",
            threshold = 10
        ),
        AchievementDef(
            id = "day_20",
            name = "Forest Giant",
            description = "20 day streak! Keep up the good work!!",
            icon = "forestgiant_award",
            threshold = 20
        ),
        AchievementDef(
            id = "day_60",
            name = "Ancient Guardian",
            description = "60 days of science! You're making progress in leaps and bounds!!",
            icon = "ancientguardian_award",
            threshold = 60
        ),
        AchievementDef(
            id = "day_90",
            name = "Sunrise",
            description = "90 days of science! You're a STAR!!",
            icon = "sunrise_award",
            threshold = 90
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