package com.edu.happytesting.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.core.content.ContextCompat
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


class ExamListAdapter(
    private val examData: ArrayList<QuestionList.QuestionListItem>,
    private val context: Context,
    private var subjectiveAnswer: (Bitmap) -> Unit,
    private var CountOFQuestions: (Boolean) -> Unit,
    private var bitMapView: (Bitmap) -> Unit,
    private var onTabSelect: OnTabSelect
) :
    RecyclerView.Adapter<ExamListAdapter.ExamViewHolder>() {
    private var currentItem: QuestionList.QuestionListItem? = null
    private var value: Bitmap? = null
    private var touchCount: Int = 0
    private var correction: Boolean? = null

    inner class ExamViewHolder(private var adapterQuestions: FragmentQuestionBinding) :
        ViewHolder(adapterQuestions.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun setView(examList: QuestionList.QuestionListItem) {
            adapterQuestions.question.text = examList.name
            adapterQuestions.drawing.setSizeForBrush(3.toFloat())
            adapterQuestions.drawing.setBrushColor(Color.BLACK)
            adapterQuestions.brushColor.setBackgroundColor(Color.WHITE)
            adapterQuestions.erase.setBackgroundColor(Color.WHITE)
            adapterQuestions.btnUndo.setBackgroundColor(Color.WHITE)
            adapterQuestions.btnRedo.setBackgroundColor(Color.WHITE)
            adapterQuestions.multicolor.setBackgroundColor(Color.WHITE)

            value?.let { subjectiveAnswer(it) }
            if (examList.type == 1) {
                adapterQuestions.drawingCanvasParent.visibility = View.GONE
                adapterQuestions.openChoiceQuestions.visibility = View.VISIBLE
                adapterQuestions.question1.text = examList.options?.get(0)
                adapterQuestions.question2.text = examList.options?.get(1)
                adapterQuestions.question3.text = examList.options?.get(2)
                adapterQuestions.question4.text = examList.options?.get(3)
                Log.d("Option", examList.options.toString())
            } else {
                adapterQuestions.openChoiceQuestions.visibility = View.GONE
                adapterQuestions.drawingCanvasParent.visibility = View.VISIBLE
                adapterQuestions.drawing.setSizeForBrush(3.toFloat())
                adapterQuestions.drawing.setBrushColor(Color.BLACK)
                try {
                    value?.let {
                        examList.type?.let { _ ->
                            subjectiveAnswer(
                                it,

                                )
                        }
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                adapterQuestions.drawing.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(
                                    0
                                ) == MotionEvent.TOOL_TYPE_ERASER
                            ) {
                                adapterQuestions.scrollView.requestDisallowInterceptTouchEvent(true)
                                touchCount = 0
                                touchCount++
                                if (touchCount == 1) {
                                    correction = true
                                    CountOFQuestions(correction!!)
                                }
                            } else {
                                adapterQuestions.scrollView.requestDisallowInterceptTouchEvent(false)

                            }

                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {}
                        MotionEvent.ACTION_MOVE -> {}


                    }


                    false
                }

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

        init {


            adapterQuestions.btnUndo.setOnClickListener {
                adapterQuestions.brushColor.setBackgroundColor(Color.WHITE)
                adapterQuestions.erase.setBackgroundColor(Color.WHITE)
                adapterQuestions.btnUndo.setBackgroundColor(Color.parseColor("#6D75FC"))
                adapterQuestions.btnRedo.setBackgroundColor(Color.WHITE)
                adapterQuestions.multicolor.setBackgroundColor(Color.WHITE)
                adapterQuestions.drawing.onClickUndo()

            }
            adapterQuestions.btnRedo.setOnClickListener {
                adapterQuestions.brushColor.setBackgroundColor(Color.WHITE)
                adapterQuestions.erase.setBackgroundColor(Color.WHITE)
                adapterQuestions.btnUndo.setBackgroundColor(Color.WHITE)
                adapterQuestions.btnRedo.setBackgroundColor(Color.parseColor("#6D75FC"))
                adapterQuestions.multicolor.setBackgroundColor(Color.WHITE)
                adapterQuestions.drawing.onClickRedo()

            }


            adapterQuestions.erase.setOnClickListener {
                adapterQuestions.brushColor.setBackgroundColor(Color.WHITE)
                adapterQuestions.erase.setBackgroundColor(Color.parseColor("#6D75FC"))
                adapterQuestions.btnUndo.setBackgroundColor(Color.WHITE)
                adapterQuestions.btnRedo.setBackgroundColor(Color.WHITE)
                adapterQuestions.multicolor.setBackgroundColor(Color.WHITE)
                val brushDialogue = Dialog(context)
                brushDialogue.setContentView(R.layout.dialogue_eraser_size)
                brushDialogue.setTitle("Choose Eraser Size: ")
                brushDialogue.findViewById<ImageButton>(R.id.eraserSmall)
                    .setOnClickListener {
                        adapterQuestions.drawing.onEraserSelect(10.toFloat())
                        brushDialogue.dismiss()
                    }
                brushDialogue.findViewById<ImageButton>(R.id.eraserMedium)
                    .setOnClickListener {
                        adapterQuestions.drawing.onEraserSelect(25.toFloat())
                        brushDialogue.dismiss()
                    }
                brushDialogue.findViewById<ImageButton>(R.id.eraserLarge)
                    .setOnClickListener {
                        adapterQuestions.drawing.onEraserSelect(40.toFloat())
                        brushDialogue.dismiss()
                    }

                brushDialogue.findViewById<ImageButton>(R.id.eraserExtraLarge)
                    .setOnClickListener {
                        adapterQuestions.drawing.onEraserSelect(70.toFloat())
                        brushDialogue.dismiss()
                    }

                brushDialogue.show()

            }

            //write brush color
            adapterQuestions.brushColor.setOnClickListener {
                adapterQuestions.brushColor.setBackgroundColor(Color.parseColor("#6D75FC"))
                adapterQuestions.erase.setBackgroundColor(Color.WHITE)
                adapterQuestions.btnUndo.setBackgroundColor(Color.WHITE)
                adapterQuestions.btnRedo.setBackgroundColor(Color.WHITE)
                adapterQuestions.multicolor.setBackgroundColor(Color.WHITE)
                val brushDialogue = Dialog(context)
                brushDialogue.setContentView(R.layout.dialogue_brush_size)
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
                brushDialogue.findViewById<ImageButton>(R.id.ibLarge)
                    .setOnClickListener {
                        adapterQuestions.drawing.setSizeForBrush(5.toFloat())
                        adapterQuestions.drawing.setBrushColor(Color.BLACK)
                        brushDialogue.dismiss()
                    }

                brushDialogue.show()

            }
            //color picker
            adapterQuestions.multicolor.setOnClickListener {
                adapterQuestions.brushColor.setBackgroundColor(Color.WHITE)
                adapterQuestions.erase.setBackgroundColor(Color.WHITE)
                adapterQuestions.btnUndo.setBackgroundColor(Color.WHITE)
                adapterQuestions.btnRedo.setBackgroundColor(Color.WHITE)
                adapterQuestions.multicolor.setBackgroundColor(Color.parseColor("#6D75FC"))
                ColorPickerDialog
                    .Builder(context)
                    .setTitle("Pick Color")
                    .setColorShape(ColorShape.SQAURE)
                    .setDefaultColor(R.color.black)
                    .setColorListener { color, _ ->
                        adapterQuestions.drawing.setBrushColor(color)
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
















