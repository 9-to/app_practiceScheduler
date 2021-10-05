package com.example.myscheduler

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter



class ScheduleAdapter(data: OrderedRealmCollection<Schedule>): RealmRecyclerViewAdapter<Schedule,ScheduleAdapter.ViewHolder>(data,true){

    private var listener: ((Long?)-> Unit)? = null

    fun setOnItemClickListener(listener:(Long?)-> Unit){
        this.listener = listener
    }

    init{
        //不変なIDを設定する
        setHasStableIds(true)
    }

    class ViewHolder(cell: View): RecyclerView.ViewHolder(cell){
        val date: TextView = cell.findViewById(android.R.id.text1)
        val title: TextView = cell.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAdapter.ViewHolder {
        /*
        * RecycleHolderが新しいViewHolderを必要とするときに呼び出される
        * */
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleAdapter.ViewHolder, position: Int) {
        /*
        * データを取り出して表示するためのメソッド
        * holder:更新する対象
        * position:更新に使用するデータのアダプターデータセット内における位置
        * */
        val schedule: Schedule? = getItem(position)
        holder.date.text = DateFormat.format("yyyy/MM/dd", schedule?.date)
        holder.title.text = schedule?.title
         holder.itemView.setOnClickListener{
             listener?.invoke(schedule?.id)
         }
    }

    override fun getItemId(position: Int): Long {
        /*
        * データ項目のIDを返す
        * */
        return getItem(position)?.id ?: 0
    }

}