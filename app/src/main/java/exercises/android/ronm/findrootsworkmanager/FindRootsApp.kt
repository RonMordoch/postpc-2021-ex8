package exercises.android.ronm.findrootsworkmanager

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import exercises.android.ronm.findrootsworkmanager.models.Calculation
import exercises.android.ronm.findrootsworkmanager.models.CalculationsHolder

class FindRootsApp: Application() {

    lateinit var sp: SharedPreferences
    var calculationsHolder: CalculationsHolder = CalculationsHolder()

    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences("worker_sp", Context.MODE_PRIVATE)
    }
}