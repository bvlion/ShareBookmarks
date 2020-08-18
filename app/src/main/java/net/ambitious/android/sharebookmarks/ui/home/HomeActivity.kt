package net.ambitious.android.sharebookmarks.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.toolbar
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.ui.inquiry.InquiryActivity
import net.ambitious.android.sharebookmarks.ui.setting.SettingActivity
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.ItemType
import net.ambitious.android.sharebookmarks.util.Const.ItemType.FOLDER
import net.ambitious.android.sharebookmarks.util.Const.ItemType.ITEM
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject

class HomeActivity : BaseActivity(), OnNavigationItemSelectedListener,
    ItemEditDialogFragment.OnClickListener {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private val preferences: PreferencesUtils.Data by inject()

  private lateinit var homeFragment: HomeFragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("MainActivity")

    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    val navController =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()!!
    appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
    setupActionBarWithNavController(navController, appBarConfiguration)
    nav_view.setupWithNavController(navController)
    nav_view.setNavigationItemSelectedListener(this)

    setNavigation()
  }

  override fun onStart() {
    super.onStart()
    homeFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        ?.childFragmentManager?.fragments?.get(0) as HomeFragment
  }

  override fun onCreateOptionsMenu(menu: Menu) = true.apply {
    menuInflater.inflate(R.menu.main, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_folder_add, R.id.menu_item_add ->
        onCreateClick(
            if (item.itemId == R.id.menu_folder_add) {
              FOLDER
            } else {
              ITEM
            }
        )
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onSupportNavigateUp() =
    findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

  override fun onBackPressed() =
    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      homeFragment.backPress()
    }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      SIGN_IN_REQUEST_CODE ->
        if (resultCode == RESULT_OK) {
          FirebaseAuth.getInstance().currentUser?.let {
            preferences.userName = it.displayName
            preferences.userEmail = it.email
            preferences.userIcon = it.photoUrl?.toString()
            setNavigation()
            showSnackbar(String.format(getString(R.string.sign_in_success), preferences.userName))
          } ?: errorSnackbar()
        } else {
          errorSnackbar()
        }
      SETTING_REQUEST_CODE -> setNavigation()
    }
  }

  override fun onNavigationItemSelected(item: MenuItem) = false.apply {
    when (item.itemId) {
      R.id.menu_login -> startActivityForResult(
          AuthUI.getInstance()
              .createSignInIntentBuilder()
              .setAvailableProviders(arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()))
              .build(),
          SIGN_IN_REQUEST_CODE
      )
      R.id.menu_logout -> AlertDialog.Builder(this@HomeActivity)
          .setTitle(R.string.menu_logout)
          .setMessage(R.string.sign_out_confirm)
          .setNegativeButton(R.string.fui_cancel, null)
          .setPositiveButton(R.string.menu_logout) { _, _ ->
            AuthUI.getInstance()
                .signOut(this@HomeActivity)
                .addOnCompleteListener {
                  preferences.userName = null
                  preferences.userEmail = null
                  preferences.userIcon = null
                  setNavigation()
                  showSnackbar(getString(R.string.sign_out_complete))
                }
          }.show()
      R.id.menu_oss_license -> startActivity(
          Intent(this@HomeActivity, OssLicensesMenuActivity::class.java).apply {
            putExtra("title", getString(R.string.menu_oss_license))
          }
      )
      R.id.menu_contact -> startActivity(Intent(this@HomeActivity, InquiryActivity::class.java))
      R.id.menu_app_rating -> try {
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
      R.id.menu_settings -> startActivityForResult(
          Intent(
              this@HomeActivity,
              SettingActivity::class.java
          ), SETTING_REQUEST_CODE
      )
    }
    drawer_layout.closeDrawer(GravityCompat.START)
  }

  override fun isBackShowOnly() = false

  override fun onEdited(itemId: Long, itemName: String, itemUrl: String?) {
    homeFragment.updateItem(itemId, itemName, itemUrl)
    showSnackbar(
        if (itemId > 0) {
          getString(R.string.snackbar_update_message)
        } else {
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

  fun onEdit(item: Item) {
    ItemEditDialogFragment.newInstance(
        item.id!!,
        if (item.url.isNullOrEmpty()) {
          FOLDER
        } else {
          ITEM
        },
        item.name,
        item.url
    )
        .show(supportFragmentManager, ItemEditDialogFragment.TAG)
  }

  private fun onCreateClick(type: ItemType) {
    ItemEditDialogFragment.newInstance(0, type, null, null)
        .show(supportFragmentManager, ItemEditDialogFragment.TAG)
  }

  private fun setNavigation() {
    nav_view.menu.findItem(R.id.menu_login).isVisible = preferences.userName == null
    nav_view.menu.findItem(R.id.menu_logout).isVisible = preferences.userName != null
    val header = nav_view.getHeaderView(0)
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

  private fun errorSnackbar() = showSnackbar(getString(R.string.sign_in_failure))

  private fun showSnackbar(message: String) = Snackbar.make(
      findViewById<FragmentContainerView>(R.id.nav_host_fragment),
      message,
      Snackbar.LENGTH_LONG
  ).show()

  companion object {
    const val SIGN_IN_REQUEST_CODE = 1001
    const val SETTING_REQUEST_CODE = 1002
  }
}
