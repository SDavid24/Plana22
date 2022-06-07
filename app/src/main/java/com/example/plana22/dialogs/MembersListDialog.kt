package com.example.plana22.dialogs

import adapters.MembersListItemsAdapter
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plana22.Models.User
import com.example.plana22.R
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class MembersListDialog(
    context: Context,
    private val list: ArrayList<User>,
    private val title : String = ""
) : Dialog(context){

    private var adapter :  MembersListItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View)
  /*
    {
        view.tvTitle_dialog_list.text = title

        if (list.size > 0) {
            view.rvList.layoutManager = LinearLayoutManager(context)
            adapter = MembersListItemsAdapter(context, list)
            view.rvList.adapter = adapter

            adapter!!.onclickListener = object : MembersListItemsAdapter.OnclickListener{
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss()
                    onItemSelected(user, action)
                }

            }
        }
    }*/

    {
        view.tvTitle_dialog_list.text = title

        if (list.size > 0) {

            view.rvList.layoutManager = LinearLayoutManager(context)
            adapter = MembersListItemsAdapter(context, list)
            view.rvList.adapter = adapter

            adapter!!.onclickListener = object :
                MembersListItemsAdapter.OnclickListener {
                override fun onClick(position: Int, user: User, action:String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            }
        }
    }

    protected abstract fun onItemSelected(user: User, action: String)

}