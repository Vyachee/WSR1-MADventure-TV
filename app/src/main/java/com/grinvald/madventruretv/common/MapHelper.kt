package com.grinvald.madventruretv.common

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.model.Circle
import com.grinvald.madventruretv.R
import com.grinvald.madventruretv.models.Player
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*


class MapHelper(var context: Context) {
    fun createCustomMarker(p: Player) : Bitmap {
        val inflater = LayoutInflater.from(context)
        val v: View = inflater.inflate(R.layout.custom_market, null, false)

        val tv_name = v.findViewById<TextView>(R.id.tv_name)
        val iv_avatar= v.findViewById<CircleImageView>(R.id.iv_avatar)

        val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val markerDate = simpleDataFormat.parse(p.date)
        val currentDate = Date()

        val timeDifference = currentDate.time - markerDate.time

        if(timeDifference <= 900) {
            v.alpha = 1f
        }   else {
            var alpha = 1f
            for(x in 0..timeDifference step 900)
                alpha -= 0.1f

            if(alpha <= 0) alpha = 0.1f
            v.alpha = alpha
        }

        tv_name.text = p.name
        Picasso.get().load(p.avatar).into(iv_avatar)

        v.isDrawingCacheEnabled = true
        v.buildDrawingCache()
        v.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        val b = v.getDrawingCache()
        return b
    }
}