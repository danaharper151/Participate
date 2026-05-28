package com.example.participate

object AchievementDefinitions {
    val allAchievements = listOf(
        AchievementDef(
            id = "day_1",
            name = "Sprout",
            description = "Day 1: Welcome Community Scientist!!",
            icon = "sprout_award",
            threshold = 1
        ),
        AchievementDef(
            id = "day_2",
            name = "Seedling",
            description = "2 days in a row: Excellent!!",
            icon = "seedling_award",
            threshold = 2
        ),
        AchievementDef(
            id = "day_7",
            name = "Young Tree",
            description = "7 day streak: Nice Work!!",
            icon = "youngtree_award",
            threshold = 7
        ),
        AchievementDef(
            id = "day_14",
            name = "Forest Guardian",
            description = "14 day streak: Amazing!!",
            icon = "forestguardian_award",
            threshold = 14
        ),
        AchievementDef(
            id = "day_21",
            name = "Forest Giant",
            description = "21 day streak: Keep up the good work!!",
            icon = "forestgiant_award",
            threshold = 21
        ),
        AchievementDef(
            id = "day_28",
            name = "Ancient Guardian",
            description = "28 day streak!! You're an absolute MARVEL!!",
            icon = "ancientguardian_award",
            threshold = 28
        ),
        AchievementDef(
            id = "day_31",
            name = "Sunrise",
            description = "31 days of Community Science, you're a STAR!!",
            icon = "sunrise_award",
            threshold = 31
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