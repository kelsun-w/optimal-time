package com.ned.optimaltime.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class NoticeDialogFragment :
    DialogFragment() {
    private lateinit var listener: NoticeDialogListener

    companion object {

        const val HOST_FRAGMENT = "com.ned.noticedialog.parent"
        const val DIALOG_MSG = "com.ned.noticedialog.msg"
        const val DIALOG_POS_MSG = "com.ned.noticedialog.posmsg"
        const val DIALOG_NEG_MSG = "com.ned.noticedialog.negmsg"

        fun getInstance(msg: Int, positiveBtnMsg: Int, negativeBtnMsg: Int): NoticeDialogFragment {
            val args: Bundle = Bundle()

            args.putInt(DIALOG_MSG, msg)
            args.putInt(DIALOG_POS_MSG, positiveBtnMsg)
            args.putInt(DIALOG_NEG_MSG, negativeBtnMsg)

            val dialog = NoticeDialogFragment()
            dialog.arguments = args
            return dialog
        }
    }

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parent = fragmentManager?.getFragment(this.arguments!!, HOST_FRAGMENT)

        // Verify that the host Fragment implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = parent as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The fragment doesn't implement the interface, throw exception
            throw ClassCastException(
                (parent.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage(arguments!!.getInt(DIALOG_MSG))
                .setPositiveButton(
                    arguments!!.getInt(DIALOG_POS_MSG),
                    DialogInterface.OnClickListener { dialog, id ->
                        // Send the positive button event back to the host activity
                        listener.onDialogPositiveClick(this)
                    })
                .setNegativeButton(
                    arguments!!.getInt(DIALOG_NEG_MSG),
                    DialogInterface.OnClickListener { dialog, id ->
                        // Send the positive button event back to the host activity
                        listener.onDialogNegativeClick(this)
                    })
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}