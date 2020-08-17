package net.ambitious.android.sharebookmarks.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_home.items_recycler_view
import kotlinx.android.synthetic.main.fragment_home.items_refresh
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.ui.home.ItemListAdapter.OnItemClickListener
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), OnItemClickListener {

  private val homeViewModel by viewModel<HomeViewModel>()
  private val preferences: PreferencesUtils.Data by inject()
  private lateinit var adapter: ItemListAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_home, container, false).apply {
    homeViewModel.items.observe(
        viewLifecycleOwner,
        Observer {
          adapter.setItems(it)
          adapter.notifyDataSetChanged()
          items_refresh.isRefreshing = false
        })
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    context?.let {
      adapter = ItemListAdapter(it, this)
      items_recycler_view.layoutManager = GridLayoutManager(context, 2)
      items_recycler_view.adapter = adapter
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
      startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
      if (preferences.closeApp) {
        activity?.finish()
      }
    }
  }

  override fun onDeleteClick(itemId: Long) {
    // TODO
    Toast.makeText(context, "onDeleteClick", Toast.LENGTH_SHORT).show()
  }

  override fun onEditClick(item: Item) =
    (activity as HomeActivity).onEdit(item)

  override fun onMoveClick(itemId: Long) {
    // TODO
    Toast.makeText(context, "onMoveClick", Toast.LENGTH_SHORT).show()
  }

  override fun onShareClick(itemId: Long) {
    // TODO
    Toast.makeText(context, "onShareClick", Toast.LENGTH_SHORT).show()
  }

  override fun onCreateShortcut(itemId: Long) {
    // TODO
    Toast.makeText(context, "onCreateShortcut", Toast.LENGTH_SHORT).show()
  }

  override fun onThumbnailUpdateClick(itemId: Long) {
    // TODO
    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
  }

  fun updateItem(itemId: Long, itemName: String, itemUrl: String?) {
    homeViewModel.updateItem(itemId, itemName, itemUrl)
  }
}