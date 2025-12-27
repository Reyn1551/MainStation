package com.mainstation.app.ui.screens.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Console

class AdminConsoleAdapter(
    private var consoles: List<Console>,
    private val onEdit: (Console) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<AdminConsoleAdapter.ConsoleViewHolder>() {

    class ConsoleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvDetails: TextView = itemView.findViewById(R.id.tv_details)
        val tvStock: TextView = itemView.findViewById(R.id.tv_stock)
        val btnEdit: View = itemView.findViewById(R.id.btn_edit)
        val btnDelete: View = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_console, parent, false)
        return ConsoleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsoleViewHolder, position: Int) {
        val console = consoles[position]
        holder.tvName.text = console.name
        holder.tvDetails.text = "${console.type} | ${com.mainstation.app.util.CurrencyUtils.toRupiah(console.pricePerHour)}/hr"
        holder.tvStock.text = "Stock: ${console.stock}"
        holder.btnEdit.setOnClickListener { onEdit(console) }
        holder.btnDelete.setOnClickListener { onDelete(console.id) }
    }

    override fun getItemCount() = consoles.size

    fun updateData(newConsoles: List<Console>) {
        consoles = newConsoles
        notifyDataSetChanged()
    }
}
