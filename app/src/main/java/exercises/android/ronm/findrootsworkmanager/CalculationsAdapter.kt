package exercises.android.ronm.findrootsworkmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import exercises.android.ronm.findrootsworkmanager.models.CalculationsHolder
import java.util.UUID

class CalculationsAdapter(private val calculationsHolder: CalculationsHolder) :
    RecyclerView.Adapter<CalculationsAdapter.CalculationViewHolder>() {

    class CalculationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewCalculation: TextView = view.findViewById(R.id.textViewCalculation)
        val buttonRemoveCalculation: ImageButton = view.findViewById(R.id.buttonRemoveCalculation)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarCalculating)
    }

    var onCalculationDeleteClickCallback: ((UUID) -> Unit)? = null

    override fun getItemCount(): Int = calculationsHolder.getCalculationsSize()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.row_calculation, parent, false)
        return CalculationViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CalculationViewHolder, position: Int) {
        val calculation = calculationsHolder.getCalculations()[position]

        // update the current progress of calculation
        viewHolder.progressBar.progress = calculation.progress
        val context = viewHolder.itemView.context
        if (calculation.isCalculating) {
            viewHolder.textViewCalculation.text = context.getString(
                R.string.calculating_roots_for,
                calculation.number
            )  //"Calculating roots for ${calculation.number}"
            // set the button image to 'cancel'
            viewHolder.buttonRemoveCalculation.setImageResource(R.drawable.ic_baseline_cancel_30)
            // update the current progress of calculation
            viewHolder.progressBar.visibility = View.VISIBLE
            viewHolder.progressBar.progress = calculation.progress
        }
        else {
            if (calculation.isNumberPrime) {
                viewHolder.textViewCalculation.text = context.getString(R.string.number_is_prime, calculation.number)
            }
            else {
                viewHolder.textViewCalculation.text = context.getString(R.string.roots_results, calculation.number, calculation.root1, calculation.root2)
            }
            // set the button image to 'delete'
            viewHolder.buttonRemoveCalculation.setImageResource(R.drawable.ic_baseline_delete_30)
            // hide progress bar
            viewHolder.progressBar.visibility = View.INVISIBLE
        }

        viewHolder.buttonRemoveCalculation.setOnClickListener {
            val callback = onCalculationDeleteClickCallback ?: return@setOnClickListener
            callback(calculation.id) // callback to MainActivity to remove the work from WorkManager's queue and calculation from holder
            notifyDataSetChanged()
        }

    }
}