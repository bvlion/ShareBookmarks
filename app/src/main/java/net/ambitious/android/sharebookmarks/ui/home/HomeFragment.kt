package net.ambitious.android.sharebookmarks.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_home.items_recycler_view
import kotlinx.android.synthetic.main.fragment_home.items_refresh
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.ShareBookmarksModelFactory
import net.ambitious.android.sharebookmarks.ui.home.ItemListAdapter.OnItemClickListener

class HomeFragment : Fragment(), OnItemClickListener {

  private lateinit var homeViewModel: HomeViewModel
  private lateinit var adapter: ItemListAdapter

  private var parentId: Long = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_home, container, false).apply {
    ViewModelProvider(
        this@HomeFragment,
        ShareBookmarksModelFactory.getInstance(context)
    ).get(HomeViewModel::class.java)
        .also { homeViewModel = it }.items.observe(
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
      homeViewModel.getItems(parentId)

      items_refresh.setOnRefreshListener {
        homeViewModel.getItems(parentId)
      }
    }
  }

  override fun onRowClick() {
    // TODO
    Toast.makeText(context, "onRowClick", Toast.LENGTH_SHORT).show()
  }

  override fun onDeleteClick() {
    // TODO
    Toast.makeText(context, "onDeleteClick", Toast.LENGTH_SHORT).show()
  }

  override fun onEditClick() {
    // TODO
    Toast.makeText(context, "onEditClick", Toast.LENGTH_SHORT).show()
  }

  override fun onMoveClick() {
    // TODO
    Toast.makeText(context, "onMoveClick", Toast.LENGTH_SHORT).show()
  }

  override fun onShareClick() {
    // TODO
    Toast.makeText(context, "onShareClick", Toast.LENGTH_SHORT).show()
  }

  override fun onCreateShortcut() {
    // TODO
    Toast.makeText(context, "onCreateShortcut", Toast.LENGTH_SHORT).show()
  }

  override fun onThumbnailUpdateClick() {
    // TODO
    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
  }
}