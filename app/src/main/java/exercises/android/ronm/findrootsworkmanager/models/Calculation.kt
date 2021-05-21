package exercises.android.ronm.findrootsworkmanager.models

data class Calculation(
        val number: Int,
        ) {

    var isCalculating : Boolean = false
    var root1 : Int = 1
    var root2 : Int = 1
    var currentProgress : Int = 0


}