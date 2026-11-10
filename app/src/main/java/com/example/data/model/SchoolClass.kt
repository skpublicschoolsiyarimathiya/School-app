package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class SchoolClass(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String, // e.g. "Grade 10"
  val section: String, // e.g. "A"
  val roomNumber: String, // e.g. "Room 302"
  val classTeacherName: String // e.g. "Dr. Sarah Jenkins"
) {
  val displayName: String
    get() = if (section.isNotBlank()) "$name - $section" else name
}
