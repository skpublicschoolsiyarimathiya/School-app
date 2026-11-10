package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "students",
  indices = [Index(value = ["rollNumber"], unique = true), Index(value = ["classId"])]
)
data class Student(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val rollNumber: String, // e.g. "STU-1001"
  val name: String,
  val classId: Long,
  val className: String, // Cached display e.g. "Grade 10-A"
  val gender: String, // "Male", "Female", "Other"
  val guardianName: String,
  val contactNumber: String,
  val email: String,
  val dob: String // "2009-03-15"
)
