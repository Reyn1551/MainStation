package com.mainstation.app.ui.screens.rooms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Room

import com.mainstation.app.databinding.ItemRoomBinding

class RoomAdapter(private val rooms: List<Room>, private val onRoomClick: (Room) -> Unit) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.binding.tvRoomName.text = room.name
        holder.binding.tvRoomCapacity.text = "Capacity: ${room.capacity} people"
        holder.binding.tvRoomPrice.text = "Rp ${room.pricePerHour}"
        holder.binding.tvFacilities.text = room.facilities.joinToString(", ")
        
        holder.binding.tvRoomTypeBadge.text = if(room.isPrivate) "Private" else "Public"
        // holder.badge.backgroundTint list (dynamic check omitted for brevity)

        holder.binding.ivRoom.setImageResource(R.drawable.bg_card) // Placeholder
        
        holder.binding.btnBookRoom.setOnClickListener {
            onRoomClick(room)
        }
    }

    override fun getItemCount() = rooms.size
}
