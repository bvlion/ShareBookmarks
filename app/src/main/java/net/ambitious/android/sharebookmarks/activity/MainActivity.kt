package net.ambitious.android.sharebookmarks.activity

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import net.ambitious.android.sharebookmarks.BaseActivity
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils

class MainActivity : BaseActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AnalyticsUtils.logStartActivity(firebaseAnalytics, "MainActivity")

    setContentView(R.layout.activity_main)
    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)

    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    val navView: NavigationView = findViewById(R.id.nav_view)
    val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()!!
    appBarConfiguration = AppBarConfiguration(
        setOf(
          R.id.nav_home,
          R.id.nav_gallery
        ),
        drawerLayout
    )
    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onSupportNavigateUp() =
    findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

  override fun onBackPressed() =
    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
}
