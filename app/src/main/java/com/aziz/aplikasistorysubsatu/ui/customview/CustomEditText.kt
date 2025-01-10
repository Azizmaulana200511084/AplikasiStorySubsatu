package com.aziz.aplikasistorysubsatu.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.aziz.aplikasistorysubsatu.R

class CustomEditText: AppCompatEditText, View.OnTouchListener {
    private lateinit var errorIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        errorIcon = ContextCompat.getDrawable(context, R.drawable.ic_error) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() || s.length < 8) {
                    setError("Password terlalu pendek", errorIcon)
                    setCompoundDrawablesWithIntrinsicBounds(null, null, errorIcon, null)
                    (parent as? ViewGroup)?.findViewById<Button>(R.id.bt_sign_up)?.isEnabled = false
                    (parent as? ViewGroup)?.findViewById<Button>(R.id.bt_login)?.isEnabled = false
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    (parent as? ViewGroup)?.findViewById<Button>(R.id.bt_sign_up)?.isEnabled = true
                    (parent as? ViewGroup)?.findViewById<Button>(R.id.bt_login)?.isEnabled = true
                }
            }


            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}