package com.example.tugas_pab.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.tugas_pab.R

object CustomToast {
    fun success(context: Context, message: String) {
        val layout = LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null)
        layout.findViewById<TextView>(R.id.custom_toast_message).text = message

        val toast = Toast(context.applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
