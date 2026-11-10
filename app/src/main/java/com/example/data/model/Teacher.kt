package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "teachers",
  indices = [Index(value = ["employeeId"], unique = true)]
)
data class Teacher(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val employeeId: String, // e.g. "TCH-201"
  val name: String,
  val email: String,
  val phone: String,
  val department: String, // e.g. "Mathematics & Computing"
  val assignedSubjects: String // e.g. "Calculus, Algebra"
)
