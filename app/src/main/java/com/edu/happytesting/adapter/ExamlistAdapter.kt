package com.edu.happytestin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.text.Html.ImageGetter
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.edu.happytesting.R
import com.edu.happytesting.activity.OnTabSelect
import com.edu.happytesting.databinding.FragmentQuestionBinding
import com.edu.happytesting.dataclass.QuestionList
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ExamlistAdapter(
    private val examData: ArrayList<QuestionList.QuestionListItem>,
    private val context: Context,
    private var onclick1: (Bitmap, Int) -> Unit,
    private var onclick2: (Boolean) -> Unit,
    private var bitMapView: (Bitmap) -> Unit,
    private var onTabSelect: OnTabSelect,
) :
    RecyclerView.Adapter<ExamlistAdapter.ExamViewHolder>() {
    var currentItem: QuestionList.QuestionListItem? = null
    var value: Bitmap? = null
    var touchCount: Int = 0
    var correction: Boolean? = null
    var choosedchoice = ""

    inner class ExamViewHolder(var adapterQuestions: FragmentQuestionBinding) :
        ViewHolder(adapterQuestions.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun setView(examList: QuestionList.QuestionListItem) {
            adapterQuestions.question.text = examList.name
            value?.let { onclick1(it, position) }
            if (examList.type == 1) {
                adapterQuestions.drawingcanvaparent.visibility = View.GONE
                adapterQuestions.openchoicequestions.visibility = View.VISIBLE
                adapterQuestions.question1.text = examList.options?.get(0)
                adapterQuestions.question2.text = examList.options?.get(1)
                adapterQuestions.question3.text = examList.options?.get(2)
                adapterQuestions.question4.text = examList.options?.get(3)
                Log.d("Option", examList.options.toString())
            } else {
                adapterQuestions.openchoicequestions.visibility = View.GONE
                adapterQuestions.drawingcanvaparent.visibility = View.VISIBLE
                adapterQuestions.drawing.setSizeForBrush(2.toFloat())
                adapterQuestions.drawing.setBrushColor(Color.BLACK)
                try {
                    value?.let {
                        examList.type?.let { it1 ->
                            onclick1(
                                it,
                                position,
                            )
                        }
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                adapterQuestions.drawing.setOnTouchListener(OnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (event.getToolType(0)==MotionEvent.TOOL_TYPE_STYLUS||event.getToolType(0)==MotionEvent.TOOL_TYPE_ERASER){
                                adapterQuestions.scrollView.requestDisallowInterceptTouchEvent(true)
                            }else{
                                adapterQuestions.scrollView.requestDisallowInterceptTouchEvent(false)

                            }
                            touchCount = 0
                            touchCount++
                            if (touchCount == 1) {
                                correction = true
                                onclick2(correction!!)
                            }
                        }
                        MotionEvent.ACTION_UP -> {

                        }
                        MotionEvent.ACTION_MOVE -> {

                        }


                    }


                    false
                })

            }

            adapterQuestions.question1.setOnClickListener {
                if (adapterQuestions.question1.isClickable) {
                    onTabSelect.onSelectAns(0)
                }
            }
            adapterQuestions.question2.setOnClickListener {
                if (adapterQuestions.question2.isClickable) {
                    onTabSelect.onSelectAns(1)
                }
            }
            adapterQuestions.question3.setOnClickListener {
                if (adapterQuestions.question3.isClickable) {
                    onTabSelect.onSelectAns(2)
                }
            }
            adapterQuestions.question4.setOnClickListener {
                if (adapterQuestions.question4.isClickable) {
                    onTabSelect.onSelectAns(3)
                }
            }
        }

        //Canva Drawing design tool
        init {


            adapterQuestions.btnUndo.setOnClickListener {
                adapterQuestions.drawing.onClickUndo()

            }
            adapterQuestions.btnRedo.setOnClickListener {
                adapterQuestions.drawing.onClickRedo()

            }
            // write erser color

            adapterQuestions.erase.setOnClickListener {
                val brushDialogue = Dialog(context)
                brushDialogue.setContentView(R.layout.dialogue_eraser_size)
                brushDialogue.setTitle("Choose Eraser Size: ")
                brushDialogue.findViewById<ImageButton>(R.id.ersersmall)
                    .setOnClickListener {
                        adapterQuestions.drawing.onEraserSelect(10.toFloat())
                        brushDialogue.findViewById<ImageButton>(R.id.ersersmall).setBackgroundColor(Color.parseColor("#FFA500"))
                        brushDialogue.findViewById<ImageButton>(R.id.ersersmall).backgroundTintList=ColorStateList.valueOf(Color.parseColor("#FFA500"))



                        brushDialogue.dismiss()
                    }
                brushDialogue.findViewById<ImageButton>(R.id.erasermedium)
                    .setOnClickListener {
                        adapterQuestions.drawing.onEraserSelect(25.toFloat())

                        brushDialogue.dismiss()
                    }
                brushDialogue.findViewById<ImageButton>(R.id.eraserlarge)
                    .setOnClickListener {
                        adapterQuestions.drawing.onEraserSelect(40.toFloat())

                        brushDialogue.dismiss()
                    }

                brushDialogue.findViewById<ImageButton>(R.id.eraserExtralarge)
                    .setOnClickListener {
                        adapterQuestions.drawing.onEraserSelect(70.toFloat())

                        brushDialogue.dismiss()
                    }

                brushDialogue.show()

            }

            //write brush color
            adapterQuestions.brushcolor.setOnClickListener {
                val brushDialogue = Dialog(context)
                brushDialogue.setContentView(com.edu.happytesting.R.layout.dialogue_brush_size)
                brushDialogue.setTitle("Choose Brush Size: ")
                brushDialogue.findViewById<ImageButton>(R.id.ibSmall)

                    .setOnClickListener {
                        adapterQuestions.drawing.setSizeForBrush(3.toFloat())
                        adapterQuestions.drawing.setBrushColor(Color.BLACK)
                        brushDialogue.dismiss()
                    }
                brushDialogue.findViewById<ImageButton>(R.id.ibMedium)
                    .setOnClickListener {
                        adapterQuestions.drawing.setSizeForBrush(4.toFloat())
                        adapterQuestions.drawing.setBrushColor(Color.BLACK)
                        brushDialogue.dismiss()
                    }
                brushDialogue.findViewById<ImageButton>(R.id.iblarge)
                    .setOnClickListener {
                        adapterQuestions.drawing.setSizeForBrush(5.toFloat())
                        adapterQuestions.drawing.setBrushColor(Color.BLACK)
                        brushDialogue.dismiss()
                    }

                brushDialogue.show()

            }
            //clor picker
            adapterQuestions.multicolor.setOnClickListener {
                ColorPickerDialog
                    .Builder(context)
                    .setTitle("Pick Color")
                    .setColorShape(ColorShape.SQAURE)
                    .setColorListener { color, _ ->
                        adapterQuestions.drawing.setBrushColor(color)
                        adapterQuestions.multicolor.setBackgroundColor(color)
                    }
                    .show()
            }


            ///current bitmap
            val lifecycleScope = CoroutineScope(Dispatchers.Main)
            lifecycleScope.launch {
                adapterQuestions.drawing.setBitmapCallback { bitmap ->
                    bitMapView(bitmap)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val examListLayoutBinding =
            FragmentQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExamViewHolder(examListLayoutBinding)
    }

    override fun getItemCount(): Int = examData.size
    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.setView(examData[position])
        currentItem = examData[holder.absoluteAdapterPosition]

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}
















