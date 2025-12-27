package com.mainstation.app.ui.screens.rooms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mainstation.app.R
import com.mainstation.app.data.model.Room

class RoomAdapter(private val rooms: List<Room>, private val onRoomClick: (Room) -> Unit) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_room_name)
        val capacity: TextView = view.findViewById(R.id.tv_room_capacity)
        val price: TextView = view.findViewById(R.id.tv_room_price)
        val facilities: TextView = view.findViewById(R.id.tv_facilities)
        val badge: TextView = view.findViewById(R.id.tv_room_type_badge)
        val image: ImageView = view.findViewById(R.id.iv_room)
        val bookButton: View = view.findViewById(R.id.btn_book_room)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.name.text = room.name
        holder.capacity.text = "Capacity: ${room.capacity} people"
        holder.price.text = "Rp ${room.pricePerHour}"
        holder.facilities.text = room.facilities.joinToString(", ")
        
        holder.badge.text = if(room.isPrivate) "Private" else "Public"
        // holder.badge.backgroundTint list (dynamic check omitted for brevity)

        holder.image.setImageResource(R.drawable.bg_card) // Placeholder
        
        holder.bookButton.setOnClickListener {
            onRoomClick(room)
        }
    }

    override fun getItemCount() = rooms.size
}
