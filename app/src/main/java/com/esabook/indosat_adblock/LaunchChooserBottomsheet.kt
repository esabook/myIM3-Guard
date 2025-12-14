package com.esabook.indosat_adblock

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esabook.indosat_adblock.databinding.LaunchChooserBottomsheetBinding
import com.esabook.indosat_adblock.databinding.LaunchChooserItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class LaunchChooserBottomsheet : BottomSheetDialogFragment(R.layout.launch_chooser_bottomsheet) {

    private val binding by lazy {
        LaunchChooserBottomsheetBinding.bind(requireView())
    }

    private val adapter by lazy { Adapter(intent!!) }
    private var resolveInfos: List<ResolveInfo>? = null
    private var intent: Intent? = null

    fun setData(i: Intent, ri: List<ResolveInfo>) = apply {
        resolveInfos = ri
        intent = i
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            binding.rvItem.adapter = adapter
            adapter.submitList(resolveInfos)

            binding.tv1.text = intent?.toUri(Intent.URI_ALLOW_UNSAFE)
        }
    }

    class VH(val b: LaunchChooserItemBinding) : RecyclerView.ViewHolder(b.root) {

        fun setData(i: ResolveInfo, intent: Intent) {
            val pm = itemView.context.packageManager
            b.tv1.text = i.loadLabel(pm)
            b.iv1.setImageDrawable(i.loadIcon(pm))

            b.root.setOnClickListener {
                val mIntent = Intent(intent)
                mIntent.setPackage(i.activityInfo.packageName)
                itemView.context.startActivity(mIntent)
            }
        }

    }

    override fun dismiss() {
        super.dismiss()

        activity?.finish()
    }

    class Adapter(val intent: Intent) : ListAdapter<ResolveInfo, VH>(DIFF) {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): VH {
            return VH(
                LaunchChooserItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(
            holder: VH,
            position: Int
        ) {
            holder.setData(getItem(position), intent)
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ResolveInfo>() {
            override fun areItemsTheSame(
                oldItem: ResolveInfo,
                newItem: ResolveInfo
            ): Boolean {
                return oldItem.activityInfo.targetActivity == newItem.activityInfo.targetActivity
            }

            override fun areContentsTheSame(
                oldItem: ResolveInfo,
                newItem: ResolveInfo
            ): Boolean {
                return true
            }

        }
    }
}