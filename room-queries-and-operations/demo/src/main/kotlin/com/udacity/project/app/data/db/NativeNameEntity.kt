package com.udacity.project.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "native_names",
    foreignKeys = [ForeignKey(
        entity = CountryEntity::class,
        parentColumns = ["common_name"],
        childColumns = ["country_name"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("country_name")]
)
data class NativeNameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "country_name")
    val countryName: String,
    val lang: String,
    val official: String,
    val common: String
)
