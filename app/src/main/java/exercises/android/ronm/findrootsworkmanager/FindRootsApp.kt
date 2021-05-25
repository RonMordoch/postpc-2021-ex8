package exercises.android.ronm.findrootsworkmanager

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import exercises.android.ronm.findrootsworkmanager.models.Calculation
import exercises.android.ronm.findrootsworkmanager.models.CalculationsHolder

const val SP_NAME_WORKERS = "workers_sp"

class FindRootsApp : Application() {

    lateinit var sp: SharedPreferences
    var calculationsHolder: CalculationsHolder = CalculationsHolder()

    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences(SP_NAME_WORKERS, Context.MODE_PRIVATE)
        sp.edit().clear().apply() // TODO for DEBUG
    }
}