package com.grinvald.madventruretv.common

import android.content.Context
import com.grinvald.grinvaldmadventure.models.Category
import com.grinvald.madventruretv.models.QuestItem

class InternetHelper(context: Context) {
    val context = context

    fun getCategories(questList: MutableList<QuestItem>) : MutableList<Category> {

        val list : MutableList<Category> = mutableListOf()

        for(x in questList) {
            val category = x.category
            list.add(category)
        }

        return list

    }

}