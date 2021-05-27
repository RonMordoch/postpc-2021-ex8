package exercises.android.ronm.findrootsworkmanager

import java.util.UUID

data class Calculation(val id: UUID, val number: Long) : Comparable<Calculation> {

    var root1: Long = number
    var root2: Long = 1L
    var progress: Int = MIN_PROGRESS
    var isNumberPrime: Boolean = false
    var isCalculating: Boolean = true
        set(value) {
            field = value
            if (!value) { // finished calculating, set progress to max
                progress = MAX_PROGRESS
                if (root1 == 1L || root2 == 1L) { // one of the roots found in result is 1, therefore the number is prime
                    isNumberPrime = true
                }
            }
        }


    override fun compareTo(other: Calculation): Int {
        // this object is in-progress, other is done, this object is less than other
        if (isCalculating && !other.isCalculating) {
            return -1
        }
        // this object is done, other is in-progress, other is less than this object
        else if (!isCalculating && other.isCalculating) {
            return 1
        }
        // else, objects are both in-progress or both are done, sort by number from smallest to largest
        return number.compareTo(other.number)
    }

    companion object {
        private const val MAX_PROGRESS = 100
        private const val MIN_PROGRESS = 0
    }
}