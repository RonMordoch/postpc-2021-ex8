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
import exercises.android.ronm.findrootsworkmanager.workers.*
import java.lang.Exception
import java.util.UUID

class MainActivity : AppCompatActivity(), CalculationDeleteClickListener {

    private lateinit var buttonStartCalculation: FloatingActionButton
    private lateinit var editTextInputNumber: EditText
    private lateinit var appContext: FindRootsApp
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: CalculationsAdapter
    private val workManager: WorkManager = WorkManager.getInstance(application)

    private val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get application class
        appContext = applicationContext as FindRootsApp

        // init recycler view and adapter
        recyclerView = findViewById(R.id.recyclerCalculationsList)
        setRecyclerViewAdapter()

        // find views and set listeners
        editTextInputNumber = findViewById(R.id.editTextInputNumber)
        buttonStartCalculation = findViewById(R.id.buttonStartCalculation)
        buttonStartCalculation.setOnClickListener {
            fabStartCalculationClicked()
            recyclerViewAdapter.notifyDataSetChanged()
        }

        // set observer for all workers
        setWorkersObserver(recyclerViewAdapter)
    }

    private fun setRecyclerViewAdapter() {
        recyclerViewAdapter = CalculationsAdapter(appContext.calculationsDatabase)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = recyclerViewAdapter
        // set adapter cancel/delete button callback
        recyclerViewAdapter.onCalculationDeleteClickCallback = { id: UUID -> onCalculationDeleteClickCallback(id) }
    }


    private fun setWorkersObserver(adapter: CalculationsAdapter) {
        val workInfosObserver = Observer<List<WorkInfo>> { workInfosList ->
            workInfosList.forEach { workInfo ->
                val id: UUID = workInfo.id
                val number: Long = workInfo.outputData.getLong(KEY_INPUT_NUMBER, 1L)
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val root1 = workInfo.outputData.getLong(KEY_RESULT_ROOT1, number)
                        val root2 = workInfo.outputData.getLong(KEY_RESULT_ROOT2, 1L)
                        appContext.calculationsDatabase.updateCalculation(id, root1, root2, false)
                        appContext.saveDatabaseToSP()
                        adapter.notifyDataSetChanged()
                    }
                    WorkInfo.State.RUNNING -> {
                        val progress = workInfo.progress.getInt(CalculationWorker.PROGRESS, 0)
                        appContext.calculationsDatabase.updateCalculationProgress(id, progress)
                        appContext.saveDatabaseToSP()
                        adapter.notifyDataSetChanged()
                    }
                    WorkInfo.State.FAILED -> {
                        // if worker failed it is due to unexpected error as we do not return Result.failure() at all
                        // restart the calculation
                        startCalculation(number)
                    }
                    else -> {
                        // either CANCELED, ENQUEUED or BLOCKED
                        // if CANCELED, don't create/update a calculation class, skip over it
                        // else ENQUEUED or BLOCKED, in OS's hands, do nothing and wait until they resume
                    }
                }
            }
        }
        // make activity observe the changes during its lifecycle
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
            startCalculation(inputNumber)

        } catch (e: Exception) {
            if (input != "") {
                Toast.makeText(appContext, getString(R.string.toast_enter_valid_input_number), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCalculation(number: Long) {
        val data = workDataOf(KEY_INPUT_NUMBER to number)
        val workRequest = OneTimeWorkRequestBuilder<CalculationWorker>().setInputData(data).addTag(TAG_OUTPUT).build()
        appContext.calculationsDatabase.addCalculation(workRequest.id, number)
        appContext.saveDatabaseToSP()
        workManager.enqueue(workRequest)
    }


    override fun onCalculationDeleteClickCallback(id: UUID) {
        appContext.calculationsDatabase.deleteCalculation(id)
        recyclerView.adapter?.notifyDataSetChanged()
        workManager.cancelWorkById(id)
        appContext.saveDatabaseToSP()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_KEY_EDIT_TEXT, editTextInputNumber.text.toString())

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextInputNumber.setText(savedInstanceState.getString(BUNDLE_KEY_EDIT_TEXT))
    }


    companion object {
        private const val BUNDLE_KEY_EDIT_TEXT = "bundle_key_edit_text"
    }

}


