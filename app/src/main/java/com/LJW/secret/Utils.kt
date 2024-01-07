package com.ljw.secret

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import okhttp3.Cookie

import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun buildCookie(name:String, value:String) = Cookie.Builder().name(name).value(value).domain("www").build()
fun getRandomString(length:Int):String{
    val random= Random()
    val buffer=StringBuffer()
    for (i in 0..length-1)
        buffer.append(BASIC_LETTER[random.nextInt(52)])
    return buffer.toString()
}

fun <T> T.POJO2Map()= JSON.parseObject(JSON.toJSONString(this),HashMap::class.java) as HashMap<String,String>

inline fun <reified T> Map<String,String>.Map2POJO():T{
    val gson= Gson()
    return gson.fromJson(gson.toJson(this), T::class.java)
}

fun Long.toTime(formater:String=""):String{
    val simpleDateFormat=SimpleDateFormat(formater, Locale.CHINA)
    simpleDateFormat.timeZone= TimeZone.getTimeZone("GMT+8")
    if (formater.isNotEmpty()) return simpleDateFormat.format(Date(this))
    return ""
}

fun <T> T.toast() =Toast.makeText(Env.context,"${this}",Toast.LENGTH_LONG).show()
fun TextView.msg()=this.text.toString().trim()

fun <T> Spinner.config(list: List<T>,itemSelect:(T)->Unit){
    val dataArrayList = ArrayList(list)
    val spinnerAdapter= ArrayAdapter(Env.context,R.layout.spinner_prompt,dataArrayList)
    spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown)
    this.adapter=spinnerAdapter
    this.setOnItemSelectedListener {
        onItemSelected{_,_,pos,_ ->
            itemSelect(dataArrayList[pos])
        }
    }
    this.setSelection(0)
}

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

    var retrofit : Retrofit
    init {
        Log.e("NetworkServerCreator.()","init")
        val okHttp = OkHttpClient.Builder().cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                COOKIE_LIST.removeIf { it.name() == "JSESSIONID" }
                COOKIE_LIST.add(cookies.last { it.name() == "JSESSIONID" })
                cookies.forEach {
                    if (!it.name().equals("JSESSIONID")) COOKIE_LIST.add(it)
                }
            }

            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                if (COOKIE_MAP["firstTime"] == null) {
                    val currentTime = System.currentTimeMillis()
                    COOKIE_LIST.add(buildCookie("firstTime","${currentTime}"))
                    COOKIE_LIST.add(buildCookie("lastTime","${currentTime}"))
                    COOKIE_MAP["firstTime"] = currentTime
                    COOKIE_MAP["lastTime"] = currentTime
                }else{
                    COOKIE_LIST.removeIf { it.name() == "lastTime" }
                    COOKIE_LIST.add(buildCookie("lastTime","${System.currentTimeMillis()}"))
                }
                COOKIE_LIST.forEach { Log.e("NetworkServerCreator.saveFromResponse", "${it.name()}  ${it.value()}") }
                return COOKIE_LIST
            }
        }).build()
        retrofit= Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp)
            .build()
    }

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

fun replaceFragment(
    fragmentManager: FragmentManager,
    resourceID:Int,
    fragment:Fragment,
    isAddToStack:Boolean = false
){
    val beginTransaction = fragmentManager.beginTransaction()
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

