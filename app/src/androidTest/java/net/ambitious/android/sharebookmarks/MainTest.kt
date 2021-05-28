package net.ambitious.android.sharebookmarks

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import net.ambitious.android.sharebookmarks.pages.HomePage
import net.ambitious.android.sharebookmarks.ui.home.HomeActivity
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainTest {

  @Rule
  @JvmField
  var activityTestRule = ActivityTestRule(HomeActivity::class.java, false, false)

  @Test
  fun useAppContext() {
    UITestUtils.initialize()
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    Assert.assertEquals(UITestUtils.PACKAGE_NAME, appContext.packageName)

    activityTestRule.launchActivity(null)

    HomePage()
      .firstDialog()
      .sideMenu(R.id.menu_notification, R.string.menu_notification)
      .checkNotification()
  }
}