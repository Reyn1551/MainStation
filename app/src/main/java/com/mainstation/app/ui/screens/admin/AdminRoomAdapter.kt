package com.mainstation.app.ui.screens.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Room

import com.mainstation.app.databinding.ItemAdminRoomBinding

class AdminRoomAdapter(
    private var rooms: List<Room>,
    private val onEdit: (Room) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<AdminRoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(val binding: ItemAdminRoomBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemAdminRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.binding.tvName.text = room.name
        holder.binding.tvDetails.text = "Cap: ${room.capacity} | ${com.mainstation.app.util.CurrencyUtils.toRupiah(room.pricePerHour)}/hr"
        holder.binding.btnEdit.setOnClickListener { onEdit(room) }
        holder.binding.btnDelete.setOnClickListener { onDelete(room.id) }
    }

    override fun getItemCount() = rooms.size

    fun updateData(newRooms: List<Room>) {
        rooms = newRooms
        notifyDataSetChanged()
    }
}
