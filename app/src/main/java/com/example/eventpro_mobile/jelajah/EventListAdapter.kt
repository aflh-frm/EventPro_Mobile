package com.example.eventpro_mobile.jelajah

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventpro_mobile.data.Event
import com.example.eventpro_mobile.databinding.ItemEventListBinding
import com.example.eventpro_mobile.util.loadEventImage
import com.example.eventpro_mobile.util.toRupiah

/**
 * Adapter RecyclerView untuk daftar event di halaman Jelajah (materi Pertemuan 10).
 * onCreateViewHolder membuat baris, onBindViewHolder mengisi baris dengan data.
 */
class EventListAdapter(
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    private val eventList = mutableListOf<Event>()

    // ViewHolder menyimpan binding satu baris agar tidak dicari ulang
    inner class EventViewHolder(val binding: ItemEventListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = eventList[position]
        with(holder.binding) {
            imgEvent.loadEventImage(item.imageUrl)
            tvCategory.text = item.category
            tvTitle.text = item.title
            tvDate.text = "${item.shortDate} • ${item.location}"
            tvPrice.text = item.price.toRupiah()
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount(): Int = eventList.size

    fun submit(events: List<Event>) {
        eventList.clear()
        eventList.addAll(events)
        notifyDataSetChanged()
    }
}
