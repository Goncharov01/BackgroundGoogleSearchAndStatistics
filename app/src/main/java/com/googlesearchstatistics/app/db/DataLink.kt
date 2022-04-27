package com.googlesearchstatistics.app.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["Links"], unique = true)]
)
data class DataLink(

    @ColumnInfo(name = "Links")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var time: String,

    var link: String

)