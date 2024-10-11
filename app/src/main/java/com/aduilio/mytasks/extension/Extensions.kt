package com.aduilio.mytasks.extension

import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.value(): String {
    return text.toString()
}