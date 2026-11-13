package com.nusukai.app.ui.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nusukai.app.data.Topic
import com.nusukai.app.databinding.ItemTopicBinding
import com.nusukai.app.util.pick

class TopicAdapter(
    private val onClick: (Topic) -> Unit
) : RecyclerView.Adapter<TopicAdapter.Holder>() {

    private var topics: List<Topic> = emptyList()
    private var done: Set<String> = emptySet()
    private var lang: String = "bn"

    fun submit(topics: List<Topic>, done: Set<String>, lang: String) {
        this.topics = topics
        this.done = done
        this.lang = lang
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = topics.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val t = topics[position]
        with(holder.binding) {
            tvNumber.text = (position + 1).toString()
            tvTitle.text = t.title.pick(lang)
            tvArabic.text = t.arabic
            tvSummary.text = t.summary.pick(lang)
            tvDone.visibility = if (t.id in done) View.VISIBLE else View.GONE
            root.setOnClickListener { onClick(t) }
        }
    }
}
