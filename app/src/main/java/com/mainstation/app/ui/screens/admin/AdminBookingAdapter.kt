package com.mainstation.app.ui.screens.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Booking
import java.text.SimpleDateFormat
import java.util.Locale

import com.mainstation.app.databinding.ItemAdminBookingBinding

class AdminBookingAdapter(
    private var bookings: List<Booking>,
    private val onApprove: (String) -> Unit,
    private val onReject: (String) -> Unit
) : RecyclerView.Adapter<AdminBookingAdapter.AdminBookingViewHolder>() {

    class AdminBookingViewHolder(val binding: ItemAdminBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBookingViewHolder {
        val binding = ItemAdminBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminBookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminBookingViewHolder, position: Int) {
        val booking = bookings[position]
        
        holder.binding.tvConsoleName.text = "${booking.location} - ${booking.consoleId ?: booking.roomId ?: "Item"}"
        holder.binding.tvStatus.text = booking.status
        holder.binding.tvPrice.text = com.mainstation.app.util.CurrencyUtils.toRupiah(booking.total)
        
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.binding.tvDate.text = "${dateFormat.format(booking.startTime)} (${booking.duration} Hours)"

        when (booking.status) {
            "PENDING" -> {
                holder.binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FACC15")) // Yellow
                holder.binding.layoutActions.visibility = View.VISIBLE
            }
            "APPROVED" -> {
                holder.binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#22C55E")) // Green
                holder.binding.layoutActions.visibility = View.GONE
            }
            "REJECTED" -> {
                holder.binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#EF4444")) // Red
                holder.binding.layoutActions.visibility = View.GONE
            }
            else -> {
                holder.binding.tvStatus.setTextColor(android.graphics.Color.WHITE)
                holder.binding.layoutActions.visibility = View.GONE
            }
        }

        holder.binding.btnApprove.setOnClickListener { onApprove(booking.id) }
        holder.binding.btnReject.setOnClickListener { onReject(booking.id) }
    }

    override fun getItemCount() = bookings.size
    
    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
