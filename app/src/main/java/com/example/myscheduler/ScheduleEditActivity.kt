package com.example.myscheduler

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import com.example.myscheduler.databinding.ActivityScheduleEditBinding
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ScheduleEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleEditBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        realm = Realm.getDefaultInstance()

        val scheduleId = intent?.getLongExtra("schedule_id", -1L)
        if (scheduleId != -1L){
            val schedule = realm.where<Schedule>()
                .equalTo("id", scheduleId).findFirst()
            binding.dateEdit.setText(DateFormat.format("yyyy/MM/dd", schedule?.date))
            binding.titleEdit.setText(schedule?.title)
            binding.detailEdit.setText(schedule?.detail)
            //新規作成ではない時はdeleteボタンを表示にする
            binding.delete.visibility = View.VISIBLE
        }else{
            binding.delete.visibility = View.INVISIBLE
        }

        binding.save.setOnClickListener{view: View ->
             when(scheduleId) {
                 -1L -> {
                     //DBに通信を行う
                     realm.executeTransactionAsync { db: Realm ->
                         val maxId = db.where<Schedule>().max("id")//最大IDを取得
                         val nextId = (maxId?.toLong() ?: 0L) + 1//次に作るcellのIDを作成
                         val schedule = db.createObject<Schedule>(nextId)
                         val date = binding.dateEdit.text.toString().toDate("yyyy/MM/dd")
                         if (date != null) schedule.date = date
                         schedule.title = binding.titleEdit.text.toString()
                         schedule.detail = binding.detailEdit.text.toString()
                     }
                     Snackbar.make(view, "追加しました", Snackbar.LENGTH_SHORT)
                         .setAction("戻る") { finish() }
                         .setActionTextColor(Color.YELLOW)
                         .show()
                 }
                 else -> {
                     realm.executeTransactionAsync { db:Realm->
                         val schedule = db.where<Schedule>()
                             .equalTo("id", scheduleId).findFirst()
                         val date = binding.dateEdit.text.toString().toDate("yyyy/MM/dd")
                         if (date != null) schedule?.date = date
                         schedule?.title = binding.titleEdit.text.toString()
                         schedule?.detail = binding.detailEdit.text.toString()
                     }
                     Snackbar.make(view, "修正しました", Snackbar.LENGTH_SHORT)
                         .setAction("戻る") { finish() }
                         .setActionTextColor(Color.YELLOW)
                         .show()
                 }
             }
        }
        binding.delete.setOnClickListener{view: View->
            realm.executeTransactionAsync{db: Realm->
                db.where<Schedule>().equalTo("id", scheduleId)
                    ?.findFirst()?.deleteFromRealm()
            }
            Snackbar.make(view, "削除しました", Snackbar.LENGTH_SHORT)
                .setAction("戻る") { finish() }
                .setActionTextColor(Color.YELLOW)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date?{
        return try {
            SimpleDateFormat(pattern).parse(this)
        }catch (e: IllegalArgumentException) {
            return null
        }catch (e: ParseException) {
            return null
        }
    }
}