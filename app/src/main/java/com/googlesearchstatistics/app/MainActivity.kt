package com.googlesearchstatistics.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.googlesearchstatistics.app.databinding.ActivityMainBinding
import com.googlesearchstatistics.app.db.DataBaseBuilder
import com.googlesearchstatistics.app.db.DataLink
import com.googlesearchstatistics.app.db.DataLinksDao
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var myAdapterRecycler: MyAdapterRecycler
    private lateinit var APP_PREFERENCES: String

    private lateinit var dataBase: DataLinksDao
    private lateinit var dataLink: List<DataLink>

    private lateinit var listDataLink: MutableList<DataLink>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        APP_PREFERENCES = getString(R.string.app_pref)
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        initRecyclerAdapter()

        setCountInKey()

        startExecutorsInitData()

        startWorker()

    }

    private fun initRecyclerAdapter() {
        myAdapterRecycler = MyAdapterRecycler()
        binding.list.adapter = myAdapterRecycler
        binding.list.layoutManager = LinearLayoutManager(this)
    }

    private fun setCountInKey() {
        val key1 = sharedPreferences.getInt("key1", 0)
        val key2 = sharedPreferences.getInt("key2", 0)
        val key3 = sharedPreferences.getInt("key3", 0)

        binding.textKey1.text = "Key Car: $key1"
        binding.textKey2.text = "Key Air: $key2"
        binding.textKey3.text = "Key Key: $key3"
    }

    private fun startExecutorsInitData() {

        Executors.newScheduledThreadPool(3).execute {

            dataBase = DataBaseBuilder.getInstans(applicationContext).dataLinksDao()
            dataLink = dataBase.selectAllLinks()
            listDataLink = mutableListOf<DataLink>()

            for (entry in dataLink) {
                listDataLink.add(
                    DataLink(
                        time = entry.time,
                        link = entry.link
                    )
                )
            }

            myAdapterRecycler.addListRecycler(listDataLink)

        }

    }

    private fun startWorker() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<MyWorker>(20, TimeUnit.MINUTES)
            .addTag("MyWorker")
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork("MyWorker", ExistingPeriodicWorkPolicy.KEEP, work)

        WorkManager.getInstance(this@MainActivity)
            .getWorkInfoByIdLiveData(work.id)
            .observe(this@MainActivity) { workInfo ->
                val status = workInfo.state
                Log.e("sum process state: ", status.toString())

                if (status == WorkInfo.State.ENQUEUED) {
                    startExecutorsInitData()
                    setCountInKey()
                }
            }
    }

}