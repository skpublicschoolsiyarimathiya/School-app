package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "subjects",
  indices = [Index(value = ["code"], unique = true), Index(value = ["classId"])]
)
data class Subject(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val code: String, // e.g. "MTH-101"
  val title: String, // e.g. "Advanced Mathematics"
  val classId: Long, // 0 if common across classes
  val teacherName: String,
  val credits: Int = 3
)
