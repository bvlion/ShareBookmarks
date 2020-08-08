package net.ambitious.android.sharebookmarks

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.ambitious.android.sharebookmarks.data.local.ShareBookmarksDatabase
import net.ambitious.android.sharebookmarks.ui.home.HomeViewModel
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import net.ambitious.android.sharebookmarks.util.RemoteConfigUtils
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ShareBookmarksApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    if (FirebaseApp.getApps(this).isNotEmpty()) {
      RemoteConfigUtils.init()
    }

    MobileAds.initialize(this)

    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

    startKoin {
      androidContext(this@ShareBookmarksApplication)
      modules(viewModelModule, databaseModule, preferencesModule, analyticsModule)
    }
  }

  private val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
  }

  private val databaseModule = module {
    single {
      ShareBookmarksDatabase.createInstance(androidContext())
    }

    factory {
      get<ShareBookmarksDatabase>().itemDao()
    }
  }

  private val preferencesModule = module {
    factory {
      PreferencesUtils.Data(get())
    }

    single {
      PreferencesUtils.Preference(androidContext())
    }
  }

  private val analyticsModule = module {
    factory {
      AnalyticsUtils(get())
    }

    single {
      FirebaseAnalytics.getInstance(androidContext())
    }
  }
}