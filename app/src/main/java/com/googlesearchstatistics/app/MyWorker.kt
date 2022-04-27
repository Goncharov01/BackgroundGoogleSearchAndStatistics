package com.googlesearchstatistics.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.googlesearchstatistics.app.db.DataBaseBuilder
import com.googlesearchstatistics.app.db.DataLink
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.*

open class MyWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val context = applicationContext

    private val dataBase = DataBaseBuilder.getInstans(this.context).dataLinksDao()
    private val links: MutableList<String> = mutableListOf()
    private var random: Random = Random()

    private val APP_PREFERENCES = context.getString(R.string.app_pref)
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    private val listUrl = arrayListOf<String>(
        context.getString(R.string.search_car),
        context.getString(R.string.search_air),
        context.getString(R.string.search_key)
    )

    private var key1 = 0
    private var key2 = 0
    private var key3 = 0

    override fun doWork(): Result {

        key1 = sharedPreferences.getInt("key1", 0)
        key2 = sharedPreferences.getInt("key2", 0)
        key3 = sharedPreferences.getInt("key3", 0)

        try {

            val randomUrl = random.nextInt(3)
            val s = listUrl[randomUrl]

            when (s) {
                context.getString(R.string.search_car) -> {
                    key1++
                    sharedPreferences.edit().putInt("key1", key1).apply()
                }
                context.getString(R.string.search_air) -> {
                    key2++
                    sharedPreferences.edit().putInt("key2", key2++).apply()
                }
                context.getString(R.string.search_key) -> {
                    key3++
                    sharedPreferences.edit().putInt("key3", key3++).apply()
                }
            }

            findLinksByKey(s)

        } catch (ex: Exception) {
            Log.e("Worker", "WorkerFailure${ex.message}")
            return Result.failure()
        }

        return Result.success()
    }

    private fun findLinksByKey(url: String) {

        val docLinksByKey: Document = Jsoup.connect(url)
            .userAgent("Google")
            .cookie("auth", "token")
            .timeout(5000)
            .get()
        val elements: Elements = docLinksByKey.select("a[href]")

        for (element in elements) {

            if (element.attr("href").toString().startsWith("/url?q=")) {
                val s: String =
                    element.attr("href").substringAfter("/url?q=").substringBefore("&")
                links.add(s)
            }

        }

        findRandomLink()

    }

    private fun findRandomLink() {

        val randomLink = links[random.nextInt(links.size)]

        dataBase.insertLink(DataLink(time = getTimeDate(), link = randomLink))

        val docRandomLink: Document = Jsoup.connect(randomLink)
            .userAgent("Google")
            .cookie("auth", "token")
            .timeout(5000)
            .get()
        val elements2: Elements = docRandomLink.select("a[href]")
        Log.i("doc2", elements2.toString())
    }

    private fun getTimeDate(): String {

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date()).replace(" ", " \n")

        return currentDate
    }

}