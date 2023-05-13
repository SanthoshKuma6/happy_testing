package com.edu.happytesting.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.edu.happytesting.databinding.SheetMessageBinding
import com.edu.happytesting.ui.manager.DialogMgr
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MessageSheet(
    private var dialogMgr: DialogMgr,
    private var message: String,
    private var positiveButton: String = "Ok"
) : BottomSheetDialogFragment() {

    private lateinit var sheetMessageBinding: SheetMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sheetMessageBinding = SheetMessageBinding.inflate(inflater, container, false)
        return sheetMessageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sheetMessageBinding.messageTextView.text = message
        sheetMessageBinding.ok.text = positiveButton

        sheetMessageBinding.ok.setOnClickListener {
            dialogMgr.onPositiveButtonClicked()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (ignored: IllegalStateException) {
        }
    }
}