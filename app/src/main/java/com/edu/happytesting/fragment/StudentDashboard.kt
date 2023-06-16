package com.edu.happytesting.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.happytesting.activity.LoginActivity
import com.edu.happytesting.activity.TabLayoutActivity
import com.edu.happytesting.adapter.QuestionListAdapter
import com.edu.happytesting.api.Response
import com.edu.happytesting.databinding.FragmentStudentDashboardBinding
import com.edu.happytesting.dataclass.RefreshingExamDetails
import com.edu.happytesting.preference.HappyPreference
import com.edu.happytesting.utils.showLog
import com.edu.happytesting.viewModel.HappyViewModel
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.Random


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
        LoginActivity.studentExamList
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
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private val refreshingObserver =
        Observer<Response<List<RefreshingExamDetails.RefreshingExamDetailsItem>>> {
            when (it) {
                is Response.Loading -> {
                    if (it.showLoader == true) {
                        studentDashboard.progress.visibility = View.VISIBLE
                        studentDashboard.imageview.visibility = View.VISIBLE
                        studentDashboard.undergoing.visibility = View.VISIBLE
                        studentDashboard.pleaseWaitTitle.visibility = View.VISIBLE

                    } else {
                        studentDashboard.progress.visibility = View.GONE
                        studentDashboard.imageview.visibility = View.GONE
                        studentDashboard.undergoing.visibility = View.GONE
                        studentDashboard.pleaseWaitTitle.visibility = View.GONE
                    }

                }

                is Response.Success -> {
                    studentDashboard.recyclerview.layoutManager =
                        LinearLayoutManager(requireContext())
                    adapter =
                        QuestionListAdapter(
                            it.data as ArrayList<RefreshingExamDetails.RefreshingExamDetailsItem>,
                            ::pendingScreen
                        )
                    studentDashboard.refreshLayout.setOnRefreshListener {
                        studentDashboard.refreshLayout.isRefreshing = false
                        Collections.shuffle(it.data, Random(System.currentTimeMillis()))
                        apiCall()
                        adapter = QuestionListAdapter(it.data, ::pendingScreen)
                        adapter?.notifyDataSetChanged()

                    }
                    studentDashboard.recyclerview.adapter = adapter
                    if (it.data.isEmpty()) {
                        studentDashboard.noQuestions.visibility = View.VISIBLE
                        studentDashboard.questionNotify.visibility = View.VISIBLE
                        studentDashboard.notify.visibility = View.VISIBLE
                        studentDashboard.questionTitle.visibility = View.VISIBLE
                    } else {
                        studentDashboard.noQuestions.visibility = View.GONE
                        studentDashboard.questionNotify.visibility = View.GONE
                        studentDashboard.notify.visibility = View.GONE
                        studentDashboard.questionTitle.visibility = View.GONE
                    }

                }
                is Response.Error -> {
                    showLog(it.errorMessage!!)

                }
            }
        }

    private fun apiCall() {
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

    private fun pendingScreen(questionListItem: RefreshingExamDetails.RefreshingExamDetailsItem) {
        val testId = questionListItem.testId
        val subjectId = questionListItem.subjectId
        val duration = questionListItem.duration
        val totalQuestions = questionListItem.numberOfQuestions
        val intent = Intent(requireContext(), TabLayoutActivity::class.java)
        intent.putExtra("testId", testId)
        intent.putExtra("subjectId", subjectId)
        intent.putExtra("duration", duration.toString())
        intent.putExtra("numberOfQuestions", totalQuestions)
        startActivity(intent)
    }


}




