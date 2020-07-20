package net.ambitious.android.sharebookmarks

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.ambitious.android.sharebookmarks.data.local.ShareBookmarksDatabase
import net.ambitious.android.sharebookmarks.util.RemoteConfigUtils

class ShareBookmarksApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (FirebaseApp.getApps(this).isNotEmpty()) {
            RemoteConfigUtils.init()
        }

        MobileAds.initialize(this)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        ShareBookmarksDatabase.initialize(this)
    }
}