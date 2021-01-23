package com.grinvald.madventruretv

import android.app.Activity
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.grinvald.grinvaldmadventure.models.Task
import com.grinvald.madventruretv.common.CacheHelper
import com.grinvald.madventruretv.common.MapHelper
import com.grinvald.madventruretv.databinding.ActivityMapBinding
import com.grinvald.madventruretv.models.Player
import org.json.JSONArray

class MapActivity : Activity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var tv_online: TextView
    private lateinit var tv_back: TextView
    private lateinit var binding: ActivityMapBinding
    private lateinit var task: Task
    private lateinit var extras: Bundle
    private lateinit var playerList: MutableList<Player>

    fun initViews() {
        tv_online = findViewById(R.id.tv_online)
        tv_back = findViewById(R.id.tv_back)
        mapView = findViewById(R.id.mapView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extras = intent.extras!!
        task = extras.getSerializable("task") as Task

        initViews()

        tv_back.setOnClickListener(View.OnClickListener {
            finish()
        })


        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
    }

    fun initMarkers() {
        for(x in playerList) {
            val sydney = LatLng(x.lat, x.lon)

            mMap.addMarker(MarkerOptions().position(sydney)
                .icon(BitmapDescriptorFactory.fromBitmap(
                    MapHelper(baseContext).createCustomMarker(x)
                )))
        }

        if(playerList.size > 0) {
            val lastPlayer = playerList.last()
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lastPlayer.lat, lastPlayer.lon)))
        }
    }

    fun getPlayers() {
        val queue = Volley.newRequestQueue(baseContext)

        val mStringRequest = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/tasks/${task.id}/participants",
            Response.Listener { response ->
                val o = JSONArray(response)

                val list : MutableList<Player> = mutableListOf()

                for(x in 0 until o.length()) {
                    val player: Player = Gson().fromJson(o.getJSONObject(x).toString(), Player::class.java)
                    list.add(player)
                }
                this.playerList = list
                initMarkers()
                tv_online.text = "${playerList.size} players online"

            },
            Response.ErrorListener { error ->
                Log.d("DEBUG", "error: ${error.message}")
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(baseContext).getToken())
                return m
            }
        }
        queue.add(mStringRequest)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getPlayers()
    }
}