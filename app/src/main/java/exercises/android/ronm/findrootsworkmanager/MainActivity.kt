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
    private lateinit var appContext: FindRootsApp
    private lateinit var recyclerView: RecyclerView
    private val workManager: WorkManager = WorkManager.getInstance(application)

    private var outputWorkInfos: LiveData<List<WorkInfo>>

    init {
        workManager.cancelAllWorkByTag(TAG_OUTPUT)  // TODO for debugging, clears the workers queue
        workManager.pruneWork() // TODO for debugging, clears the workers queue from workers that are SUCCEEDED/FAILED/CANCELLED
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get application class
        appContext = applicationContext as FindRootsApp

        // init recycler view and adapter
        recyclerView = findViewById(R.id.recyclerCalculationsList)
        val adapter = CalculationsAdapter(appContext.calculationsHolder)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.onCalculationDeleteClickCallback = { id: UUID -> onCalculationDeleteClickCallback(id) }


        // find views and set listeners
        editTextInputNumber = findViewById(R.id.editTextInputNumber)
        buttonStartCalculation = findViewById(R.id.buttonStartCalculation)
        buttonStartCalculation.setOnClickListener {
            fabStartCalculationClicked()
            adapter.notifyDataSetChanged()
        }


        val workInfosObserver = Observer<List<WorkInfo>> { workInfosList ->
            workInfosList.forEach { workInfo ->
                val id: UUID = workInfo.id
                val number: Long = workInfo.outputData.getLong(KEY_INPUT_NUMBER, 1)
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val root1 = workInfo.outputData.getLong(KEY_RESULT_ROOT1, number)
                        val root2 = workInfo.outputData.getLong(KEY_RESULT_ROOT2, 1L)
                        appContext.calculationsHolder.updateCalculation(id, root1, root2, false)
                        adapter.notifyDataSetChanged()
                    }
                    WorkInfo.State.RUNNING -> {
                        val progress = workInfo.progress.getInt(CalculationWorker.PROGRESS, 0)
                        appContext.calculationsHolder.updateCalculationProgress(id, progress)
                        adapter.notifyDataSetChanged()
                    }
                    WorkInfo.State.FAILED -> {
                        // if worker failed it is due to unexpected error as we do not return Result.failure() at all
                        // cancel the failed worker to remove it from the queue and restart it, don't delete matching calculation object
                        workManager.cancelWorkById(id)
                        startCalculation(number, false)
                    }
                    else -> {
                        // either CANCELED, ENQUEUED or BLOCKED, in OS's hands, do nothing and wait until they resume
                        // if canceled, don't create/update a calculation class, skip over it
                        // else ENQUEUED or BLOCKED, in OS's hands, do nothing and wait until they resume
                    }
                }
            }
        }
        // set the observer to the live data
        outputWorkInfos.observe(this, workInfosObserver)
    }


    private fun fabStartCalculationClicked() {
        val input = editTextInputNumber.text.toString()
        try {
            val inputNumber: Long = input.toLong()
            if (inputNumber <= 0) {
                Toast.makeText(applicationContext, getString(R.string.toast_enter_valid_input_number), Toast.LENGTH_SHORT).show()
                return
            }
            // else a valid input number, start new calculation
            editTextInputNumber.setText("")
            startCalculation(inputNumber, true)

        } catch (e: Exception) {
            if (input != "") {
                Toast.makeText(appContext, getString(R.string.toast_enter_valid_input_number), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCalculation(number: Long, isNew: Boolean) {
        val data = workDataOf(KEY_INPUT_NUMBER to number)
        val workRequest = OneTimeWorkRequestBuilder<CalculationWorker>().setInputData(data).addTag(TAG_OUTPUT).build()
        if (isNew) {
            // if new calculation and not from re-creating a worker due to failed state
            appContext.calculationsHolder.addCalculation(workRequest.id, number)
        }
        workManager.enqueue(workRequest)
    }


    override fun onCalculationDeleteClickCallback(id: UUID) {
        workManager.cancelWorkById(id)
    }


}


