package exercises.android.ronm.findrootsworkmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception
import java.util.UUID

class MainActivity : AppCompatActivity(), CalculationDeleteClickListener {

    private lateinit var buttonStartCalculation: FloatingActionButton
    private lateinit var editTextInputNumber: EditText
    private lateinit var appContext : FindRootsApp
    // todo RecyclerView & Adapter

    private val workManager: WorkManager = WorkManager.getInstance(application)
    private var outputWorkInfos: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT) // TODO do I need to update this every time?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get application class
        appContext = applicationContext as FindRootsApp

        // init recycler view and adapter
        val recyclerView : RecyclerView = findViewById(R.id.recyclerCalculationsList)
        val adapter = CalculationsAdapter(appContext.calculationsHolder)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.onCalculationDeleteClickCallback = {id: UUID -> onCalculationDeleteClickCallback(id)}


        // find views and set listeners
        editTextInputNumber = findViewById(R.id.editTextInputNumber)
        buttonStartCalculation = findViewById(R.id.buttonStartCalculation)
        buttonStartCalculation.setOnClickListener { fabStartCalculationClicked()
        adapter.notifyDataSetChanged()}

        workManager.pruneWork() // TODO for debugging, clears the workers queue

        val workInfosObserver = Observer<List<WorkInfo>>{ workInfosList ->
            workInfosList.forEach { workInfo ->
                val id: UUID = workInfo.id
                val number: Long = workInfo.outputData.getLong(KEY_INPUT_NUMBER, 1)
                if (workInfo.state == WorkInfo.State.SUCCEEDED){
                    val root1 = workInfo.outputData.getLong(KEY_RESULT_ROOT1, number)
                    val root2 = workInfo.outputData.getLong(KEY_RESULT_ROOT2, 1L)
                    appContext.calculationsHolder.updateCalculation(id, root1, root2, false)
                    adapter.notifyDataSetChanged()

                }
                // if running, no need to update
                // TODO if failed due to timeout
            }
        }
        // set observer to the live data
        outputWorkInfos.observe(this, workInfosObserver)
    }


    private fun fabStartCalculationClicked() {
        val input = editTextInputNumber.text.toString()
        try {
            val inputNumber: Long = input.toLong()
            if (inputNumber <= 0) {
                Toast.makeText(applicationContext, "Please enter a positive integer", Toast.LENGTH_SHORT).show()
                return
            }
            // else a valid input number, start new calculation
            editTextInputNumber.setText("")
            startCalculation(inputNumber)

        } catch (e: Exception) {
            if (input != "") {
                Toast.makeText(appContext, "Please enter a positive integer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCalculation(number: Long) {
        val data = workDataOf(KEY_INPUT_NUMBER to number)
        val workRequest = OneTimeWorkRequestBuilder<CalculationWorker>().setInputData(data).addTag(TAG_OUTPUT).build()
        appContext.calculationsHolder.addCalculation(workRequest.id, number)
        workManager.enqueue(workRequest)
    }


    override fun onCalculationDeleteClickCallback(id: UUID){
        workManager.cancelWorkById(id)
    }




}


