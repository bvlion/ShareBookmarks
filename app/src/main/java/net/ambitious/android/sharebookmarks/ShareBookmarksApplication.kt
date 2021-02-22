package net.ambitious.android.sharebookmarks

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.ambitious.android.sharebookmarks.data.local.ShareBookmarksDatabase
import net.ambitious.android.sharebookmarks.data.remote.ShareBookmarksApi
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.data.remote.item.ItemApi
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsApi
import net.ambitious.android.sharebookmarks.data.remote.share.ShareApi
import net.ambitious.android.sharebookmarks.data.remote.users.UsersApi
import net.ambitious.android.sharebookmarks.data.source.ShareBookmarksDataSource
import net.ambitious.android.sharebookmarks.data.source.impl.ShareBookmarksDataSourceImpl
import net.ambitious.android.sharebookmarks.ui.faq.FaqViewModel
import net.ambitious.android.sharebookmarks.ui.home.HomeViewModel
import net.ambitious.android.sharebookmarks.ui.inquiry.InquiryViewModel
import net.ambitious.android.sharebookmarks.ui.notification.NotificationViewModel
import net.ambitious.android.sharebookmarks.ui.others.DetailViewModel
import net.ambitious.android.sharebookmarks.ui.share.ShareUserViewModel
import net.ambitious.android.sharebookmarks.ui.shareadd.ShareAddViewModel
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
      modules(
          viewModelModule,
          databaseModule,
          apiModule,
          preferencesModule,
          analyticsModule,
          dataSourceModule
      )
    }
  }

  private val viewModelModule = module {
    viewModel {
      HomeViewModel(get(), get(), get())
    }
    viewModel {
      InquiryViewModel(get())
    }
    viewModel {
      ShareAddViewModel(get())
    }
    viewModel {
      NotificationViewModel(get())
    }
    viewModel {
      ShareUserViewModel(get())
    }
    viewModel {
      DetailViewModel(get())
    }
    viewModel {
      FaqViewModel(get())
    }
  }

  private val databaseModule = module {
    single {
      ShareBookmarksDatabase.createInstance(androidContext())
    }

    factory {
      get<ShareBookmarksDatabase>().itemDao()
    }

    factory {
      get<ShareBookmarksDatabase>().shareDao()
    }
  }

  private val apiModule = module {
    single {
      ShareBookmarksApi.createInstance(get())
    }

    factory {
      get<ShareBookmarksApi>().create4Inquiry()
    }

    factory {
      get<ShareBookmarksApi>().create(NotificationsApi::class)
    }

    factory {
      get<ShareBookmarksApi>().create(UsersApi::class)
    }

    factory {
      get<ShareBookmarksApi>().create(ShareApi::class)
    }

    factory {
      get<ShareBookmarksApi>().create(ItemApi::class)
    }

    factory {
      get<ShareBookmarksApi>().create(EtcApi::class)
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

  private val dataSourceModule = module {
    single<ShareBookmarksDataSource> {
      ShareBookmarksDataSourceImpl(get(), get(), get(), get(), get())
    }
  }
}