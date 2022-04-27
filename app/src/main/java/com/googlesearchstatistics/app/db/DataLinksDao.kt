package com.googlesearchstatistics.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataLinksDao {

    @Insert
    fun insertLink(listLinks: DataLink): Long

    @Query("select * from DataLink")
    fun selectAllLinks(): List<DataLink>

}