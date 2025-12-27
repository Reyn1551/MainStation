package com.mainstation.app.ui.screens.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Booking
import java.text.SimpleDateFormat
import java.util.Locale

class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val status: TextView = view.findViewById(R.id.tv_booking_status)
        val total: TextView = view.findViewById(R.id.tv_booking_total)
        val id: TextView = view.findViewById(R.id.tv_booking_id)
        val date: TextView = view.findViewById(R.id.tv_booking_date)
        val itemName: TextView = view.findViewById(R.id.tv_item_name)
        val indicator: View = view.findViewById(R.id.view_status_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_history, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.status.text = booking.status
        holder.total.text = "Rp ${java.text.NumberFormat.getIntegerInstance().format(booking.total)}"
        holder.id.text = "Booking #${booking.id.takeLast(6).uppercase()}"
        
        val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        holder.date.text = dateFormat.format(booking.startTime)
        
        holder.itemName.text = if (booking.consoleId != null) "PlayStation 5" else "VIP Suite"
        
        // Dynamic Status Color (Simplified)
        when (booking.status) {
            "CONFIRMED" -> {
                holder.status.background.setTint(android.graphics.Color.parseColor("#3B82F6")) // Blue
                holder.indicator.setBackgroundColor(android.graphics.Color.parseColor("#3B82F6"))
            }
            "PENDING" -> {
                holder.status.background.setTint(android.graphics.Color.parseColor("#EAB308")) // Yellow
                holder.indicator.setBackgroundColor(android.graphics.Color.parseColor("#EAB308"))
            }
            else -> {
                holder.status.background.setTint(android.graphics.Color.parseColor("#64748B")) // Slate
                holder.indicator.setBackgroundColor(android.graphics.Color.parseColor("#64748B"))
            }
        }
    }

    override fun getItemCount() = bookings.size
}
