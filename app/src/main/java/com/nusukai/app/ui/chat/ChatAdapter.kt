package com.nusukai.app.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nusukai.app.data.ChatMessage
import com.nusukai.app.databinding.ItemMsgAiBinding
import com.nusukai.app.databinding.ItemMsgUserBinding

class ChatAdapter(
    private val onCopy: (ChatMessage) -> Unit,
    private val onShare: (ChatMessage) -> Unit
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(Diff) {

    override fun getItemViewType(position: Int): Int = if (getItem(position).isUser) TYPE_USER else TYPE_AI

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER) {
            UserHolder(ItemMsgUserBinding.inflate(inflater, parent, false))
        } else {
            AiHolder(ItemMsgAiBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserHolder -> holder.binding.tvMessage.text = message.content
            is AiHolder -> {
                holder.binding.tvMessage.text = message.content
                holder.binding.btnCopy.setOnClickListener { onCopy(message) }
                holder.binding.btnShare.setOnClickListener { onShare(message) }
            }
        }
    }

    private class UserHolder(val binding: ItemMsgUserBinding) : RecyclerView.ViewHolder(binding.root)
    private class AiHolder(val binding: ItemMsgAiBinding) : RecyclerView.ViewHolder(binding.root)

    private object Diff : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(a: ChatMessage, b: ChatMessage) = a.id == b.id
        override fun areContentsTheSame(a: ChatMessage, b: ChatMessage) = a == b
    }

    private companion object {
        const val TYPE_USER = 0
        const val TYPE_AI = 1
    }
}
