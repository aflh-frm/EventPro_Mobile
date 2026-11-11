package com.example.eventpro_mobile.jelajah

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.eventpro_mobile.data.Event
import com.example.eventpro_mobile.databinding.ItemEventListBinding
import com.example.eventpro_mobile.util.loadEventImage
import com.example.eventpro_mobile.util.toRupiah

/**
 * Custom Adapter ListView (materi MessageAdapter) untuk daftar event.
 * convertView dipakai ulang supaya scroll tetap ringan.
 */
class EventListAdapter(
    context: Context,
    private val onClick: (Event) -> Unit
) : ArrayAdapter<Event>(context, 0, mutableListOf()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ItemEventListBinding.inflate(LayoutInflater.from(context), parent, false).also {
                it.root.tag = it
            }
        } else {
            convertView.tag as ItemEventListBinding
        }

        val event = getItem(position) ?: return binding.root

        binding.imgEvent.loadEventImage(event.imageUrl)
        binding.tvCategory.text = event.category
        binding.tvTitle.text = event.title
        binding.tvDate.text = "${event.shortDate} • ${event.location}"
        binding.tvPrice.text = event.price.toRupiah()

        // Event klik ditangani di adapter (materi: listener di adapter)
        binding.root.setOnClickListener { onClick(event) }
        return binding.root
    }

    fun submit(events: List<Event>) {
        clear()
        addAll(events)
        notifyDataSetChanged()
    }
}
