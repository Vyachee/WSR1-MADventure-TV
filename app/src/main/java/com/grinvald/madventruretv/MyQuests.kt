package com.grinvald.madventruretv

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.grinvald.madventruretv.adapter.BestQuestsAdapter
import com.grinvald.madventruretv.common.CacheHelper
import com.grinvald.madventruretv.common.InternetHelper
import com.grinvald.madventruretv.models.Profile
import com.grinvald.madventruretv.models.QuestItem
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MyQuests : Activity() {

    lateinit var ll_search_btn : LinearLayout
    lateinit var s_categories : Spinner
    lateinit var et_search : EditText
    lateinit var rv_quests : RecyclerView
    lateinit var iv_avatar : ImageView
    lateinit var tv_nickname : TextView
    lateinit var tv_logout : TextView


    lateinit var filter_category : String
    lateinit var questList : MutableList<QuestItem>
    lateinit var filteredList : MutableList<QuestItem>
    lateinit var profile : Profile


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_quests)

        initViews()
        getQuests()
        getProfile()

        tv_logout.setOnClickListener(View.OnClickListener {

            CacheHelper(baseContext).removeToken()
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()

        })

        ll_search_btn.setOnClickListener(View.OnClickListener {
            filter()
        })

    }

    fun initAdapter(list: MutableList<QuestItem>) {

        val adapter = BestQuestsAdapter(list, applicationContext)
        val layoutManager = GridLayoutManager(baseContext, 3)

        rv_quests.adapter = adapter
        rv_quests.layoutManager = layoutManager

    }

    fun filter() {
        val wordsFilter = et_search.text.toString()
        filteredList = mutableListOf()
        for(x in questList) {
            if(x.name.contains(wordsFilter) && x.description.contains(wordsFilter)) {
                if(x.category.name.contains(filter_category)) {
                    filteredList.add(x)
                }
            }
        }

        initAdapter(filteredList)
    }

    fun initProfile() {
        var name = ""
        if(profile.firstName.isNullOrEmpty() || profile.lastName.isNullOrEmpty()) {
            name = profile.nickName
        }   else name = "${profile.firstName} ${profile.lastName}"

        tv_nickname.text = name
        Picasso.get().load(profile.avatar).into(iv_avatar)
    }

    fun getProfile() {
        val queue = Volley.newRequestQueue(baseContext)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/user/profile",
            Response.Listener { response ->

                val json = JSONObject(response).getJSONObject("content").toString()
                val profile: Profile = Gson().fromJson(json, Profile::class.java)
                this.profile = profile
                initProfile()
            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(baseContext).getToken()
                return headers
            }
        }

        queue.add(request)

    }

    fun initSpinner(quests: MutableList<QuestItem>) {
        val categories = InternetHelper(baseContext).getCategories(quests)

        val data = mutableListOf<String>()

        for(x in categories)
            if(!data.contains(x.name))
                data.add(x.name)

        val arrayAdapter = ArrayAdapter<String>(baseContext, R.layout.spinner_item, data)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s_categories.prompt = "Title"
        s_categories.adapter = arrayAdapter
        s_categories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                filter_category = data.get(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun getQuests() {
        val queue = Volley.newRequestQueue(this)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/quests",
            Response.Listener { response ->

                val json = JSONObject(response).getJSONArray("content")

                val list: MutableList<QuestItem> = mutableListOf()

                for (i in 0..json.length() - 1) {
                    val item = json.getJSONObject(i)
                    val questItem = Gson().fromJson(item.toString(), QuestItem::class.java)
                    list.add(questItem)
                }

                questList = list

                initAdapter(questList)
                initSpinner(list)


            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(baseContext).getToken()
                return headers
            }
        }

        queue.add(request)
    }

    fun initViews() {
        ll_search_btn = findViewById(R.id.ll_search_btn)
        s_categories = findViewById(R.id.s_categories)
        et_search = findViewById(R.id.et_search)
        rv_quests = findViewById(R.id.rv_quests)
        iv_avatar = findViewById(R.id.iv_avatar)
        tv_nickname = findViewById(R.id.tv_nickname)
        tv_logout = findViewById(R.id.tv_logout)
    }
}