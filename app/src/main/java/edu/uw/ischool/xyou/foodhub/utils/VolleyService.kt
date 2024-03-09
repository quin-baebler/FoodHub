package edu.uw.ischool.xyou.foodhub.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyService private constructor(ctx: Context) { //private constructor; cannot instantiate directly
    companion object {
        private var instance: VolleyService? = null //the single instance of this singleton

        //call this "factory" method to access the Singleton
        fun getInstance(ctx: Context): VolleyService {
            //only create the singleton if it doesn't exist yet
            if (instance == null) {
                instance = VolleyService(ctx)
            }

            return instance as VolleyService //force casting
        }
    }

    //from Kotlin docs
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(ctx.applicationContext) //return the context-based requestQueue
    }

    //convenience wrapper method
    fun <T> add(req: Request<T>) {
        requestQueue.add(req)
    }

}