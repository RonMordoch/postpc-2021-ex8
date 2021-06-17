package exercises.android.ronm.findrootsworkmanager.models

import java.util.UUID

class CalculationsDatabase {

    private val calculationsHashMap = mutableMapOf<UUID, Calculation>()

    // return a sorted copy of the values
    fun getCalculations(): MutableList<Calculation> {
        val values = calculationsHashMap.values.toMutableList()
        values.sort()
        return values
    }

    fun getCalculationsSize() = calculationsHashMap.values.size

    fun addCalculation(id: UUID, number: Long) {
        calculationsHashMap[id] = Calculation(id, number)
    }

    fun deleteCalculation(id: UUID) {
        calculationsHashMap.remove(id) // returns null if key is present in map, no exceptions thrown
    }

    fun updateCalculation(id: UUID, root1: Long, root2: Long, isCalculating: Boolean) {
        if (calculationsHashMap.containsKey(id)) {
            val calculation = calculationsHashMap[id]
            calculation?.root1 = root1
            calculation?.root2 = root2
            calculation?.isCalculating = isCalculating
        }
    }

    fun updateCalculationProgress(id: UUID, progress: Int) {
        if (calculationsHashMap.containsKey(id)) {
            calculationsHashMap[id]?.progress = progress
        }
    }


}