package com.zybooks.circuitmaker

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var tools: View? = null

    private var circuitView: ConstraintLayout? = null

    private var buttonX = 0f
    private var buttonY = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tools = findViewById(R.id.circuit_tools)
        val button: ImageButton? = findViewById(R.id.toolBox)
        val placeholderButton: Button? = findViewById(R.id.placeholder_button)

        button!!.setOnClickListener {
            Log.d("BUTTONS", "Clicked")
            val toolsVisible: Int? = tools!!.visibility

            Log.d("X position when last dropped", buttonX.toString())
            Log.d("Y position when last dropped", buttonY.toString())

            Log.d("Width of tools container", tools!!.width.toString())
            Log.d("Height of tools container", tools!!.height.toString())

            if (toolsVisible == View.VISIBLE) {
                tools!!.visibility = View.GONE
                if (placeholderButton!!.x < tools!!.width) {
                    placeholderButton.visibility = View.GONE
                } else {
                    placeholderButton.x = (buttonX + (tools!!.width / 2.0) - 1).toFloat()
                    placeholderButton.y = (buttonY + (tools!!.height / 2.0) - 1).toFloat()
                }
            } else {
                tools!!.visibility = View.VISIBLE
                placeholderButton?.x?.let {
                    if (it >= tools!!.width) {
                        placeholderButton.x = (buttonX - (tools!!.width / 2.0)).toFloat()
                        placeholderButton.y = (buttonY + (tools!!.height / 2.0)).toFloat()
                    }
                    placeholderButton.visibility = View.VISIBLE
                }
            }
        }
        circuitView = findViewById(R.id.main)
        onLongClickListener(placeholderButton)
        circuitView?.setOnDragListener(this::dragListener)
    }

    @Composable
    private fun DrawLine() {

    }

    private fun cloneButton(e: DragEvent): Button {
        Log.d("Local state", (e.localState as Button).x.toString())
        Log.d("Tools x", tools!!.x.toString())

        if (tools!!.width > (e.localState as Button).x) {
            val cloneButton = Button(this@MainActivity)
            val originalButton = (e.localState as Button)

            cloneButton.text = originalButton.getText()
            cloneButton.setTextColor(originalButton.currentTextColor)
            cloneButton.background = originalButton.background

            onLongClickListener(cloneButton)

            return cloneButton
        }
        return (e.localState as Button)
    }

    private fun onLongClickListener(button: Button?) {
        button?.setOnLongClickListener { view: View? ->
            val item = ClipData.Item(view!!.tag as CharSequence?)
            val dragData = ClipData(
                view.tag as CharSequence?,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            val myShadow = DragShadowBuilder(button)

            view.startDragAndDrop(
                dragData,
                myShadow,
                view,
                0
            )
            true
        }
    }

    private fun dragListener(v: View, e: DragEvent): Boolean {
        // Handle each of the expected events
        when (e.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                // Determine whether this View can accept
                if (e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    v.invalidate()

                    return true
                }

                return false
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                // ((ImageView) v).setColorFilter(Color.GREEN);
                v.invalidate()

                return true
            }

            DragEvent.ACTION_DRAG_LOCATION -> return true

            DragEvent.ACTION_DROP -> {
                val item = e.clipData.getItemAt(0)

                val dragData = item.text

                val cloneButton = cloneButton(e)
                if (cloneButton.x <= tools!!.x) {
                    circuitView!!.addView(cloneButton)
                }

                buttonX = e.x
                buttonY = e.y
                Log.d("Drop X position", buttonX.toString())
                Log.d("Drop Y position", buttonY.toString())

                cloneButton.x = buttonX
                cloneButton.y = buttonY

                Toast.makeText(this, "Dragged data is $dragData", Toast.LENGTH_LONG).show()

                // ((ImageView) v).clearColorFilter();
                v.invalidate()

                return true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                // ((ImageView) v).clearColorFilter();
                v.invalidate()

                if (e.result) {
                    Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG).show()
                }

                return true
            }

            else -> Log.e("DragDrop test", "Unknown action type received by View.OnDragListener")
        }

        return false
    }
}