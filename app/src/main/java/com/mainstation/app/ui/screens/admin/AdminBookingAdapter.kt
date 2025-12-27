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

class AdminBookingAdapter(
    private var bookings: List<Booking>,
    private val onApprove: (String) -> Unit,
    private val onReject: (String) -> Unit
) : RecyclerView.Adapter<AdminBookingAdapter.AdminBookingViewHolder>() {

    class AdminBookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvConsoleName: TextView = itemView.findViewById(R.id.tv_console_name)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val btnApprove: View = itemView.findViewById(R.id.btn_approve)
        val btnReject: View = itemView.findViewById(R.id.btn_reject)
        val layoutActions: View = itemView.findViewById(R.id.layout_actions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_booking, parent, false)
        return AdminBookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminBookingViewHolder, position: Int) {
        val booking = bookings[position]
        
        holder.tvConsoleName.text = "${booking.location} - ${booking.consoleId ?: booking.roomId ?: "Item"}"
        holder.tvStatus.text = booking.status
        holder.tvPrice.text = com.mainstation.app.util.CurrencyUtils.toRupiah(booking.total)
        
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.tvDate.text = "${dateFormat.format(booking.startTime)} (${booking.duration} Hours)"

        when (booking.status) {
            "PENDING" -> {
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FACC15")) // Yellow
                holder.layoutActions.visibility = View.VISIBLE
            }
            "APPROVED" -> {
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#22C55E")) // Green
                holder.layoutActions.visibility = View.GONE
            }
            "REJECTED" -> {
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#EF4444")) // Red
                holder.layoutActions.visibility = View.GONE
            }
            else -> {
                holder.tvStatus.setTextColor(android.graphics.Color.WHITE)
                holder.layoutActions.visibility = View.GONE
            }
        }

        holder.btnApprove.setOnClickListener { onApprove(booking.id) }
        holder.btnReject.setOnClickListener { onReject(booking.id) }
    }

    override fun getItemCount() = bookings.size
    
    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
