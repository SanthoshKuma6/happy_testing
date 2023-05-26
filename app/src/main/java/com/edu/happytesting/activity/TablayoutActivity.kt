package com.edu.happytesting.activity

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.edu.happytestin.ExamlistAdapter
import com.edu.happytesting.R
import com.edu.happytesting.api.Response
import com.edu.happytesting.databinding.ActivityTablayoutBinding
import com.edu.happytesting.dataclass.ObjectiveAnswer
import com.edu.happytesting.dataclass.QuestionList
import com.edu.happytesting.dataclass.SubjectiveAnswer
import com.edu.happytesting.fragment.Question
import com.edu.happytesting.preference.HappyPreference
import com.edu.happytesting.utils.CountDownTimerPausable
import com.edu.happytesting.utils.MyReceiver
import com.edu.happytesting.utils.showToast
import com.edu.happytesting.viewModel.HappyViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class TablayoutActivity : AppCompatActivity(), OnTabSelect {
    private lateinit var tablayoutBinding: ActivityTablayoutBinding

    private val questionList: ArrayList<QuestionList.QuestionListItem> = ArrayList()
    private val questionListTemp: ArrayList<QuestionList.QuestionListItem> = ArrayList()

     var objectiveAnswer: ArrayList<QuestionList.QuestionListItem> = ArrayList()
    private val subjectiveAnswer: ArrayList<QuestionList.QuestionListItem> = ArrayList()
    private lateinit var myReceiver: MyReceiver

    //total questions arraylist
    private val questionCount: ArrayList<QuestionList.QuestionListItem> = ArrayList()

    var examlistAdapter: ExamlistAdapter? = null
    var newItem = 0
    var idType: Int? = 0
    private var correction: Boolean? = null
    private val happyViewModel: HappyViewModel by viewModels()
    private var testDuration = ""
    private var testId = ""
    private var subjectId = ""
    private var numberOfQuestions = 0
    private var oCount = 0
    private var count = 0
    private var sCount = 0
    private var writingBitmap: Bitmap? = null
    private var totalTimeCountInMilliseconds: Long = 0
    private var totalLeftTimeCountInMilliseconds: Long = 0
    private var timeBlinkInMilliseconds: Long = 0
    private var countDownTimer: CountDownTimerPausable? = null
    var timerDetail = false
    var timerStart = false

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        tablayoutBinding = ActivityTablayoutBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        supportActionBar?.hide()
        setContentView(tablayoutBinding.root)
        happyViewModel.questionListResponse.observe(this, questionListObserver)

        happyViewModel.objectivesAnswer.observe(this, objetiveanswerObserver)
        happyViewModel.subjectiveAnswer.observe(this, subjectiveAnswerObserver)

        testId = intent.getStringExtra("testId").toString()
        subjectId = intent.getStringExtra("subjectId").toString()
        testDuration = intent.getStringExtra("duration").toString()
        numberOfQuestions = intent.getIntExtra("numberOfQuestions", 0)
        myReceiver = MyReceiver()
        apiCallQuestionList()
        tablayoutBinding.submit.setOnClickListener {
            leftAnswers()
        }
    }
    private val objetiveanswerObserver = Observer<Response<ObjectiveAnswer>> {
        when (it) {
            is Response.Success -> {
                finalResponse()
            }
            is Response.Loading -> {
                if (it.showLoader == true) {
                    tablayoutBinding.progress.visibility = View.VISIBLE
                } else {
                    tablayoutBinding.progress.visibility = View.GONE
                }
            }
            is Response.Error -> {
                showToast(" "+it.errorMessage)

            }
        }
    }

    private val subjectiveAnswerObserver = Observer<Response<SubjectiveAnswer>> {
        when (it) {
            is Response.Success -> {
                finalResponse()

            }
            is Response.Loading -> {
                if (it.showLoader == true) {
                    tablayoutBinding.progress.visibility = View.VISIBLE
                } else {
                    tablayoutBinding.progress.visibility = View.GONE
                }

            }

            is Response.Error -> {
            }
        }
    }
    private val questionListObserver = Observer<Response<List<QuestionList.QuestionListItem>>> {
        when (it) {
            is Response.Success -> {
                it.data?.let { it1 ->
                    questionList.addAll(it1)
                    questionListTemp.addAll(it1)
                    objectiveAnswer.addAll(it1)
                    subjectiveAnswer.addAll(it1)
                    questionCount.addAll(it1)
                    tabLayoutView()
                    startTimer()
                }
                if (it.data!!.isEmpty()) {

                    MaterialAlertDialogBuilder(this)
                        .setCancelable(false)
                        .setMessage("There is No Questions Available")
                        .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                            dialog.dismiss()

                        }
                        .show()

                }

                examlistAdapter = ExamlistAdapter(
                    questionList,
                    this@TablayoutActivity,
//                    ::objectiveAnswer,
                    ::subjectiveAnswer, ::choiceQuestions, ::viewBitMap, this,
                )
                tablayoutBinding.viewPager.adapter = examlistAdapter
            }

            is Response.Error -> {}
            is Response.Loading -> {
                if (it.showLoader == true) {
                    tablayoutBinding.progress.visibility = View.VISIBLE

                } else {
                    tablayoutBinding.progress.visibility = View.GONE
                }

            }
        }
    }


    private fun viewBitMap(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)

        subjectiveAnswer[newItem] =
            QuestionList.QuestionListItem(
                subjectiveAnswer[newItem].id,
                subjectiveAnswer[newItem].mark,
                subjectiveAnswer[newItem].name,
                subjectiveAnswer[newItem].options,
                subjectiveAnswer[newItem].type,
                subjectiveAnswer[newItem].isAnswered,
                subjectiveAnswer[newItem].chosenAnswer,
                encoded,
            )

    }

    private fun subjectiveAnswer(bitmap: Bitmap, position: Int) {
//        writingBitmap = bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
        subjectiveAnswer[position] =
            QuestionList.QuestionListItem(
                subjectiveAnswer[position].id,
                subjectiveAnswer[position].mark,
                subjectiveAnswer[position].name,
                subjectiveAnswer[position].options,
                subjectiveAnswer[position].type,
                subjectiveAnswer[position].isAnswered,
                subjectiveAnswer[position].chosenAnswer,
                encoded,
            )


    }

    private fun choiceQuestions(touchlistner: Boolean) {
        correction = touchlistner
        subjectiveAnswer[newItem] =
            QuestionList.QuestionListItem(
                subjectiveAnswer[newItem].id,
                subjectiveAnswer[newItem].mark,
                subjectiveAnswer[newItem].name,
                subjectiveAnswer[newItem].options,
                subjectiveAnswer[newItem].type,
                correction!!,
                subjectiveAnswer[newItem].chosenAnswer,
                subjectiveAnswer[newItem].encodedAnswer,
            )
    }

    override fun onResume() {
        super.onResume()
        newItem = 0
        tablayoutBinding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                newItem = tab.position
                examlistAdapter?.notifyDataSetChanged()
                idType = questionList[newItem].type
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                examlistAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun tabLayoutView() {
        val viewPager = tablayoutBinding.viewPager
        val tabLayout = tablayoutBinding.tabLayout
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
//        viewPager.isUserInputEnabled=false
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            for (i in 0 until questionList.size) {
                when (position) {
                    i -> tab.text = "Q ${i + 1}"
                }
            }
        }.attach()
        timerCreate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Check if the event was generated by a pencil or hand touch
        val toolType = event?.getToolType(0)
        when (toolType) {
            MotionEvent.TOOL_TYPE_STYLUS -> {
                tablayoutBinding.viewPager.isUserInputEnabled = false
            }

            MotionEvent.TOOL_TYPE_FINGER -> {
                tablayoutBinding.viewPager.isUserInputEnabled = true

            }

            else -> {}
        }
        // Return false to allow the ViewPager2 to handle the event
        return false
    }


    private fun timerCreate() {
        tablayoutBinding.duration.text = testDuration
        totalTimeCountInMilliseconds = (60 * Integer.valueOf(testDuration) * 1000).toLong()
        totalLeftTimeCountInMilliseconds = totalTimeCountInMilliseconds
        timeBlinkInMilliseconds = (30 * 1000).toLong()

    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimerPausable(totalTimeCountInMilliseconds, 500) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                totalLeftTimeCountInMilliseconds = millisUntilFinished
                if (millisUntilFinished < timeBlinkInMilliseconds) {
                    tablayoutBinding.duration.visibility = View.VISIBLE
                }
                tablayoutBinding.duration.text = String.format(
                    "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60,
                    seconds % 60
                )
                timerStart = true
            }

            override fun onFinish() {
                timerDetail = true
                timesUp()

            }
        }.start()
    }

    private fun timesUp() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setIcon(R.mipmap.happytesting)
            .setTitle("Alert!")
            .setMessage("Times Up!")
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                this.let {
                    MaterialAlertDialogBuilder(this)
                    val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .create()
                    val view = layoutInflater.inflate(R.layout.custom_alert_layout, null)
                    builder.setView(view)
                    val button = view.findViewById<TextView>(R.id.okbutton)
                    button.setOnClickListener {

                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    builder.setCanceledOnTouchOutside(false)
                    builder.show()

                }

            }
            .show()

    }


    private fun finalResponse(){
        this.let {
            MaterialAlertDialogBuilder(this)
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.custom_alert_layout, null)
            builder.setView(view)
            val button = view.findViewById<TextView>(R.id.okbutton)
            button.setOnClickListener {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()
        }

    }


    private fun answerSubmission() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setMessage(resources.getString(R.string.are_you_sure_submit))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                submit()
                dialog.dismiss()

            }.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()

            }
            .show()
    }

    private fun submit() {
        if (idType == 2) {
            for (i in objectiveAnswer) {
                if (i.type != 2) {
                    val data1 = HappyPreference(this).getUserDetails()
                    val jsonObject = JsonObject().apply {
                        addProperty("studentId", data1["studentId"])
                        addProperty("class_id", data1["class_id"])
                        addProperty("testId", testId)
                        addProperty("subjectId", subjectId)
                        addProperty("questionId", i.id)
                        addProperty("mark", i.mark)
                        addProperty("isAnswered", i.isAnswered)
                        addProperty("chosenAnswer", i.chosenAnswer)
                    }
                    lifecycleScope.launch {
                        happyViewModel.putObjectiveAnswer(jsonObject)
                    }
                } else {
                    for (i in subjectiveAnswer) {
                        if (i.type == 2) {
                            val data1 = HappyPreference(this).getUserDetails()
                            val studentWritingDetails = JsonObject().apply {
                                addProperty("studentId", data1["studentId"])
                                addProperty("class_id", data1["class_id"])
                                addProperty("testId", testId)
                                addProperty("subjectId", subjectId)
                                addProperty("questionId", i.id)
                                addProperty("mark", i.mark)
                                addProperty("isAnswered", i.isAnswered)
                                addProperty("encodedAnswer", i.encodedAnswer)
                            }

                            lifecycleScope.launch {
                                happyViewModel.putSubjectiveAnswer(
                                    studentWritingDetails
                                )
                            }
                        }
                    }
                }
            }
        }
        val array=JSONArray()
        for (i in objectiveAnswer) {
            if (i.type != 2) {
                val data1 = HappyPreference(this).getUserDetails()

                val jsonObject = JsonObject().apply {
                    addProperty("studentId", data1["studentId"])
                    addProperty("class_id", data1["class_id"])
                    addProperty("testId", testId)
                    addProperty("subjectId", subjectId)
                    addProperty("questionId", i.id)
                    addProperty("mark", i.mark)
                    addProperty("isAnswered", i.isAnswered)
                    addProperty("chosenAnswer", i.chosenAnswer)
                }
//                array.put(jsonObject)
//                val jsonObject1=JSONObject()
//                jsonObject1.put("data",array)

                lifecycleScope.launch {
                    happyViewModel.putObjectiveAnswer(jsonObject)

                }

            } else {
                for (i in subjectiveAnswer) {
                    if (i.type == 2) {
                        val data1 = HappyPreference(this).getUserDetails()
                        val studentWritingDetails = JsonObject().apply {
                            addProperty("studentId", data1["studentId"])
                            addProperty("class_id", data1["class_id"])
                            addProperty("testId", testId)
                            addProperty("subjectId", subjectId)
                            addProperty("questionId", i.id)
                            addProperty("mark", i.mark)
                            addProperty("isAnswered", i.isAnswered)
                            addProperty("encodedAnswer", i.encodedAnswer)
                        }

                        lifecycleScope.launch {
                            happyViewModel.putSubjectiveAnswer(studentWritingDetails)
                        }

                    }
                }

            }

        }

    }

    private fun leftAnswers() {
        oCount = 0
        for (i in objectiveAnswer) {
            if (i.type != 2) {
                if (!i.isAnswered) {
                    oCount++
                }
            }
        }
        sCount = 0
        for (j in subjectiveAnswer) {
            if (j.type == 2) {
                if (!j.isAnswered) {
                    sCount++

                }
            }
        }
//        encodeCount=cCount+sCount

        count = oCount + sCount
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setMessage("You have $count questions left to attend, Submit Anyway?")
            .setPositiveButton(resources.getString(R.string.submit)) { dialog, _ ->
                dialog.dismiss()
                answerSubmission()

            }.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()

            }
            .show()
    }

    private fun apiCallQuestionList() {
        lifecycleScope.launch {
            happyViewModel.getQuestionList(
                testId
            )
        }
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return questionList.size
        }


        override fun createFragment(position: Int): Fragment {

            for (i in 0 until questionList.size) {
                when (position) {
                    1 -> return Question()
                }
            }
            return Question()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!timerDetail) {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Alert!")
            builder.setMessage("Are you want to exit from this test?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    countDownTimer!!.cancel()
                    finish()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> dialog.dismiss() }.show()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(myReceiver, intentFilter)
        super.onStart()

    }


    override fun onSelectAns(position: Int) {
        objectiveAnswer[newItem] = questionList[newItem].options?.get(position)?.let { it1 ->
            QuestionList.QuestionListItem(
                objectiveAnswer[newItem].id,
                objectiveAnswer[newItem].mark,
                objectiveAnswer[newItem].name,
                objectiveAnswer[newItem].options,
                objectiveAnswer[newItem].type,
                true,
                it1,
            )
        }!!

    }
}

interface OnTabSelect {

    fun onSelectAns(position: Int)

}


