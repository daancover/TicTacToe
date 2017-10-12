package com.coverlabs.tictactoe.util

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Html


/**
 * Created by Daniel on 12/10/2017.
 */
class DialogUtils {
    companion object {
        private var alertDialog: AlertDialog? = null

        fun showDialog(activity: Activity, title: String, message: String, positiveClick: DialogInterface.OnClickListener) {
            showDialog(activity, null, title, message, false, "Ok", null, positiveClick, null)
        }

        fun showDialog(activity: Activity?, icon: Int?, title: String?, msg: String?, setCancelable: Boolean, positiveButton: String?, negativeButton: String?, positiveClick: DialogInterface.OnClickListener?, negativeClick: DialogInterface.OnClickListener?) {
            val builder = AlertDialog.Builder(activity)

            if (title != null) {
                builder.setTitle(title)
            } else {
                builder.setTitle("")
            }
            if (msg != null) {
                builder.setMessage(Html.fromHtml(msg))
            }
            if (positiveButton != null) {
                builder.setPositiveButton(positiveButton, positiveClick)//second parameter used for onclicklistener
            }
            if (negativeButton != null) {
                builder.setNegativeButton(negativeButton, negativeClick)
            }

            alertDialog = builder.create()

            if (setCancelable) {
                (alertDialog as AlertDialog?)?.setCancelable(true)
            } else {
                (alertDialog as AlertDialog?)?.setCancelable(false)
            }

            //alertDialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
            if (activity != null && !activity.isFinishing) {
                (alertDialog as AlertDialog?)?.show()
            }
        }
    }
}