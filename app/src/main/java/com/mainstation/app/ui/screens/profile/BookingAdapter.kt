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

import com.mainstation.app.databinding.ItemBookingHistoryBinding

class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(val binding: ItemBookingHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.binding.tvBookingStatus.text = booking.status
        holder.binding.tvBookingTotal.text = "Rp ${java.text.NumberFormat.getIntegerInstance().format(booking.total)}"
        holder.binding.tvBookingId.text = "Booking #${booking.id.takeLast(6).uppercase()}"
        
        val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        holder.binding.tvBookingDate.text = dateFormat.format(booking.startTime)
        
        holder.binding.tvItemName.text = if (booking.consoleId != null) "PlayStation 5" else "VIP Suite"
        
        // Dynamic Status Color (Simplified)
        when (booking.status) {
            "CONFIRMED" -> {
                holder.binding.tvBookingStatus.background.setTint(android.graphics.Color.parseColor("#3B82F6")) // Blue
                holder.binding.viewStatusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#3B82F6"))
            }
            "PENDING" -> {
                holder.binding.tvBookingStatus.background.setTint(android.graphics.Color.parseColor("#EAB308")) // Yellow
                holder.binding.viewStatusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#EAB308"))
            }
            else -> {
                holder.binding.tvBookingStatus.background.setTint(android.graphics.Color.parseColor("#64748B")) // Slate
                holder.binding.viewStatusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#64748B"))
            }
        }
    }

    override fun getItemCount() = bookings.size
}
