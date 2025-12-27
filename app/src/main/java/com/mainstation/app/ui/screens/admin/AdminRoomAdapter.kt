package com.mainstation.app.ui.screens.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Room

class AdminRoomAdapter(
    private var rooms: List<Room>,
    private val onEdit: (Room) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<AdminRoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvDetails: TextView = itemView.findViewById(R.id.tv_details)
        val btnEdit: View = itemView.findViewById(R.id.btn_edit)
        val btnDelete: View = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.tvName.text = room.name
        holder.tvDetails.text = "Cap: ${room.capacity} | ${com.mainstation.app.util.CurrencyUtils.toRupiah(room.pricePerHour)}/hr"
        holder.btnEdit.setOnClickListener { onEdit(room) }
        holder.btnDelete.setOnClickListener { onDelete(room.id) }
    }

    override fun getItemCount() = rooms.size

    fun updateData(newRooms: List<Room>) {
        rooms = newRooms
        notifyDataSetChanged()
    }
}
