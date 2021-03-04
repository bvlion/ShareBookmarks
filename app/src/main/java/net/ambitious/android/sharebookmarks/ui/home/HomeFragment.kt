package net.ambitious.android.sharebookmarks.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.squareup.moshi.Moshi
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.databinding.FragmentHomeBinding
import net.ambitious.android.sharebookmarks.service.DataUpdateService
import net.ambitious.android.sharebookmarks.service.UpdateImageService
import net.ambitious.android.sharebookmarks.ui.home.adapter.BreadcrumbsAdapter
import net.ambitious.android.sharebookmarks.ui.home.adapter.BreadcrumbsAdapter.OnBreadcrumbsClickListener
import net.ambitious.android.sharebookmarks.ui.home.adapter.ItemListAdapter
import net.ambitious.android.sharebookmarks.ui.home.adapter.ItemListAdapter.OnItemClickListener
import net.ambitious.android.sharebookmarks.ui.share.ShareUserActivity
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import net.ambitious.android.sharebookmarks.util.Const.ItemType
import net.ambitious.android.sharebookmarks.util.Const.OwnerType
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), OnItemClickListener, OnBreadcrumbsClickListener {

  private val homeViewModel by viewModel<HomeViewModel>()
  private val preferences: PreferencesUtils.Data by inject()
  private val analyticsUtils: AnalyticsUtils by inject()

  private lateinit var itemListAdapter: ItemListAdapter
  private lateinit var breadcrumbsAdapter: BreadcrumbsAdapter
  private lateinit var binding: FragmentHomeBinding

  private var loading: AlertDialog? = null
  private var isFirstLoad = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = FragmentHomeBinding.inflate(inflater, container, false).apply {
    binding = this
    homeViewModel.items.observe(
        viewLifecycleOwner,
        {
          itemListAdapter.setItems(it, homeViewModel.ownerType.value == OwnerType.OWNER.value)
          binding.itemsRefresh.isRefreshing = false
          targetImageUpdate()
        })

    homeViewModel.breadcrumbs.observe(
        viewLifecycleOwner,
        {
          (activity as HomeActivity).title = it.lastOrNull()?.second
          breadcrumbsAdapter.setBreadcrumbs(it)
        })

    homeViewModel.folders.observe(
        viewLifecycleOwner,
        {
          it?.run {
            folderSelectDialogShow(first, second)
            homeViewModel.setFolderNull()
          }
        })

    homeViewModel.sorting.observe(
        viewLifecycleOwner,
        {
          itemListAdapter.setSortable(it)
          binding.itemsRecyclerView.post { itemListAdapter.notifyDataSetChanged() }
          itemTouchHelper.attachToRecyclerView(
              if (it) {
                binding.itemsRecyclerView
              } else {
                null
              }
          )
        }
    )

    homeViewModel.tokenSave.observe(
        viewLifecycleOwner,
        {
          if (it == null) {
            return@observe
          }
          preferences.userBearer = it.accessToken
          if (isFirstLoad) {
            setLoadingShow(true)
          } else {
            context?.let { context ->
              DataUpdateService.startItemSync(context)
              if (!preferences.shareSynced) {
                DataUpdateService.startShareSync(context)
              }
            }
          }
          isFirstLoad = false
          if (preferences.isPremium != it.premium) {
            preferences.isPremium = it.premium
            (activity as HomeActivity).changeAdmob()
          }
          homeViewModel.setTokenSaved()
        }
    )

    homeViewModel.ogpImage.observe(
        viewLifecycleOwner,
        { ogp ->
          ogp.first.isVisible = ogp.second != null
          if (ogp.second != null) {
            context?.let {
              Glide.with(it)
                  .load(ogp.second)
                  .centerCrop()
                  .into(ogp.first)
            }
          }
        }
    )

    homeViewModel.itemUpdate.observe(
        viewLifecycleOwner,
        {
          if (it == null) {
            return@observe
          }
          context?.let { context ->
            DataUpdateService.startItemSync(context)
          }
          homeViewModel.itemUpdated()
        }
    )

    homeViewModel.dialogShow.observe(
        viewLifecycleOwner,
        {
          if (it) {
            context?.let { context ->
              loading = AlertDialog.Builder(context)
                  .setView(View.inflate(context, R.layout.dialog_loading, null).apply {
                    Glide.with(context).load(R.mipmap.loading).into(findViewById(R.id.loading_gif))
                  })
                  .setCancelable(false)
                  .create()
              loading?.show()
              activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
              DataUpdateService.startAllSync(context)
            }
          } else {
            loading?.dismiss()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
          }
        }
    )

    if (savedInstanceState == null) {
      preferences.userEmail?.let { email ->
        homeViewModel.sendUserData(
            email,
            preferences.userUid ?: return@let,
            preferences.fcmToken ?: return@let
        )
      }
      homeViewModel.setInitialParentId(preferences.startFolderId)
    }
  }.root

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    context?.let {
      itemListAdapter = ItemListAdapter(it, this)
      binding.itemsRecyclerView.layoutManager = GridLayoutManager(context, 2)
      binding.itemsRecyclerView.adapter = itemListAdapter
    }

    breadcrumbsAdapter = BreadcrumbsAdapter(this)
    binding.breadcrumbsRecyclerView.layoutManager =
      LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    binding.breadcrumbsRecyclerView.adapter = breadcrumbsAdapter

    binding.itemsRefresh.setOnRefreshListener {
      if (homeViewModel.sorting.value == false) {
        if (preferences.userUid.isNullOrEmpty()) {
          homeViewModel.getItems()
        } else {
          context?.let { context ->
            DataUpdateService.startItemSync(context)
          }
        }
      } else {
        binding.itemsRefresh.isRefreshing = false
      }
    }

    if (savedInstanceState == null) {
      homeViewModel.getItems()
      homeViewModel.setSort()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == AppCompatActivity.RESULT_OK) {
      when (requestCode) {
        SHARE_USER_ACTIVITY_REQUEST -> context?.let {
          preferences.shareSynced = false
          DataUpdateService.startShareSync(it)
        }
      }
    }
  }

  override fun onRowClick(item: Long?, url: String?) {
    item?.let {
      analyticsUtils.logHomeTap("folder")
      homeViewModel.setParentId(it, preferences)
    }
    url?.let {
      analyticsUtils.logHomeTap("bookmark")
      try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        if (preferences.closeApp) {
          activity?.finish()
        }
      } catch (e: Exception) {
        analyticsUtils.logResult("bookmark tap error", e.message ?: "")
        Toast.makeText(activity, R.string.cant_start_activity, Toast.LENGTH_LONG).show()
      }
    }
  }

  override fun onDeleteClick(itemId: Long, itemName: String, itemType: ItemType) {
    analyticsUtils.logHomeTap("delete")
    context?.let {
      AlertDialog.Builder(it)
          .setTitle(getString(R.string.delete_title, itemName))
          .setMessage(
              getString(
                  if (itemType == ItemType.ITEM) {
                    R.string.delete_item_message
                  } else {
                    R.string.delete_folder_message
                  }
              )
          )
          .setNegativeButton(R.string.dialog_cancel_button, null)
          .setPositiveButton(R.string.dialog_delete_button) { _, _ ->
            analyticsUtils.logResult("delete", "success")
            homeViewModel.deleteItem(itemId)
          }.show()
    }
  }

  override fun onEditClick(item: Item) = (activity as HomeActivity).onEdit(item)

  override fun onMoveClick(itemId: Long) {
    analyticsUtils.logHomeTap("move")
    homeViewModel.getFolders(itemId)
  }

  override fun onShareClick(itemId: Long, url: String?) {
    if (url.isNullOrEmpty()) {
      if (preferences.userEmail == null) {
        analyticsUtils.logHomeTap("share folder", "error")
        context?.let {
          AlertDialog.Builder(it).setMessage(R.string.share_login_warning)
              .setPositiveButton(android.R.string.ok, null).create().show()
        }
        return
      }
      analyticsUtils.logHomeTap("share folder", "success")
      startActivityForResult(
          ShareUserActivity.createIntent(context ?: return, itemId),
          SHARE_USER_ACTIVITY_REQUEST
      )
      activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    } else {
      analyticsUtils.logHomeTap("share bookmark")
      startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
      }, null))
    }
  }

  override fun onCreateShortcut(itemId: Long, url: String, name: String) {
    analyticsUtils.logHomeTap("create shortcut")
    homeViewModel.bitmap.observe(viewLifecycleOwner,
        {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (activity?.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager).requestPinShortcut(
                ShortcutInfo.Builder(activity, "shortcut-id-$itemId")
                    .setShortLabel(name)
                    .setIcon(Icon.createWithBitmap(it))
                    .setIntent(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    .build(), null
            )
          } else {
            @Suppress("DEPRECATION")
            activity?.sendBroadcast(Intent("com.android.launcher.action.INSTALL_SHORTCUT").apply {
              putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
              putExtra(Intent.EXTRA_SHORTCUT_INTENT, Intent(Intent.ACTION_VIEW, Uri.parse(url)))
              putExtra(Intent.EXTRA_SHORTCUT_ICON, it)
            })
            Toast.makeText(context, R.string.shortcut_created, Toast.LENGTH_SHORT).show()
          }
        })
    homeViewModel.getBitmapFromUrl(context ?: return, url)
  }

  override fun onThumbnailUpdateClick(ogpView: ImageView, url: String?, id: Long) {
    analyticsUtils.logHomeTap("thumbnail update")
    homeViewModel.updateImage(ogpView, id, url)
    Toast.makeText(context, R.string.toast_thumbnail_reload, Toast.LENGTH_SHORT).show()
  }

  override fun onRowClick(id: Long) = homeViewModel.setParentId(id, preferences)

  override fun onStartDrag(holder: ViewHolder) = itemTouchHelper.startDrag(holder)

  override fun onSetSortMode() = sort(start = true, isSave = false)

  fun updateItem(itemId: Long, itemName: String, itemUrl: String?) =
    homeViewModel.updateItem(itemId, itemName, itemUrl)

  fun moveItem(selfId: Long, parentId: Long) = homeViewModel.moveItem(selfId, parentId)

  fun backPress() {
    if (homeViewModel.sorting.value == true) {
      (activity as HomeActivity).showSnackbar(getString(R.string.snackbar_sort_cancel_message))
      sort(start = false, isSave = false)
      return
    }

    if (homeViewModel.searchText.value != null) {
      (activity as HomeActivity).closeSearch()
      return
    }

    if (homeViewModel.breadcrumbs.value?.size == 1) {
      activity?.finish()
    } else {
      homeViewModel.setParentId(
          homeViewModel.breadcrumbs.value?.size?.minus(2)?.let {
            homeViewModel.breadcrumbs.value?.get(it)?.first
          } ?: 0, preferences
      )
    }
  }

  fun sort(start: Boolean, isSave: Boolean) {
    if (isSave) {
      analyticsUtils.logResult("sort", "success")
      (activity as HomeActivity).showSnackbar(getString(R.string.snackbar_sort_complete_message))
      homeViewModel.sortSave(itemListAdapter.getItems())
    } else {
      homeViewModel.sortModeChange(start)
    }
    (activity as HomeActivity).setSortMode(start)
  }

  fun reloadItems(isImageUpdate: Boolean) {
    if (isImageUpdate) {
      context?.let {
        Glide.get(it).clearMemory()
      }
    } else {
      homeViewModel.endSearch()
      binding.itemsRefresh.isEnabled = true
    }
    homeViewModel.getItems()
  }

  fun setFirstFolder() = homeViewModel.breadcrumbs.value?.last()?.let {
    preferences.startFolderId = it.first
    getString(R.string.set_first_folder_done, it.second)
  } ?: getString(R.string.set_first_folder_error)

  fun saveUserData(email: String, uid: String, token: String) {
    isFirstLoad = true
    homeViewModel.sendUserData(email, uid, token)
  }

  fun initializeInsert() = homeViewModel.initializeInsert()

  fun setLoadingShow(isShow: Boolean) = homeViewModel.setLoadingShow(isShow)

  fun hideBreadcrumbs() {
    binding.itemsRefresh.isEnabled = false
    homeViewModel.hideBreadcrumbs()
  }

  fun searchItems(text: String) = homeViewModel.searchItems(text)

  fun getSearchText() = homeViewModel.searchText.value

  private fun folderSelectDialogShow(selfId: Long, folderList: List<Item>) =
    (activity as HomeActivity).onMove(selfId, folderList)

  private fun targetImageUpdate() {
    val syncTarget = preferences.imageSyncTarget
    if (!syncTarget.isNullOrEmpty()) {
      Moshi.Builder().build().adapter(Map::class.java).fromJson(syncTarget)?.forEach {
        context?.let { context ->
          ContextCompat.startForegroundService(
              context,
              Intent(context, UpdateImageService::class.java).apply {
                putExtra(UpdateImageService.PARAM_ITEM_ID, it.key.toString().toLong())
                putExtra(UpdateImageService.PARAM_ITEM_URL, it.value.toString())
              })
        }
      }
    }
    preferences.imageSyncTarget = ""
  }

  private val itemTouchHelper by lazy {
    val simpleItemTouchCallback =
      object : ItemTouchHelper.SimpleCallback(
          ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
          0
      ) {

        override fun onMove(
          recyclerView: RecyclerView,
          viewHolder: ViewHolder,
          target: ViewHolder
        ): Boolean {
          (recyclerView.adapter as ItemListAdapter).moveItem(
              viewHolder.adapterPosition,
              target.adapterPosition
          )
          return false
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {}
      }

    ItemTouchHelper(simpleItemTouchCallback)
  }

  companion object {
    const val SHARE_USER_ACTIVITY_REQUEST = 2001
  }
}