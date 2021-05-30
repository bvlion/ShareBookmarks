package net.ambitious.android.sharebookmarks.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsApi
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsEntity
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsEntity.Notification
import net.ambitious.android.sharebookmarks.ui.notification.NotificationViewModel
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class NotificationViewModelTest {
  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  private lateinit var viewModel: NotificationViewModel

  private val entity = NotificationsEntity(
    listOf(
      Notification(
        "target date",
        "title",
        "subject",
        "url"
      )
    )
  )

  @Before
  fun setUp() {
    val notificationApiMock: NotificationsApi =
      object : NotificationsApi by mock(NotificationsApi::class.java) {
        override suspend fun getNotifications() = entity
      }
    val prefDataMock = mock(PreferencesUtils.Data::class.java)
    `when`(prefDataMock.userBearer).thenReturn(null)
    viewModel = NotificationViewModel(
      notificationApiMock,
      prefDataMock
    )
  }

  @Test
  fun fetchNotification() {
    val observer = TestObserver<NotificationsEntity>()
    viewModel.notifications.observeForever(observer)
    observer.await()

    val notifications = observer.get()!!

    assertThat(
      notifications.notifications.size,
      `is`(1)
    )

    val notification = entity.notifications[0]

    notifications.notifications.forEach {
      assertThat(it.targetDate, `is`(notification.targetDate))
      assertThat(it.title, `is`(notification.title))
      assertThat(it.subject, `is`(notification.subject))
      assertThat(it.url, `is`(notification.url))
    }
  }
}