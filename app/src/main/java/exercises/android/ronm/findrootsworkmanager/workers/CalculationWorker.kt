package exercises.android.ronm.findrootsworkmanager.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlin.math.sqrt

const val KEY_INPUT_NUMBER = "input_number"
const val KEY_CURRENT_SAVED_PROGRESS = "saved_progress"
const val KEY_RESULT_ROOT1 = "res_root1"
const val KEY_RESULT_ROOT2 = "res_root2"

class CalculationWorker(appContext: Context,
                        workerParameters: WorkerParameters)
    : Worker(appContext, workerParameters) {

    private val number: Long = inputData.getLong(KEY_INPUT_NUMBER, 1)
    private val savedProgress: Long = inputData.getLong(KEY_CURRENT_SAVED_PROGRESS, DEFAULT_START)

    override fun doWork(): Result {
        // work work work work work work
        val output: Data = calculateRoots()
        return Result.success(output)
    }

    private fun calculateRoots(): Data {
        var root1 = number
        var root2 = 1L
        for (i in savedProgress until sqrt(number.toDouble()).toLong()) {
            if (i % STEP_SIZE_TO_SAVE == 0L) {
                // todo save current progress count
            }
            if (number % i == 0L) { // found roots
                root1 = i
                root2 = number / i
                break
            }
        }
        return workDataOf(KEY_RESULT_ROOT1 to root1,
                KEY_RESULT_ROOT2 to root2)
    }

    companion object {
        private const val STEP_SIZE_TO_SAVE = 10000L
        private const val DEFAULT_START = 2L
    }
}