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

class ConsoleAdapter(
    private var consoles: List<Console>,
    private val onConsoleClick: (Console) -> Unit = {}
) : RecyclerView.Adapter<ConsoleAdapter.ConsoleViewHolder>() {

    class ConsoleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_console_name)
        val type: TextView = view.findViewById(R.id.tv_console_type)
        val price: TextView = view.findViewById(R.id.tv_price)
        val stock: TextView = view.findViewById(R.id.tv_stock)
        val status: TextView = view.findViewById(R.id.tv_status)
        val image: ImageView = view.findViewById(R.id.iv_console)
        val btnBook: View = view.findViewById(R.id.btn_book)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_console, parent, false)
        return ConsoleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsoleViewHolder, position: Int) {
        val console = consoles[position]
        holder.name.text = console.name
        holder.type.text = console.type
        holder.price.text = "${CurrencyUtils.toRupiah(console.pricePerHour)}/hr"
        holder.stock.text = "Stock: ${console.stock}"
        holder.status.text = if(console.stock > 0) "Available" else "Out of Stock"
        holder.status.setTextColor(if(console.stock > 0) android.graphics.Color.parseColor("#3DDC84") else android.graphics.Color.RED)
        
        // Setup image placeholders
        holder.image.setImageResource(R.drawable.bg_card) 
        holder.image.setColorFilter(android.graphics.Color.parseColor("#581C87")) 
        
        if (console.stock > 0) {
            holder.itemView.setOnClickListener { onConsoleClick(console) }
            holder.btnBook.isEnabled = true
            holder.btnBook.alpha = 1.0f
            (holder.btnBook as android.widget.TextView).text = "Book Now"
            holder.btnBook.setOnClickListener { onConsoleClick(console) }
        } else {
            holder.itemView.setOnClickListener { /* No Op */ }
            holder.btnBook.isEnabled = false
            holder.btnBook.alpha = 0.5f
            (holder.btnBook as android.widget.TextView).text = "Out of Stock"
            holder.btnBook.setOnClickListener { /* No Op */ }
        }
    }

    override fun getItemCount() = consoles.size
    
    fun updateData(newConsoles: List<Console>) {
        consoles = newConsoles
        notifyDataSetChanged()
    }
}
