package com.edu.happytesting.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edu.happytesting.databinding.QuestionlistLayoutBinding
import com.edu.happytesting.dataclass.RefreshingExamDetails

class QuestionListAdapter(
    private val Questions: ArrayList<RefreshingExamDetails.RefreshingExamDetailsItem>,
    private var onClick: (value: RefreshingExamDetails.RefreshingExamDetailsItem) -> Unit


) : RecyclerView.Adapter<QuestionListAdapter.QuestionViewHolder>() {
    inner class QuestionViewHolder(private var binding: QuestionlistLayoutBinding) :
        ViewHolder(binding.root) {
        fun setView(data: RefreshingExamDetails.RefreshingExamDetailsItem) {
            binding.examTitle.text = data.examName
            binding.count.text = data.numberOfQuestions.toString()
            binding.statusBtn.text = data.testStatus

//            if (data.testStatus == "Pending")
            if (data.testStatus == "Completed")
            {
                binding.statusBtn.setBackgroundColor(Color.parseColor("#FFA500"))

                binding.firstCard.setOnClickListener { onClick(data) }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = QuestionlistLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }
    override fun getItemCount(): Int = Questions.size
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.setView(Questions[position])
    }
}

