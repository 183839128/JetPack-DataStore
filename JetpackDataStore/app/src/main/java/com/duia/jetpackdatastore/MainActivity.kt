package com.duia.jetpackdatastore

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val DATASTORE_PREFERENCE_NAME = "DataStorePreference"
    private val PREFERENCE_KEY_NAME = "preferenceKeyName"
    private val dataStore: DataStore<Preferences> = this.createDataStore(
        name = DATASTORE_PREFERENCE_NAME
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //没有权限，向用户请求权限
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE),
            0
        )


        findViewById<TextView>(R.id.read).setOnClickListener {
            lifecycleScope.launch {
                val value = readPreInfo()
                Log.e(
                    "DataStore",
                    "(onCreate:${Thread.currentThread().stackTrace[2].lineNumber}) value=$value"
                )
            }
        }
        findViewById<TextView>(R.id.write).setOnClickListener {
            lifecycleScope.launch {
                savePreInfo("写入的数据")
                Log.e(
                    "DataStore",
                    "(onCreate:${Thread.currentThread().stackTrace[2].lineNumber}) write data..."
                )
            }
        }
    }

    //写入数据
    private suspend fun savePreInfo(value: String) {
        dataStore.edit {
            it[preferencesKey<String>(PREFERENCE_KEY_NAME)] = value
        }
    }

    private suspend fun readPreInfo(): String {
        val value = dataStore.data.map {
            it[preferencesKey<String>(PREFERENCE_KEY_NAME)] ?: "没有值"
        }
        return value.first()
    }

}