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

class CalculationsAdapter(var calculationsHolder: CalculationsHolder) :
    RecyclerView.Adapter<CalculationsAdapter.CalculationViewHolder>() {

    class CalculationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewCalculation: TextView = view.findViewById(R.id.textViewCalculation)
        val buttonRemoveCalculation: ImageButton = view.findViewById(R.id.buttonRemoveCalculation)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBarCalculating)
    }

    var onCalculationDeleteClickCallback : ((UUID) -> Unit)? = null

    override fun getItemCount(): Int = calculationsHolder.getCalculationsSize()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.row_calculation, parent, false)
        return CalculationViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalculationViewHolder, position: Int) {
        val calculation = calculationsHolder.getCalculations()[position]
        holder.textViewCalculation.text = calculation.number.toString()
        holder.progressBar.visibility = if (calculation.isCalculating) View.VISIBLE else View.INVISIBLE

        if (calculation.isCalculating) { // set the button image to 'cancel'
            holder.buttonRemoveCalculation.setImageResource(R.drawable.ic_baseline_cancel_30)
            holder.textViewCalculation.text = "Calculating roots for ${calculation.number}"
        } else { // set the button image to 'delete'
            holder.buttonRemoveCalculation.setImageResource(R.drawable.ic_baseline_delete_30)
            holder.textViewCalculation.text = "Roots for ${calculation.number}:  ${calculation.root1}x${calculation.root2}"

        }
        holder.buttonRemoveCalculation.setOnClickListener{
            calculationsHolder.deleteCalculation(calculation)
            notifyDataSetChanged()
            val callback = onCalculationDeleteClickCallback?:return@setOnClickListener
            callback(calculation.id)
        }

    }
}