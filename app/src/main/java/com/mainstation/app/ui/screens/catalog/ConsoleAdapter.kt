package com.mainstation.app.ui.screens.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Console
import com.mainstation.app.util.CurrencyUtils

import com.mainstation.app.databinding.ItemConsoleBinding

class ConsoleAdapter(
    private var consoles: List<Console>,
    private val onConsoleClick: (Console) -> Unit = {}
) : RecyclerView.Adapter<ConsoleAdapter.ConsoleViewHolder>() {

    class ConsoleViewHolder(val binding: ItemConsoleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleViewHolder {
        val binding = ItemConsoleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConsoleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConsoleViewHolder, position: Int) {
        val console = consoles[position]
        holder.binding.tvConsoleName.text = console.name
        holder.binding.tvConsoleType.text = console.type
        holder.binding.tvPrice.text = "${CurrencyUtils.toRupiah(console.pricePerHour)}/hr"
        holder.binding.tvStock.text = "Stock: ${console.stock}"
        holder.binding.tvStatus.text = if(console.stock > 0) "Available" else "Out of Stock"
        holder.binding.tvStatus.setTextColor(if(console.stock > 0) android.graphics.Color.parseColor("#3DDC84") else android.graphics.Color.RED)
        
        // Setup image placeholders
        holder.binding.ivConsole.setImageResource(R.drawable.bg_card) 
        holder.binding.ivConsole.setColorFilter(android.graphics.Color.parseColor("#581C87")) 
        
        if (console.stock > 0) {
            holder.itemView.setOnClickListener { onConsoleClick(console) }
            holder.binding.btnBook.isEnabled = true
            holder.binding.btnBook.alpha = 1.0f
            (holder.binding.btnBook as android.widget.TextView).text = "Book Now"
            holder.binding.btnBook.setOnClickListener { onConsoleClick(console) }
        } else {
            holder.itemView.setOnClickListener { /* No Op */ }
            holder.binding.btnBook.isEnabled = false
            holder.binding.btnBook.alpha = 0.5f
            (holder.binding.btnBook as android.widget.TextView).text = "Out of Stock"
            holder.binding.btnBook.setOnClickListener { /* No Op */ }
        }
    }

    override fun getItemCount() = consoles.size
    
    fun updateData(newConsoles: List<Console>) {
        consoles = newConsoles
        notifyDataSetChanged()
    }
}
