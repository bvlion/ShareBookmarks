package net.ambitious.android.sharebookmarks.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.AppLaunchChecker
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.databinding.ActivityMainBinding
import net.ambitious.android.sharebookmarks.receiver.ImageUploadEndBroadcastReceiver
import net.ambitious.android.sharebookmarks.receiver.MessageBroadcastReceiver
import net.ambitious.android.sharebookmarks.service.UpdateImageService
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import net.ambitious.android.sharebookmarks.ui.ItemEditDialogFragment
import net.ambitious.android.sharebookmarks.ui.admob.AdmobFragment
import net.ambitious.android.sharebookmarks.ui.home.dialog.FolderListDialogFragment
import net.ambitious.android.sharebookmarks.ui.notification.NotificationActivity
import net.ambitious.android.sharebookmarks.ui.others.OtherActivity
import net.ambitious.android.sharebookmarks.ui.setting.SettingActivity
import net.ambitious.android.sharebookmarks.ui.usage.UsageActivity
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.ItemType
import net.ambitious.android.sharebookmarks.util.Const.ItemType.FOLDER
import net.ambitious.android.sharebookmarks.util.Const.ItemType.ITEM
import net.ambitious.android.sharebookmarks.util.NotificationUtils

class HomeActivity : BaseActivity(), OnNavigationItemSelectedListener,
    ItemEditDialogFragment.OnClickListener,
    FolderListDialogFragment.OnSetListener {

  private lateinit var appBarConfiguration: AppBarConfiguration

  private lateinit var homeFragment: HomeFragment
  private var sorting = false

  private lateinit var messageBroadcastReceiver: MessageBroadcastReceiver
  private lateinit var imageBroadcastReceiver: ImageUploadEndBroadcastReceiver
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("MainActivity")

    setTheme(R.style.HomeIndigoTheme)
    binding = ActivityMainBinding.inflate(layoutInflater)

    setContentView(binding.root)
    setSupportActionBar(binding.contentMain.toolbar)

    val navController =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()!!
    appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
    setupActionBarWithNavController(navController, appBarConfiguration)
    binding.navView.setupWithNavController(navController)
    binding.navView.setNavigationItemSelectedListener(this)

    setNavigation()
  }

  override fun onStart() {
    super.onStart()
    homeFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        ?.childFragmentManager?.fragments?.get(0) as HomeFragment

    // 初回起動時に初期値 DB を設定
    if (!AppLaunchChecker.hasStartedFromLauncher(this)) {
      homeFragment.initializeInsert()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationUtils.createChannels(this)
      }
      AlertDialog.Builder(this)
          .setView(View.inflate(this, R.layout.dialog_first_message, null))
          .setPositiveButton(R.string.first_dialog_ok) { d, _ ->
            analyticsUtils.logMenuTap("first dialog how to use")
            d.dismiss()
            startActivity(Intent(this@HomeActivity, UsageActivity::class.java))
          }
          .setNegativeButton(R.string.first_dialog_cancel, null)
          .create().show()
    }
    AppLaunchChecker.onActivityCreate(this)
    messageBroadcastReceiver = MessageBroadcastReceiver {
      analyticsUtils.logResult("MessageBroadcastReceiver", it)
      if (it == Const.SyncMessageType.ALL_SYNC_ERROR.value) {
        homeFragment.setLoadingShow(false)
      }
      if (it == Const.SyncMessageType.ALL_SYNC_SUCCESS.value) {
        ContextCompat.startForegroundService(
            this,
            Intent(this, UpdateImageService::class.java).apply {
              putExtra(UpdateImageService.PARAM_ITEM_ALL, true)
            })
      }
      if (it == Const.SyncMessageType.ALL_SYNC_ERROR.value || it == Const.SyncMessageType.NORMAL_SYNC_ERROR.value) {
        showSnackbar(getString(R.string.sync_network_error))
      }
      homeFragment.imageReload()
    }

    imageBroadcastReceiver = ImageUploadEndBroadcastReceiver {
      if (it) {
        showSnackbar(getString(R.string.sync_success))
        homeFragment.setLoadingShow(false)
      }
      homeFragment.imageReload()
    }
  }

  override fun onResume() {
    super.onResume()
    registerReceiver(messageBroadcastReceiver, IntentFilter(Const.MESSAGE_BROADCAST_ACTION))
    registerReceiver(imageBroadcastReceiver, IntentFilter(Const.IMAGE_UPLOAD_BROADCAST_ACTION))
  }

  override fun onPause() {
    unregisterReceiver(messageBroadcastReceiver)
    unregisterReceiver(imageBroadcastReceiver)
    super.onPause()
  }

  override fun onCreateOptionsMenu(menu: Menu) = true.apply {
    menuInflater.inflate(R.menu.main, menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu?) = true.apply {
    menu?.let {
      it.findItem(R.id.menu_folder_add).isVisible = !sorting
      it.findItem(R.id.menu_item_add).isVisible = !sorting
      it.findItem(R.id.menu_sort_start).isVisible = !sorting
      it.findItem(R.id.menu_image_reacquisition).isVisible = !sorting
      it.findItem(R.id.menu_set_start_folder).isVisible =
        !sorting && preferences.startFolder == Const.StartFolderType.TARGET.value
      it.findItem(R.id.menu_sort_end).isVisible = sorting
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = super.onOptionsItemSelected(item).apply {
    when (item.itemId) {
      R.id.menu_folder_add, R.id.menu_item_add ->
        onCreateClick(
            if (item.itemId == R.id.menu_folder_add) {
              analyticsUtils.logMenuTap("folder add")
              FOLDER
            } else {
              analyticsUtils.logMenuTap("item add")
              ITEM
            }
        )
      R.id.menu_sort_start -> {
        analyticsUtils.logMenuTap("sort start")
        homeFragment.sort(start = true, isSave = false)
      }
      R.id.menu_sort_end -> {
        analyticsUtils.logMenuTap("sort end")
        homeFragment.sort(start = false, isSave = true)
      }
      R.id.menu_image_reacquisition -> {
        analyticsUtils.logMenuTap("image reacquisition")
        ContextCompat.startForegroundService(
            this@HomeActivity,
            Intent(this@HomeActivity, UpdateImageService::class.java).apply {
              putExtra(UpdateImageService.PARAM_ITEM_ALL, true)
            })
        showSnackbar(getString(R.string.snackbar_all_thumbnail_reload))
      }
      R.id.menu_set_start_folder -> {
        analyticsUtils.logMenuTap("set start folder")
        showSnackbar(homeFragment.setFirstFolder())
      }
    }
  }

  override fun onSupportNavigateUp() =
    findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

  override fun onBackPressed() {
    analyticsUtils.logMenuTap("onBackPressed")
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      binding.drawerLayout.closeDrawer(GravityCompat.START)
    } else {
      homeFragment.backPress()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      SIGN_IN_REQUEST_CODE ->
        if (resultCode == RESULT_OK) {
          FirebaseAuth.getInstance().currentUser?.also {
            preferences.userName = it.displayName
            preferences.userEmail = it.email
            preferences.userUid = it.uid
            preferences.userIcon = it.photoUrl?.toString()
            setNavigation()
            preferences.fcmToken?.let { token ->
              homeFragment.saveUserData(it.email ?: return, it.uid, token)
            }
            analyticsUtils.logResult("login", "success")
            showSnackbar(String.format(getString(R.string.sign_in_success), it.displayName))
          } ?: errorSnackbar()
        } else {
          errorSnackbar()
        }
      SETTING_REQUEST_CODE -> setNavigation()
      UPDATE_REQUEST_CODE -> if (resultCode == RESULT_OK) {
        homeFragment.setLoadingShow(true)
      }
    }
  }

  override fun onNavigationItemSelected(item: MenuItem) = false.apply {
    when (item.itemId) {
      R.id.menu_notification -> {
        analyticsUtils.logMenuTap("notification")
        startActivity(
            Intent(
                this@HomeActivity,
                NotificationActivity::class.java
            )
        )
      }
      R.id.menu_login -> {
        analyticsUtils.logMenuTap("login")
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                .build(),
            SIGN_IN_REQUEST_CODE
        )
      }
      R.id.menu_logout -> {
        analyticsUtils.logMenuTap("logout")
        AlertDialog.Builder(this@HomeActivity)
            .setTitle(R.string.menu_logout)
            .setMessage(R.string.sign_out_confirm)
            .setNegativeButton(R.string.fui_cancel, null)
            .setPositiveButton(R.string.menu_logout) { _, _ ->
              AuthUI.getInstance()
                  .signOut(this@HomeActivity)
                  .addOnCompleteListener {
                    preferences.userName = null
                    preferences.userEmail = null
                    preferences.userUid = null
                    preferences.userIcon = null
                    preferences.userBearer = null
                    setNavigation()
                    showSnackbar(getString(R.string.sign_out_complete))
                  }
            }.show()
      }
      R.id.menu_oss_license -> {
        analyticsUtils.logMenuTap("oss license")
        startActivity(
            Intent(this@HomeActivity, OssLicensesMenuActivity::class.java).apply {
              putExtra("title", getString(R.string.menu_oss_license))
            }
        )
      }
      R.id.menu_other -> {
        analyticsUtils.logMenuTap("other")
        startActivityForResult(
            Intent(this@HomeActivity, OtherActivity::class.java),
            UPDATE_REQUEST_CODE
        )
      }
      R.id.menu_app_rating -> {
        analyticsUtils.logMenuTap("app rating")
        try {
          startActivity(
              Intent(
                  Intent.ACTION_VIEW,
                  Uri.parse("market://details?id=${Const.STORE_URL}")
              )
          )
        } catch (_: ActivityNotFoundException) {
          startActivity(
              Intent(
                  Intent.ACTION_VIEW,
                  Uri.parse("https://play.google.com/store/apps/details?id=${Const.STORE_URL}")
              )
          )
        }
      }
      R.id.menu_settings -> {
        analyticsUtils.logMenuTap("settings")
        startActivityForResult(
            Intent(
                this@HomeActivity,
                SettingActivity::class.java
            ), SETTING_REQUEST_CODE
        )
      }
      R.id.menu_how_to_use -> {
        analyticsUtils.logMenuTap("how to use")
        startActivity(Intent(this@HomeActivity, UsageActivity::class.java))
      }
    }
    binding.drawerLayout.closeDrawer(GravityCompat.START)
  }

  override fun isBackShowOnly() = false

  override fun onEdited(itemId: Long, itemName: String, itemUrl: String?, folderId: Long?) {
    homeFragment.updateItem(itemId, itemName, itemUrl)
    val target = if (itemUrl.isNullOrEmpty()) {
      "folder"
    } else {
      "bookmark"
    }
    showSnackbar(
        if (itemId > 0) {
          analyticsUtils.logResult("item update", target)
          getString(R.string.snackbar_update_message)
        } else {
          analyticsUtils.logResult("item create", target)
          getString(R.string.snackbar_create_message)
        }.format(
            if (itemUrl.isNullOrEmpty()) {
              getString(R.string.snackbar_target_folder)
            } else {
              getString(R.string.snackbar_target_item)
            }
        )
    )
  }

  override fun onCancel() =
    supportFragmentManager.fragments
        .filter { it is ItemEditDialogFragment || it is FolderListDialogFragment }
        .forEach {
          supportFragmentManager.beginTransaction().remove(it).commit()
        }

  override fun onSet(selfId: Long, parentId: Long) {
    analyticsUtils.logResult("item move", "done")
    homeFragment.moveItem(selfId, parentId)
    showSnackbar(getString(R.string.move_complete))
  }

  fun onMove(selfId: Long, folderList: List<Item>) =
    FolderListDialogFragment.newInstance(selfId, ArrayList(folderList))
        .show(supportFragmentManager, FolderListDialogFragment.TAG)

  fun onEdit(item: Item) {
    analyticsUtils.logHomeTap(
        "edit",
        if (item.url.isNullOrEmpty()) {
          "folder"
        } else {
          "bookmark"
        }
    )
    ItemEditDialogFragment.newInstance(
        item.id!!,
        if (item.url.isNullOrEmpty()) {
          FOLDER
        } else {
          ITEM
        },
        item.name,
        item.url
    ).show(supportFragmentManager, ItemEditDialogFragment.TAG)
  }

  fun setSortMode(sortStart: Boolean) {
    analyticsUtils.logHomeTap("sort", sortStart.toString())
    sorting = sortStart
    invalidateOptionsMenu()
  }

  fun showSnackbar(message: String) = Snackbar.make(
      findViewById<FragmentContainerView>(R.id.nav_host_fragment),
      message,
      Snackbar.LENGTH_LONG
  ).show()

  fun changeAdmob() =
    supportFragmentManager.findFragmentById(R.id.admob_fragment)?.let {
      (it as AdmobFragment).displayChange()
    }

  private fun onCreateClick(type: ItemType) =
    ItemEditDialogFragment.newInstance(0, type, null, null)
        .show(supportFragmentManager, ItemEditDialogFragment.TAG)

  private fun setNavigation() {
    binding.navView.menu.findItem(R.id.menu_login).isVisible = preferences.userEmail == null
    binding.navView.menu.findItem(R.id.menu_logout).isVisible = preferences.userEmail != null
    binding.navView.menu.findItem(R.id.menu_billing).isVisible = false
    val header = binding.navView.getHeaderView(0)
    Glide.with(this)
        .load(preferences.userIcon ?: R.mipmap.ic_launcher_round)
        .circleCrop()
        .into(header.findViewById(R.id.user_image))
    header.findViewById<TextView>(R.id.user_name).text =
      preferences.userName ?: getString(R.string.app_name)
    header.findViewById<TextView>(R.id.user_mail).run {
      text = preferences.userEmail
      visibility = if (preferences.showMailAddress) {
        View.VISIBLE
      } else {
        View.GONE
      }
    }
  }

  private fun errorSnackbar() {
    analyticsUtils.logResult("login", "failed")
    showSnackbar(getString(R.string.sign_in_failure))
  }

  companion object {
    const val SIGN_IN_REQUEST_CODE = 1001
    const val SETTING_REQUEST_CODE = 1002
    const val UPDATE_REQUEST_CODE = 1003
  }
}
