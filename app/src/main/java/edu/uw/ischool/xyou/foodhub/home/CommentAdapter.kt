package edu.uw.ischool.xyou.foodhub.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.uw.ischool.xyou.foodhub.data.Comment
import edu.uw.ischool.xyou.foodhub.databinding.CommentItemBinding

class CommentAdapter (
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.binding.apply {
            commentItemUsername.text = comments[position].username
            commentItemComment.text = comments[position].comment
            commentItemDate.text = comments[position].date.substring(0, 10)
        }
    }
}