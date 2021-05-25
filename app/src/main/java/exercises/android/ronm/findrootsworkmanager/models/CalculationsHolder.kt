package exercises.android.ronm.findrootsworkmanager.models

import java.util.UUID

class CalculationsHolder {

    private val calculationsList = mutableListOf<Calculation>()

    // return a copy of the list
    fun getCalculations(): MutableList<Calculation> {
        val copy = mutableListOf<Calculation>()
        copy.addAll(calculationsList)
        return copy
    }

    fun getCalculationsSize() = calculationsList.size

    fun addCalculation(calculation: Calculation) {
        calculationsList.add(calculation)
        sortCalculations()
    }

    fun addCalculation(id: UUID, number: Long) {
        calculationsList.add(Calculation(id, number))
        sortCalculations()
    }

    fun deleteCalculation(calculation: Calculation) {
        calculationsList.remove(calculation) // if element is presents removes and returns true, else returns false (no exceptions thrown)
        sortCalculations()

    }

    fun updateCalculation(id: UUID, root1: Long, root2: Long, isCalculating: Boolean) {
        calculationsList.forEach { calculation ->
            if (calculation.id == id) {
                calculation.root1 = root1
                calculation.root2 = root2
                calculation.isCalculating = isCalculating
                return@forEach
            }
        }
        sortCalculations()

    }

    fun updateCalculationProgress(id: UUID, progress: Int) {
        calculationsList.forEach { calculation ->
            if (calculation.id == id) {
                calculation.progress = progress
                return@forEach
            }
        }
    }

    private fun sortCalculations() {
        calculationsList.sort()
    }

}