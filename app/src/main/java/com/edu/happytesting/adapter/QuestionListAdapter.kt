package com.edu.happytesting.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edu.happytesting.databinding.QuestionlistLayoutBinding
import com.edu.happytesting.dataclass.RefresigExamDetails

class QuestionListAdapter(
    private val Questions: ArrayList<RefresigExamDetails.RefresigExamDetailsItem>,
    private var onClick: (value: RefresigExamDetails.RefresigExamDetailsItem) -> Unit


) : RecyclerView.Adapter<QuestionListAdapter.QuestionViewHolder>() {
    inner class QuestionViewHolder(var binding: QuestionlistLayoutBinding) :
        ViewHolder(binding.root) {
        fun setView(data: RefresigExamDetails.RefresigExamDetailsItem) {
            binding.examtitle.text = data.examName
            binding.count.text = data.numberOfQuestions.toString()
            binding.statusbtn.text = data.testStatus

            if (data.testStatus == "Pending")
//            if (data.testStatus == "Completed")
            {
                binding.statusbtn.setBackgroundColor(Color.parseColor("#FFA500"))

                binding.firstcard.setOnClickListener { onClick(data) }
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

