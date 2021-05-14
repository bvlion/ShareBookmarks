package net.ambitious.android.sharebookmarks.ui.share.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.LinearLayout
import android.widget.TextView
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.contact.Contact
import net.ambitious.android.sharebookmarks.ui.share.adapter.ShareUserListAdapter.OnUserCompleteListener

class ContactUserArrayAdapter constructor(
  context: Context,
  private val allContacts: List<Contact>,
  private val listener: OnUserCompleteListener
) : ArrayAdapter<Contact>(
  context,
  android.R.layout.simple_list_item_2,
  allContacts
) {

  private val _contacts = arrayListOf<Contact>()
  private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

  init {
    _contacts.addAll(allContacts)
  }

  override fun getCount() = _contacts.size

  override fun getItem(position: Int) = _contacts[position]

  override fun getFilter() = ArrayFilter(this, _contacts, allContacts)

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
    (convertView ?: inflater.inflate(
      R.layout.row_contact_candidate,
      parent,
      false
    )).apply {
      findViewById<TextView>(R.id.name).text = _contacts[position].displayName
      findViewById<TextView>(R.id.email).text = _contacts[position].email
      findViewById<LinearLayout>(R.id.user_layout).setOnClickListener {
        listener.onEditDone(_contacts[position])
      }
    }

  class ArrayFilter(
    private val adapter: ArrayAdapter<Contact>,
    private val contacts: ArrayList<Contact>,
    private val allContacts: List<Contact>
  ) : Filter() {
    override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
      allContacts.filter { it.email.contains(constraint ?: "") }.let { filtered ->
        values = filtered
        contacts.clear()
        contacts.addAll(filtered)
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