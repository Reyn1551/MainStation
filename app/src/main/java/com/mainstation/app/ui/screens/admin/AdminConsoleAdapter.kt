package com.mainstation.app.ui.screens.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Console

import com.mainstation.app.databinding.ItemAdminConsoleBinding

class AdminConsoleAdapter(
    private var consoles: List<Console>,
    private val onEdit: (Console) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<AdminConsoleAdapter.ConsoleViewHolder>() {

    class ConsoleViewHolder(val binding: ItemAdminConsoleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleViewHolder {
        val binding = ItemAdminConsoleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConsoleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConsoleViewHolder, position: Int) {
        val console = consoles[position]
        holder.binding.tvName.text = console.name
        holder.binding.tvDetails.text = "${console.type} | ${com.mainstation.app.util.CurrencyUtils.toRupiah(console.pricePerHour)}/hr"
        holder.binding.tvStock.text = "Stock: ${console.stock}"
        holder.binding.btnEdit.setOnClickListener { onEdit(console) }
        holder.binding.btnDelete.setOnClickListener { onDelete(console.id) }
    }

    override fun getItemCount() = consoles.size

    fun updateData(newConsoles: List<Console>) {
        consoles = newConsoles
        notifyDataSetChanged()
    }
}
