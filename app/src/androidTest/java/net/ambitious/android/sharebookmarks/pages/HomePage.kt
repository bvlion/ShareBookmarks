package net.ambitious.android.sharebookmarks.pages

import android.R.id
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.UITestUtils

class HomePage {
  private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

  fun sideMenu(id: Int, string: Int): SideMenuPage {
    onView(withId(R.id.drawer_layout))
      .check(matches(isClosed(Gravity.LEFT)))
      .perform(DrawerActions.open())

    UITestUtils.waitUtilHasObject(By.text(appContext.getString(string)))
    onView(withId(id))
      .check(matches(isDisplayed()))
      .perform(click())

    return SideMenuPage()
  }

  fun firstDialog(): HomePage {
    UITestUtils.waitUtilHasObject(By.text(appContext.getString(R.string.first_dialog_cancel)))

    onView(withId(id.button1))
      .inRoot(RootMatchers.isDialog())
      .check(matches(withText(R.string.first_dialog_ok)))
      .check(matches(isDisplayed()))

    onView(withId(id.button2))
      .inRoot(RootMatchers.isDialog())
      .check(matches(withText(R.string.first_dialog_cancel)))
      .check(matches(isDisplayed()))
      .perform(click())

    UITestUtils.waitUtilGoneObject(By.text(appContext.getString(R.string.first_dialog_cancel)))

    return this
  }
}