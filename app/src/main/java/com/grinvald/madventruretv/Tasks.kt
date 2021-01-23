package com.grinvald.madventruretv

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grinvald.madventruretv.adapter.TasksAdapter
import com.grinvald.madventruretv.models.QuestItem

class Tasks : Activity() {

    lateinit var extras : Bundle

    lateinit var tv_back : TextView
    lateinit var tv_quest_title : TextView
    lateinit var tv_global_online : TextView
    lateinit var rv_tasks : RecyclerView

    lateinit var quest : QuestItem

    var online = 0

    fun addOnline(onlineToAdd: Int) {
        online += onlineToAdd
        tv_global_online.text = "$online players online"
    }

    fun initViews() {
        tv_back = findViewById(R.id.tv_back)
        rv_tasks = findViewById(R.id.rv_tasks)
        tv_quest_title = findViewById(R.id.tv_quest_title)
        tv_global_online = findViewById(R.id.tv_global_online)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        extras = intent.extras!!
        quest = extras.getSerializable("quest") as QuestItem

        initViews()
        initHandlers()
        initData()
    }

    fun initHandlers() {
        tv_back.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    fun initData() {
        tv_quest_title.text = quest.name

        val tasksAdapter = TasksAdapter(quest.tasks, this)
        val layoutManager = GridLayoutManager(this, 3)

        rv_tasks.adapter = tasksAdapter
        rv_tasks.layoutManager = layoutManager
    }
}