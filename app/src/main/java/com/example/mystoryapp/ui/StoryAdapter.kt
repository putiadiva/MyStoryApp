package com.example.mystoryapp.ui

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.remote.response.Story
import com.example.mystoryapp.databinding.ItemStoryBinding

class StoryAdapter :
    PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        const val TAG = "StoryAdapter"
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "in on create view holder")
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "in on bind view holder")
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class ViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story) {
            Log.i(TAG, "in bind fun")
            binding.tvName.text = data.name
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivPhoto)
            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                intentDetail.putExtra("extra_id", data.id)
                itemView.context.startActivity(intentDetail)
            }
        }
    }
}