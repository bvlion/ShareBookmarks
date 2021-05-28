package net.ambitious.android.sharebookmarks

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import kotlinx.coroutines.runBlocking
import net.ambitious.android.sharebookmarks.data.local.ShareBookmarksDatabase
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.hamcrest.Matchers

object UITestUtils {
  const val PACKAGE_NAME = "net.ambitious.android.sharebookmarks.debug"

  private const val TIMEOUT_UI_DEVICE: Long = 15000

  private var uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

  @JvmStatic
  fun waitUtilHasObject(selector: BySelector, multipleTimeout: Int = 1) =
    ViewMatchers.assertThat(
      uiDevice.wait(Until.hasObject(selector), TIMEOUT_UI_DEVICE * multipleTimeout),
      Matchers.`is`(true)
    )

  @JvmStatic
  fun waitUtilGoneObject(selector: BySelector, multipleTimeout: Int = 1) =
    ViewMatchers.assertThat(
      uiDevice.wait(Until.gone(selector), TIMEOUT_UI_DEVICE * multipleTimeout),
      Matchers.`is`(true)
    )

  fun waitTime(waitTime: Long = 1000) = uiDevice.waitForWindowUpdate(PACKAGE_NAME, waitTime)

  fun initialize() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    // 初期化
    val preferences = PreferencesUtils.Data(PreferencesUtils.Preference(appContext))
    preferences.logout()
    // AppLaunchChecker を初期化
    appContext.getSharedPreferences("android.support.AppLaunchChecker", 0).edit()
      .remove("startedFromLauncher").apply()
    // DB を空にする
    runBlocking {
      ShareBookmarksDatabase.createInstance(appContext).itemDao().deleteAllItems()
    }
    waitTime(2000)
  }
}