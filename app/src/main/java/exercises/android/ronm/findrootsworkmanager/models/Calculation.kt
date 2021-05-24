package exercises.android.ronm.findrootsworkmanager.models

import java.util.UUID

data class Calculation(val id: UUID, val number: Long): Comparable<Calculation> {

    var root1: Long = number
    var root2: Long = 1
    var isCalculating: Boolean = true


    override fun compareTo(other: Calculation): Int {
        // this object is in-progress, other is done, this object is less than other
        if (isCalculating && !other.isCalculating){
            return -1
        }
        // this object is done, other is in-progress, other is less than this object
        else if (!isCalculating && other.isCalculating){
            return 1
        }
        // else, objects are both in-progress or both are done, sort by number from smallest to largest
        return number.compareTo(other.number)
    }

}