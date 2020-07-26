package net.ambitious.android.sharebookmarks.activity

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.toolbar
import net.ambitious.android.sharebookmarks.BaseActivity
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils

class MainActivity : BaseActivity(), OnNavigationItemSelectedListener {

  private lateinit var appBarConfiguration: AppBarConfiguration

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AnalyticsUtils.logStartActivity(firebaseAnalytics, "MainActivity")

    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    val navController =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()!!
    appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
    setupActionBarWithNavController(navController, appBarConfiguration)
    nav_view.setupWithNavController(navController)
    nav_view.setNavigationItemSelectedListener(this)
  }

  override fun onCreateOptionsMenu(menu: Menu) = true.apply {
    menuInflater.inflate(R.menu.main, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return super.onOptionsItemSelected(item)
  }

  override fun onSupportNavigateUp() =
    findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

  override fun onBackPressed() =
    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }

  override fun onNavigationItemSelected(item: MenuItem) = false.apply {
    when (item.itemId) {
      R.id.menu_oss_license -> startActivity(
          Intent(this@MainActivity, OssLicensesMenuActivity::class.java).apply {
            putExtra("title", getString(R.string.menu_oss_license))
          }
      )
    }
    drawer_layout.closeDrawer(GravityCompat.START)
  }
}
