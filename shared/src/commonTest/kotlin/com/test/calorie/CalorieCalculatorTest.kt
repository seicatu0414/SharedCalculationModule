package com.test.calorie


import com.example.calorie.CalorieCalculator
import com.example.calorie.MajorNutrientsIntakes
import kotlin.test.Test
import kotlin.test.assertEquals


class CalorieCalculatorTest {

    private val calculator = CalorieCalculator

    @Test
    fun testRoundToFirstDecimal() {
        // 小数第2位を四捨五入して小数第1位にすることを確認
        assertEquals(12.3f, calculator.roundToFirstDecimal(12.34f))
        assertEquals(12.4f, calculator.roundToFirstDecimal(12.36f))
        assertEquals(0.0f, calculator.roundToFirstDecimal(0.04f))
    }

    @Test
    fun testCalculateCalories() {
        val intake = MajorNutrientsIntakes(protein = 12.3f, fat = 8.6f, carbohydrate = 50.4f)
        val result = calculator.calculateCalories(intake)

        // 期待されるカロリー値
        assertEquals(49, result.protein)
        assertEquals(77, result.fat)
        assertEquals(202, result.carbohydrate)
    }
}

