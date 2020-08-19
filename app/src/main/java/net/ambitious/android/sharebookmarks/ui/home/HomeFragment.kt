package net.ambitious.android.sharebookmarks.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.breadcrumbs_recycler_view
import kotlinx.android.synthetic.main.fragment_home.items_recycler_view
import kotlinx.android.synthetic.main.fragment_home.items_refresh
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.ui.home.adapter.BreadcrumbsAdapter
import net.ambitious.android.sharebookmarks.ui.home.adapter.BreadcrumbsAdapter.OnBreadcrumbsClickListener
import net.ambitious.android.sharebookmarks.ui.home.adapter.ItemListAdapter
import net.ambitious.android.sharebookmarks.ui.home.adapter.ItemListAdapter.OnItemClickListener
import net.ambitious.android.sharebookmarks.util.Const.ItemType
import net.ambitious.android.sharebookmarks.util.Const.ItemType.ITEM
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), OnItemClickListener, OnBreadcrumbsClickListener {

  private val homeViewModel by viewModel<HomeViewModel>()
  private val preferences: PreferencesUtils.Data by inject()
  private lateinit var itemListAdapter: ItemListAdapter
  private lateinit var breadcrumbsAdapter: BreadcrumbsAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_home, container, false).apply {
    homeViewModel.items.observe(
        viewLifecycleOwner,
        {
          breadcrumbsAdapter.setBreadcrumbs(homeViewModel.breadcrumbs.value!!)
          itemListAdapter.setItems(it)
          items_refresh.isRefreshing = false
        })

    homeViewModel.folders.observe(
        viewLifecycleOwner,
        { folderSelectDialogShow(it.first, it.second) }
    )
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    context?.let {
      itemListAdapter = ItemListAdapter(it, this)
      items_recycler_view.layoutManager = GridLayoutManager(context, 2)
      items_recycler_view.adapter = itemListAdapter

      breadcrumbsAdapter = BreadcrumbsAdapter(this)
      breadcrumbs_recycler_view.layoutManager =
        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
      breadcrumbs_recycler_view.adapter = breadcrumbsAdapter

      homeViewModel.getItems()

      items_refresh.setOnRefreshListener {
        homeViewModel.getItems()
      }
    }
  }

  override fun onRowClick(item: Long?, url: String?) {
    item?.let {
      homeViewModel.setParentId(it)
    }
    url?.let {
      try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        if (preferences.closeApp) {
          activity?.finish()
        }
      } catch (_: Exception) {
        Toast.makeText(activity, R.string.cant_start_activity, Toast.LENGTH_LONG).show()
      }
    }
  }

  override fun onDeleteClick(itemId: Long, itemName: String, itemType: ItemType) {
    context?.let {
      AlertDialog.Builder(it)
          .setTitle(getString(R.string.delete_title, itemName))
          .setMessage(
              getString(
                  if (itemType == ITEM) {
                    R.string.delete_item_message
                  } else {
                    R.string.delete_folder_message
                  }
              )
          )
          .setNegativeButton(R.string.dialog_cancel_button, null)
          .setPositiveButton(R.string.dialog_delete_button) { _, _ ->
            homeViewModel.deleteItem(itemId)
          }.show()
    }
  }

  override fun onEditClick(item: Item) =
    (activity as HomeActivity).onEdit(item)

  override fun onMoveClick(itemId: Long) {
    homeViewModel.getFolders(itemId)
  }

  override fun onShareClick(itemId: Long, url: String?) {
    if (url.isNullOrEmpty()) {
      // TODO
    } else {
      startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
      }, null))
    }
  }

  override fun onCreateShortcut(itemId: Long, url: String, name: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      (activity?.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager).requestPinShortcut(
          ShortcutInfo.Builder(activity, "shortcut-id-$itemId")
              .setShortLabel(name)
              .setIcon(Icon.createWithResource(activity, R.mipmap.ic_launcher_round)) // TODO
              .setIntent(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
              .build(), null
      )
    } else {
      @Suppress("DEPRECATION")
      activity?.sendBroadcast(Intent("com.android.launcher.action.INSTALL_SHORTCUT").apply {
        putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
        putExtra(Intent.EXTRA_SHORTCUT_INTENT, Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        putExtra(
            Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
            Intent.ShortcutIconResource.fromContext(activity, R.mipmap.ic_launcher_round) // TODO
        )
      })
      Toast.makeText(context, R.string.shortcut_created, Toast.LENGTH_SHORT).show()
    }
  }

  override fun onThumbnailUpdateClick(itemId: Long, url: String) {
    Toast.makeText(context, "onThumbnailUpdateClick", Toast.LENGTH_SHORT).show()
  }

  override fun onRowClick(id: Long) {
    homeViewModel.setParentId(id)
  }

  fun updateItem(itemId: Long, itemName: String, itemUrl: String?) {
    homeViewModel.updateItem(itemId, itemName, itemUrl)
  }

  fun moveItem(selfId: Long, parentId: Long) = homeViewModel.moveItem(selfId, parentId)

  fun backPress() {
    if (homeViewModel.breadcrumbs.value?.size == 1) {
      activity?.finish()
    } else {
      homeViewModel.setParentId(
          homeViewModel.breadcrumbs.value?.size?.minus(2)?.let {
            homeViewModel.breadcrumbs.value?.get(it)?.first
          } ?: 0
      )
    }
  }

  private fun folderSelectDialogShow(selfId: Long, folderList: List<Item>) =
    (activity as HomeActivity).onMove(selfId, folderList)
}