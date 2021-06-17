package exercises.android.ronm.findrootsworkmanager

import exercises.android.ronm.findrootsworkmanager.models.Calculation
import org.junit.Assert
import org.junit.Test
import java.util.*

class CalculationTest {

    @Test
    fun when_updatingProgressBelowMaxProgress_then_stateShouldBeCalculating(){
        val calculation = Calculation(UUID.randomUUID(), 5)
        calculation.progress = 50
        Assert.assertTrue(calculation.isCalculating)
    }

    @Test
    fun when_updatingProgressAsMaxProgress_then_stateShouldBeDone(){
        val calculation = Calculation(UUID.randomUUID(), 5)
        calculation.progress = 50
        Assert.assertTrue(calculation.isCalculating)
        calculation.progress = 100
        Assert.assertFalse(calculation.isCalculating)
    }

    @Test
    fun when_rootsAreFound_numberIsPrime_isFalse(){
        val calculation = Calculation(UUID.randomUUID(), 6)
        calculation.root1 = 2L
        calculation.root2 = 3L
        calculation.progress = 100
        Assert.assertFalse(calculation.isNumberPrime)
    }

    @Test
    fun when_rootsAreNotFound_numberIsPrime_isTrue(){
        val calculation = Calculation(UUID.randomUUID(), 7)
        calculation.root1 = 1L
        calculation.root2 = 7L
        calculation.progress = 100
        Assert.assertTrue(calculation.isNumberPrime)
    }


    @Test
    fun testCompareTo_with_twoInProgressCalculations(){
        val calculation1 = Calculation(UUID.randomUUID(), 7)
        val calculation2 = Calculation(UUID.randomUUID(), 9)
        Assert.assertEquals(LESS_THAN, calculation1.compareTo(calculation2))
    }

    @Test
    fun testCompareTo_with_twoDoneCalculations(){
        val calculation1 = Calculation(UUID.randomUUID(), 12)
        calculation1.isCalculating = false
        val calculation2 = Calculation(UUID.randomUUID(), 9)
        calculation2.isCalculating = false
        Assert.assertEquals(GREATER_THAN, calculation1.compareTo(calculation2))
    }

    @Test
    fun testCompareTo_with_oneCalcInProgress_and_oneCalcDone(){
        val calculationDone = Calculation(UUID.randomUUID(), 7)
        calculationDone.isCalculating = false
        val calculationInProgress = Calculation(UUID.randomUUID(), 9)
        Assert.assertEquals(GREATER_THAN, calculationDone.compareTo(calculationInProgress))
    }


    companion object {
        private const val LESS_THAN = -1
        private const val GREATER_THAN = 1
    }

}