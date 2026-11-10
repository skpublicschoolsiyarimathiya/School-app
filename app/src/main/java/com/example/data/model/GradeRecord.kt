package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "grades",
  indices = [Index(value = ["studentId", "subjectId", "examName"])]
)
data class GradeRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val studentId: Long,
  val subjectId: Long,
  val examName: String, // e.g. "Midterm Exam", "Final Exam", "Unit Test 1"
  val marksObtained: Double,
  val maxMarks: Double = 100.0,
  val remarks: String = "",
  val dateRecorded: String = "" // "YYYY-MM-DD"
) {
  val percentage: Double
    get() = if (maxMarks > 0) (marksObtained / maxMarks) * 100.0 else 0.0

  val letterGrade: String
    get() = when {
      percentage >= 90.0 -> "A+"
      percentage >= 80.0 -> "A"
      percentage >= 70.0 -> "B"
      percentage >= 60.0 -> "C"
      percentage >= 50.0 -> "D"
      else -> "F"
    }

  val gradePoint: Double
    get() = when {
      percentage >= 90.0 -> 4.0
      percentage >= 85.0 -> 3.7
      percentage >= 80.0 -> 3.3
      percentage >= 75.0 -> 3.0
      percentage >= 70.0 -> 2.7
      percentage >= 65.0 -> 2.3
      percentage >= 60.0 -> 2.0
      percentage >= 50.0 -> 1.0
      else -> 0.0
    }
}
