package exercises.android.ronm.findrootsworkmanager

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

const val SP_NAME_CALCULATIONS = "sp_calculations_workers"

class FindRootsApp : Application() {

    lateinit var calculationsDatabase: CalculationsDatabase
    lateinit var sp: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sp =  getSharedPreferences(SP_NAME_CALCULATIONS, Context.MODE_PRIVATE)
        // load saved database
        val databaseString = sp.getString(SP_DATABASE_KEY, "")
        calculationsDatabase = if (databaseString == "") CalculationsDatabase() else Gson().fromJson(databaseString, CalculationsDatabase::class.java)
    }

    fun saveDatabaseToSP() {
        val gson = Gson()
        val databaseString = gson.toJson(calculationsDatabase)
        sp.edit().putString(SP_DATABASE_KEY, databaseString).apply()
    }

    companion object {
        private const val SP_DATABASE_KEY = "sp_database_key"
    }
}