package exercises.android.ronm.findrootsworkmanager

import android.content.Context
import android.os.Handler
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlin.math.sqrt

const val KEY_INPUT_NUMBER = "input_number"
const val KEY_RESULT_ROOT1 = "res_root1"
const val KEY_RESULT_ROOT2 = "res_root2"
const val TAG_OUTPUT = "OUTPUT"

class CalculationWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : Worker(appContext, workerParameters) {

    private val number: Long = inputData.getLong(KEY_INPUT_NUMBER, 1)

    // save number to output data
    private val outputBuilder = Data.Builder()

    init {
        outputBuilder.putLong(KEY_INPUT_NUMBER, number)
    }

    override fun doWork(): Result {
        // look up in shared-preferences if we have any saved progress and start from there , else from 2

        val sp = (applicationContext as FindRootsApp).sp
        val start: Long = sp.getLong(number.toString(), DEFAULT_START)
        // initialize results variables, default them to prime-number results
        var root1 = number
        var root2 = 1L
        for (i in start until number) { //until sqrt(number.toDouble()).toLong()

            // store the current progress every X amount of steps
            if (i % STEP_SIZE_TO_SAVE == 0L) {
                sp.edit().putLong(number.toString(), i).apply()
            }
            if (number % i == 0L) { // found roots
                root1 = i
                root2 = number / i
                break
            }
        }
        outputBuilder.putAll(
            workDataOf(
                KEY_RESULT_ROOT1 to root1,
                KEY_RESULT_ROOT2 to root2
            )
        )
        // save the roots to the output data
        val outputData = outputBuilder.build()
        return Result.success(outputData)
    }

    companion object {
        private const val STEP_SIZE_TO_SAVE = 20000L
        private const val DEFAULT_START = 2L
    }
}