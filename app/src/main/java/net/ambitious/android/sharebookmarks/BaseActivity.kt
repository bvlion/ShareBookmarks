package net.ambitious.android.sharebookmarks

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import net.ambitious.android.sharebookmarks.util.RemoteConfigUtils

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        RemoteConfigUtils.fetch()
    }
}