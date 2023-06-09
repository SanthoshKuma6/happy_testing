package com.edu.happytesting.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.edu.happytesting.databinding.FragmentQuestionBinding


class Question : Fragment() {
    private lateinit var question: FragmentQuestionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        question = FragmentQuestionBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)


    }


}



