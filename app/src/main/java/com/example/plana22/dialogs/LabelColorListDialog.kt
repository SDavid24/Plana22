package com.example.plana22.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plana22.Adapters.LabelColorListItemsAdapter
import com.example.plana22.R
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class LabelColorListDialog(
    context: Context,
    private val list: ArrayList<String>,
    private val title: String,
    private val mSelectedColor:  String
    ) : Dialog(context) {

    var adapter : LabelColorListItemsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)

        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View){
        view.tvTitle_dialog_list.text = title

        view.rvList.layoutManager  = LinearLayoutManager(context)

        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        view.rvList.adapter = adapter

        //view.rvList.setHasFixedSize(true)

        adapter!!.onclickListener = object : LabelColorListItemsAdapter.OnclickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }
    }

    protected abstract fun onItemSelected(color: String)

}