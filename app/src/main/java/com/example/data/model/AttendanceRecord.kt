package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AttendanceStatus {
  PRESENT,
  ABSENT,
  LATE,
  EXCUSED
}

@Entity(
  tableName = "attendance",
  indices = [Index(value = ["studentId", "date"], unique = true), Index(value = ["classId", "date"])]
)
data class AttendanceRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val studentId: Long,
  val classId: Long,
  val date: String, // "YYYY-MM-DD" e.g. "2026-11-10"
  val status: AttendanceStatus,
  val remarks: String = ""
)
