package me.chievent.colorpreview

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.widget.EditText
import android.widget.ToggleButton

class MainActivity : AppCompatActivity() {

    companion object {
        val PRESS_SET: IntArray = intArrayOf(android.R.attr.state_pressed)
        val NORMAL_SET: IntArray = intArrayOf()
    }

    var mNormalColorInput: EditText? = null
    var mPressedColorInput: EditText? = null
    var mNormalColorPreview: View? = null
    var mPressedColorPreview: View? = null
    var mPreviewView: View? = null
    var mRippleToggle: ToggleButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mNormalColorInput = findViewById(R.id.normal_color_input) as EditText
        mPressedColorInput = findViewById(R.id.pressed_color_input) as EditText
        mNormalColorPreview = findViewById(R.id.normal_color_preview)
        mPressedColorPreview = findViewById(R.id.pressed_color_preview)
        mPreviewView = findViewById(R.id.preview_view)
        mRippleToggle = findViewById(R.id.ripple_toggle) as ToggleButton

        mRippleToggle?.setOnCheckedChangeListener { buttonView, isChecked ->
            updateColors()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mRippleToggle?.isChecked = false
            mRippleToggle?.isEnabled = false
            mRippleToggle?.visibility = GONE
        }

        updateColors()

        val focusChangeListener: View.OnFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                updateColors()
            }
        }
        mNormalColorInput?.setOnFocusChangeListener(focusChangeListener)

        mPressedColorInput?.setOnFocusChangeListener(focusChangeListener)

        mPreviewView?.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                currentFocus.clearFocus()
            }

            return@setOnTouchListener false
        }
    }

    private fun updateColors() {

        val mNormalColor: Int = parseColor(mNormalColorInput?.text.toString()) ?: return
        val mPressedColor: Int = parseColor(mPressedColorInput?.text.toString()) ?: return

        mNormalColorPreview?.setBackgroundColor(mNormalColor)
        mPressedColorPreview?.setBackgroundColor(mPressedColor)
        if (mPreviewView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mRippleToggle?.isChecked ?: false) {
                val maskDrawable = GradientDrawable()
                maskDrawable.shape = GradientDrawable.RECTANGLE
                maskDrawable.setColor(mPressedColor)
                val contentDrawable = GradientDrawable()
                contentDrawable.setColor(mNormalColor)
                val bgDrawable: RippleDrawable =
                        RippleDrawable(ColorStateList.valueOf(mPressedColor), contentDrawable, maskDrawable)
                ViewCompat.setBackground(mPreviewView, bgDrawable)
            } else {
                val bgDrawable: StateListDrawable = StateListDrawable()
                bgDrawable.addState(PRESS_SET, ColorDrawable(mPressedColor))
                bgDrawable.addState(NORMAL_SET, ColorDrawable(mNormalColor))
                ViewCompat.setBackground(mPreviewView, bgDrawable)
            }
        }
    }

    private fun parseColor(s: String?): Int? {

        val len = s?.length ?: 0
        if (len == 8 || len == 6) {
            return Color.parseColor("#" + s.toString())
        } else {
            return null
        }
    }
}
