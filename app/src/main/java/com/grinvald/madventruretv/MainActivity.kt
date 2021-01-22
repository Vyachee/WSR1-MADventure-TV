package com.grinvald.madventruretv

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.grinvald.madventruretv.common.CacheHelper

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed(Runnable {

            if(CacheHelper(this).getToken().equals("null")) {
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            }   else {
                val intent = Intent(this, MyQuests::class.java)
                startActivity(intent)
            }
            finish()

        }, 2000)
    }
}