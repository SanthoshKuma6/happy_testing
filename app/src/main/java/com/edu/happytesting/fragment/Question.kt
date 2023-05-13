package com.edu.happytesting.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.edu.happytesting.R
import com.edu.happytesting.api.Response
import com.edu.happytesting.databinding.FragmentQuestionBinding
import com.edu.happytesting.dataclass.QuestionList
import com.edu.happytesting.viewModel.HappyViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class Question : Fragment() {
    private lateinit var question: FragmentQuestionBinding
    private val happyViewModel: HappyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        question = FragmentQuestionBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        happyViewModel.questionListResponse.observe(requireActivity(), questionListObserver)


    }

    private val questionListObserver = Observer<Response<List<QuestionList.QuestionListItem>>> {
        when (it) {
            is Response.Success -> {
                for (i in it.data!!) {
                    question.question.text = i.name

                }

            }
            is Response.Error -> {}
            is Response.Loading -> {}
        }
    }




}



