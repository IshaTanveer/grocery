package com.grocery.groceryhub

import android.app.Activity
import android.content.Context
import android.content.Intent

class StartNewActivity {
    fun startingActivity(context: Context, target: Class<out Activity>){
        val intent = Intent(context, target)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }
    fun nextActivity(context: Context, target: Class<out Activity>){
        val intent = Intent(context, target)
        context.startActivity(intent)
    }
}