package com.example.calorie

import kotlin.math.roundToInt

object CalorieCalculator {
    internal fun roundToFirstDecimal(value: Float): Float {
        // カロリー計算なんで問題ないが負の値の場合ラウンドの挙動に問題のある可能性がある
        // -7.5→-7になるかも
        return (value * 10).roundToInt() / 10f
    }

    fun calculateCalories(intake: MajorNutrientsIntakes): MajorNutrientsCalories {
        return MajorNutrientsCalories(
            protein = calculateCalorie(intake.protein, Nutrient.PROTEIN),
            fat = calculateCalorie(intake.fat, Nutrient.FAT),
            carbohydrate = calculateCalorie(intake.carbohydrate, Nutrient.CARBOHYDRATE)
        )
    }

    private fun calculateCalorie(intake: Float, nutrient: Nutrient): Int {
        val roundedIntake = roundToFirstDecimal(intake)
        return (roundedIntake * nutrient.calorieFactor).roundToInt()
    }
}

enum class Nutrient(val calorieFactor: Float) {
    PROTEIN(4.0f),
    FAT(9.0f),
    CARBOHYDRATE(4.0f)
}

data class MajorNutrientsIntakes(
    val protein: Float,
    val fat: Float,
    val carbohydrate: Float
)

data class MajorNutrientsCalories(
    val protein: Int,
    val fat: Int,
    val carbohydrate: Int
)