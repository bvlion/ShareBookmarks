package net.ambitious.android.sharebookmarks.ui.share.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import net.ambitious.android.sharebookmarks.data.local.contact.Contact

class ContactUserArrayAdapter constructor(
  context: Context,
  private val contacts: List<Contact>
) : ArrayAdapter<String>(
    context,
    android.R.layout.simple_dropdown_item_1line,
    contacts.map { it.email }) {

  override fun getFilter() = ArrayFilter(this, contacts)

  class ArrayFilter(
    private val adapter: ArrayAdapter<String>,
    private val contacts: List<Contact>
  ) : Filter() {
    override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
      contacts.filter { it.email.startsWith(constraint ?: "") }.let { filtered ->
        values = filtered.map { it.email }
        count = filtered.size
      }
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
      if (results != null && results.count > 0) {
        adapter.notifyDataSetChanged()
      } else {
        adapter.notifyDataSetInvalidated()
      }
    }
  }
}