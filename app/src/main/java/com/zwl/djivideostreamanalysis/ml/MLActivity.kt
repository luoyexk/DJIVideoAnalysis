package com.zwl.djivideostreamanalysis.ml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.zwl.djivideostreamanalysis.R
import timber.log.Timber

class MLActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mlactivity)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.container, MLFragment())
            }
        }
    }
}