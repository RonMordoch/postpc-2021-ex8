package exercises.android.ronm.findrootsworkmanager.workers

import android.content.Context

import androidx.work.*
import exercises.android.ronm.findrootsworkmanager.FindRootsApp
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

const val KEY_INPUT_NUMBER = "input_number"
const val KEY_RESULT_ROOT1 = "res_root1"
const val KEY_RESULT_ROOT2 = "res_root2"
const val TAG_OUTPUT = "OUTPUT_WORKERS"

class CalculationWorker(appContext: Context, workerParameters: WorkerParameters) : CoroutineWorker(appContext, workerParameters) {

    private val number: Long = inputData.getLong(KEY_INPUT_NUMBER, 1L)

    // immediately save number to output data so we can update progress outside properly
    private val outputBuilder = Data.Builder()
    init {
        outputBuilder.putLong(KEY_INPUT_NUMBER, number)
    }


    override suspend fun doWork(): Result {

        val startTime: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
        // look up in shared-preferences if we have any saved progress and start from there , else from 2
        val sp = (applicationContext as FindRootsApp).sp
        val start: Long = sp.getLong(number.toString(), DEFAULT_START)
        // initialize results variables, default them to prime-number results
        var root1 = number
        var root2 = 1L
        for (i in start until number) {

            // calculate and save current progress to worker
            val progress: Int = (((i.toDouble() / number.toDouble()) * 100).toInt())
            // show progress only every K steps, to avoid many ui-refreshes
            // note this condition is optional and we can update it without the if-condition
            if (progress % PROGRESS_STEPS == 0){
                setProgress(workDataOf(PROGRESS to progress))
            }
            // store the current progress every X amount of steps
            if (i % STEP_SIZE_TO_SAVE == 0L) {
                sp.edit().putLong(number.toString(), i).apply()
            }
            // if our set maximum running time in minutes passed , retry
            val currentTime: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
            if (Duration.between(startTime, currentTime).toMinutes() >= MAX_RUNNING_TIME_MINUTES) {
                // save current progress
                sp.edit().putLong(number.toString(), i).apply()
                return Result.retry()
            }

            if (number % i == 0L) { // found roots
                root1 = i
                root2 = number / i
                break
            }
        }
        // put roots result to output
        outputBuilder.putAll(
            workDataOf(
                KEY_RESULT_ROOT1 to root1,
                KEY_RESULT_ROOT2 to root2
            )
        )
        // save the roots to the output data
        val outputData = outputBuilder.build()
        // remove the saved progress to avoid having shared-preferences over-filled with data
        sp.edit().remove(number.toString()).apply()
        return Result.success(outputData)
    }


    companion object {
        private const val STEP_SIZE_TO_SAVE = 20000L
        private const val MAX_RUNNING_TIME_MINUTES = 8
        private const val PROGRESS_STEPS = 2
        private const val DEFAULT_START = 2L
        const val PROGRESS = "Progress"
    }
}