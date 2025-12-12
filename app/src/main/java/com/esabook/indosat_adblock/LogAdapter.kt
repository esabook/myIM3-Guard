package com.esabook.indosat_adblock

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.esabook.indosat_adblock.databinding.LogItemBinding
import java.text.DateFormat

class LogAdapter : PagingDataAdapter<LogEntity, LogAdapter.LogViewHolder>(LOG_COMPARATOR) {
    private val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
    val textSize = MutableLiveData<Float>()

    inner class LogViewHolder(private val binding: LogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(log: LogEntity) {
            binding.tvDate.text = dateFormat.format(java.util.Date(log.timestamp))
            binding.tvUri.text = log.uri
            binding.tvString.text = log.inString

            textSize.observeForever(::observeTextSize)
        }

        fun observeTextSize(size: Float?){
            if (size == null) return
            binding.tvDate.textSize = size
            binding.tvUri.textSize = size
            binding.tvString.textSize = size
        }

        fun clearObserving(){
            textSize.removeObserver(::observeTextSize)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder(
            LogItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = getItem(position)
        if (log != null) {
            holder.bind(log)
        }
    }

    override fun onViewRecycled(holder: LogViewHolder) {
        holder.clearObserving()
        super.onViewRecycled(holder)
    }

    companion object {
        private val LOG_COMPARATOR = object : DiffUtil.ItemCallback<LogEntity>() {

            override fun areItemsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean =
                oldItem == newItem
        }
    }
}