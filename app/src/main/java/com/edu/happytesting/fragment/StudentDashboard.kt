package com.edu.happytesting.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.happytesting.activity.LoginActivity
import com.edu.happytesting.activity.MainActivity
import com.edu.happytesting.activity.TablayoutActivity
import com.edu.happytesting.adapter.QuestionListAdapter
import com.edu.happytesting.api.Response
import com.edu.happytesting.databinding.FragmentStudentDashboardBinding
import com.edu.happytesting.dataclass.RefresigExamDetails
import com.edu.happytesting.preference.HappyPreference
import com.edu.happytesting.utils.showLog
import com.edu.happytesting.viewModel.HappyViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


class StudentDashboard : Fragment() {

    private lateinit var studentDashboard: FragmentStudentDashboardBinding
    private var adapter: QuestionListAdapter? = null
    private val happyViewModel: HappyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        studentDashboard = FragmentStudentDashboardBinding.inflate(layoutInflater)
        return studentDashboard.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        studentDashboard.root
        super.onViewCreated(view, savedInstanceState)
        LoginActivity.studentexamdata
        happyViewModel.refreshingListData.observe(requireActivity(), refreshingObserver)

//        val bundle = arguments
//        val message = bundle!!.getParcelableArray("examData")


    }

    override fun onStart() {
        super.onStart()
        val data = HappyPreference(requireContext()).getUserDetails()
        lifecycleScope.launch {
            data["class_id"]?.let {
                data["studentId"]?.let { it1 ->
                    happyViewModel.getRefreshingData(
                        it,
                        it1
                    )
                }
            }
        }
    }




    @SuppressLint("SetTextI18n")
    private val refreshingObserver =
        Observer<Response<List<RefresigExamDetails.RefresigExamDetailsItem>>> {
            when (it) {
                is Response.Loading -> {
                    if (it.showLoader==true) {
                        studentDashboard.progress.visibility = View.VISIBLE

                    } else {
                        studentDashboard.progress.visibility = View.GONE
                    }

                }

                is Response.Success -> {
                    studentDashboard.recyclerview.layoutManager =
                        LinearLayoutManager(requireContext())
                    adapter =
                        QuestionListAdapter(
                            it.data as ArrayList<RefresigExamDetails.RefresigExamDetailsItem>,
                            ::pendingScreen
                        )
                    studentDashboard.refreshlayout.setOnRefreshListener {
                        Handler()
                            .postDelayed({
                                studentDashboard.refreshlayout.isRefreshing = false
                                Collections.shuffle(it.data, Random(System.currentTimeMillis()))
                                apiCall()
                                adapter = QuestionListAdapter(it.data, ::pendingScreen)
                                adapter?.notifyDataSetChanged()
                            }, 2000)
                    }
                    studentDashboard.recyclerview.adapter = adapter
                    if (it.data.isEmpty()) {
                        studentDashboard.noData.visibility=View.VISIBLE
                    }else{
                        studentDashboard.noData.visibility=View.GONE
                    }


                }
                is Response.Error -> {
                    showLog(it.errorMessage!!)

                }
            }
        }
    private fun apiCall(){
        val data = HappyPreference(requireContext()).getUserDetails()
        lifecycleScope.launch {
            data["class_id"]?.let {
                data["studentId"]?.let { it1 ->
                    happyViewModel.getRefreshingData(
                        it,
                        it1
                    )
                }
            }
        }
    }


    private fun pendingScreen(questionListItem: RefresigExamDetails.RefresigExamDetailsItem) {
        val testId = questionListItem.testId
        val subjectId = questionListItem.subjectId
        val duration = questionListItem.duration
        val totalQuestions = questionListItem.numberOfQuestions
        val intent = Intent(requireContext(), TablayoutActivity::class.java)
        intent.putExtra("testId", testId)
        intent.putExtra("subjectId", subjectId)
        intent.putExtra("duration", duration.toString())
        intent.putExtra("numberOfQuestions", totalQuestions)
        startActivity(intent)
    }
}




