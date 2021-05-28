package net.ambitious.android.sharebookmarks.pages

import androidx.test.uiautomator.By
import net.ambitious.android.sharebookmarks.UITestUtils

class SideMenuPage {
  fun checkNotification(): HomePage {
    UITestUtils.waitUtilGoneObject(By.depth())
    return HomePage()
  }
}