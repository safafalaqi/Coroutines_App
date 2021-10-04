package com.example.coroutinesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var advice:Advice? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvAdvice= findViewById<TextView>(R.id.tvAdvice)
        val btAdvice= findViewById<Button>(R.id.btAdvice)
        val btPause=findViewById<Button>(R.id.btPause)

        tvAdvice.text ="New Advice"

        var pressed:Boolean
        btAdvice.setOnClickListener{
            pressed=true
            CoroutineScope(Dispatchers.IO).launch {
                while(pressed) {
                    getApiResult()
                    withContext(Dispatchers.Main) {
                        if(advice?.slip?.advice.toString()!="null")
                        tvAdvice.text = advice?.slip?.advice.toString()
                    }
                }
            }
        }
        btPause.setOnClickListener{
            pressed=false
        }

    }


    suspend fun getApiResult(){

        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

        val call: Call<Advice?>? = apiInterface!!.doGetListResources()

        call?.enqueue(object : Callback<Advice?> {
            override fun onResponse(
                call: Call<Advice?>?,
                response: Response<Advice?>
            ) {

                advice= response.body()
            }

            override fun onFailure(call: Call<Advice?>, t: Throwable) {
                Toast.makeText(applicationContext, "" + t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }

        })
        delay(100L)
   }
}