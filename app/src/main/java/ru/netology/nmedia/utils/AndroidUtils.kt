package ru.netology.nmedia.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.view.View

object AndroidUtils {
    fun hideKeybord(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}