package com.grinvald.madventruretv

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.grinvald.madventruretv.common.CacheHelper
import org.json.JSONObject

class SignIn : Activity() {

    lateinit var et_email: EditText
    lateinit var et_password: EditText
    lateinit var tv_signin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initViews()

        tv_signin.setOnClickListener(View.OnClickListener {

            val email = et_email.text.toString()
            val password = et_password.text.toString()

            tryAuth(email, password)

        })
    }

    fun initViews() {
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        tv_signin = findViewById(R.id.tv_signin)
    }

    fun tryAuth(email: String, password: String) {

        val queue = Volley.newRequestQueue(this)
        val request = object: StringRequest(
            Request.Method.POST,
            "http://wsk2019.mad.hakta.pro/api/user/login",
            Response.Listener { response ->
                Log.d("DEBUG", "response: $response")
                try {
                    val j = JSONObject(response)

                    if(j.has("token")){
                        CacheHelper(this).saveToken(j.getString("token"))
                        val intent = Intent(this, MyQuests::class.java)
                        startActivity(intent)
                        finish()
                    }

                }   catch (e: Exception) {}
            },
            Response.ErrorListener { error ->
                Log.d("DEBUG", "error: $error")

            }
        ) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val r = JSONObject()
                r.put("email", email)
                r.put("password", password)
                return r.toString().toByteArray()
            }
        }

        queue.add(request)

    }

}