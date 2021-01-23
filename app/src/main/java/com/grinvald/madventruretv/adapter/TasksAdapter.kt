package com.grinvald.madventruretv.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.grinvald.grinvaldmadventure.models.Task
import com.grinvald.madventruretv.R
import com.grinvald.madventruretv.Tasks
import com.grinvald.madventruretv.common.CacheHelper
import com.grinvald.madventruretv.models.QuestItem
import com.grinvald.madventruretv.models.TaskDetails
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class TasksAdapter(questsList: MutableList<Task>, context: Context) : RecyclerView.Adapter<TasksAdapter.Holder>() {

    var questsList = questsList
    var context = context

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val v = itemView

        val iv_quest_preview : ImageView = v.findViewById<ImageView>(R.id.iv_quest_preview)
        val tv_title : TextView = v.findViewById<TextView>(R.id.tv_quest_title)
        val tv_description : TextView = v.findViewById<TextView>(R.id.tv_quest_description)
        val tv_online : TextView = v.findViewById<TextView>(R.id.tv_online)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.task, parent, false)
        return Holder(view)

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val task = questsList.get(position)

        getTaskDetails(task, holder)
        getOnline(task.id, holder)

    }

    fun getOnline(taskId: String, holder: Holder) {
        val queue = Volley.newRequestQueue(context)

        val mStringRequest = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/tasks/$taskId/participants",
            Response.Listener { response ->
                val o = JSONArray(response)
                val online = o.length()
                holder.tv_online.text = "$online players online"
                (context as Tasks).addOnline(online)
            },
            Response.ErrorListener { error ->
                Log.d("DEBUG", "error: ${error.message}")
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(context).getToken())
                return m
            }
        }
        queue.add(mStringRequest)
    }

    fun getTaskDetails(task: Task, holder: Holder) {

        val queue = Volley.newRequestQueue(context)

        val mStringRequest = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/tasks/${task.id}",
            Response.Listener { response ->
                val o = JSONObject(response).getJSONObject("content")
                val taskDetails : TaskDetails = Gson().fromJson(o.toString(), TaskDetails::class.java)

                var description = taskDetails.description

                if(description.length > 100)
                    description = description.substring(0, 100)


                holder.tv_description.text = description
                holder.tv_title.text = taskDetails.name

                if(taskDetails.photos.size > 0)
                    Picasso.get().load(taskDetails.photos.get(0)).into(holder.iv_quest_preview)



            },
            Response.ErrorListener { error ->
                Log.d("DEBUG", "error: ${error.message}")
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(context).getToken())
                return m
            }
        }
        queue.add(mStringRequest)
    }

    override fun getItemCount(): Int {
        return questsList.size
    }

}
