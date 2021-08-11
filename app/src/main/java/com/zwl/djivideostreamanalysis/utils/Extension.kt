package com.zwl.djivideostreamanalysis.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.TextureView
import android.widget.Toast
import androidx.fragment.app.Fragment

val ui = Handler(Looper.getMainLooper())
fun runOnUiThreadEt(block: () -> Unit) = ui.post(block)

//region view extension
fun TextureView.drawBitmap(bitmap: Bitmap?) {
    bitmap ?: return
    val canvas: Canvas = lockCanvas() ?: return
    canvas.drawColor(0, PorterDuff.Mode.CLEAR)
    canvas.drawBitmap(
        bitmap,
        Rect(0, 0, bitmap.width, bitmap.height),
        Rect(0, 0, width, height),
        null
    )
    unlockCanvasAndPost(canvas)
}
//endregion

//region toast extension
var lastToastMessage = ""

fun Fragment.toast(message: String) {
    requireActivity().toast(message)
}

fun Activity.toast(message: String) {
    applicationContext.toast(message)
}

fun Context.toast(message: String) {
    if (lastToastMessage != message) {
        lastToastMessage = message
        runOnUiThreadEt {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
//endregion