package net.ambitious.android.sharebookmarks.pages

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.uiautomator.By
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.UITestUtils
import org.hamcrest.core.AllOf

class SideMenuPage {
  fun checkNotification(): HomePage {
    UITestUtils.waitUtilGoneObject(By.res(UITestUtils.PACKAGE_NAME, "loading"))

    val view = Espresso.onView(
      AllOf.allOf(
        ViewMatchers.withId(R.id.recycler_view),
        ViewMatchers.isDisplayed()
      )
    )

    // プルリフレッシュ
    view.perform(ViewActions.swipeDown())

    return HomePage()
  }
}