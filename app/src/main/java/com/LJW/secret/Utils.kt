package com.LJW.secret

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun TextView.message()=this.text.toString()

inline fun TextView.addTextChangedListener(bridge: EditTextBridge.()->Unit)=addTextChangedListener(EditTextBridge().apply (bridge))

class EditTextBridge: TextWatcher {
    private var beforeTextChanged: ((CharSequence?, Int, Int, Int) ->  Unit)? = null
    private var onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var afterTextChanged: ((Editable?) ->  Unit)? = null
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { beforeTextChanged?.invoke(s, start, count, after) }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { onTextChanged?.invoke(s, start, before, count) }
    override fun afterTextChanged(s: Editable?) { afterTextChanged?.invoke(s) }
    fun beforeTextChanged(listener: (CharSequence?, Int, Int, Int) ->  Unit) { beforeTextChanged = listener }
    fun onTextChanged(listener: (CharSequence?, Int, Int, Int) ->  Unit) { onTextChanged = listener }
    fun afterTextChanged(listener: (Editable?) ->  Unit) { afterTextChanged = listener }
}
inline fun Spinner.setOnItemSelectedListener(bridge:SpinnerBridge.()->Unit) = setOnItemSelectedListener(SpinnerBridge().apply(bridge))

class SpinnerBridge: AdapterView.OnItemSelectedListener {
    private var onItemSelected:((AdapterView<*>?,View?,Int,Long) -> Unit)?=null
    private var onNothingSelected:((AdapterView<*>?) -> Unit)?= null
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { onItemSelected?.invoke(p0, p1, p2, p3) }
    override fun onNothingSelected(p0: AdapterView<*>?) { onNothingSelected?.invoke(p0) }
    fun onItemSelected(listener:(AdapterView<*>?,View?,Int,Long) -> Unit) { onItemSelected=listener }
    fun onNothingSelected(listener:(AdapterView<*>?) -> Unit){ onNothingSelected=listener }
}

/*
*网络传输
 */
object NetworkServerCreator{
    val retrofit= Retrofit.Builder()
        .baseUrl("https://2xrnknq21l5m.hk1.xiaomiqiu123.top")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    inline fun <reified T> create():T=retrofit.create(T::class.java)
}
suspend fun <T : Any> Call<T>.await():T{
    return suspendCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(p0: Call<T>, p1: Response<T>) {
                val body=p1.body()
                if(body!=null)
                    continuation.resume(body)
                else continuation.resumeWithException(RuntimeException("return empty"))
            }

            override fun onFailure(p0: Call<T>, p1: Throwable) {
                continuation.resumeWithException(p1)
            }
        })
    }
}

fun replaceFragment(fragmentManager: FragmentManager,resourceID:Int,fragment:Fragment,isAddToStack:Boolean){
    var beginTransaction = fragmentManager.beginTransaction()
    beginTransaction.replace(resourceID,fragment)
    if (isAddToStack) beginTransaction.addToBackStack(null)
    beginTransaction.commit()
}

object ActivityCollector{
    private val activities=ArrayList<Activity>()
    fun addActivity(activity: Activity){ activities.add(activity) }
    fun removeActivity(activity: Activity){ activities.remove(activity) }
    fun finishAll(){ activities.forEach{if (!it.isFinishing) it.finish()} }
}
