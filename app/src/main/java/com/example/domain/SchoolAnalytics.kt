package com.example.domain

import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.Teacher

data class StudentAnalytics(
  val student: Student,
  val totalAttendanceDays: Int,
  val daysPresent: Int,
  val daysAbsent: Int,
  val daysLate: Int,
  val daysExcused: Int,
  val attendanceRate: Double,
  val isAttendanceAtRisk: Boolean,
  val totalExamsTaken: Int,
  val averagePercentage: Double,
  val cumulativeGpa: Double,
  val letterGrade: String,
  val isAcademicAtRisk: Boolean,
  val academicStanding: String
)

data class SchoolDashboardMetrics(
  val totalStudents: Int = 0,
  val totalTeachers: Int = 0,
  val totalClasses: Int = 0,
  val totalSubjects: Int = 0,
  val overallAttendanceRate: Double = 0.0,
  val overallSchoolGpa: Double = 0.0,
  val atRiskStudentsCount: Int = 0,
  val topPerformers: List<StudentAnalytics> = emptyList(),
  val atRiskStudents: List<StudentAnalytics> = emptyList()
)

object SchoolAnalyticsCalculator {

  fun calculateStudentAnalytics(
    student: Student,
    attendanceList: List<AttendanceRecord>,
    gradesList: List<GradeRecord>
  ): StudentAnalytics {
    val studentAttendance = attendanceList.filter { it.studentId == student.id }
    val totalDays = studentAttendance.size
    val present = studentAttendance.count { it.status == AttendanceStatus.PRESENT }
    val absent = studentAttendance.count { it.status == AttendanceStatus.ABSENT }
    val late = studentAttendance.count { it.status == AttendanceStatus.LATE }
    val excused = studentAttendance.count { it.status == AttendanceStatus.EXCUSED }

    // Attendance calculation: Late counts as 0.75 present, Excused doesn't penalize
    val effectiveDays = totalDays - excused
    val attendanceRate = if (effectiveDays > 0) {
      ((present.toDouble() + (late * 0.75)) / effectiveDays.toDouble()) * 100.0
    } else if (totalDays > 0) {
      100.0
    } else {
      100.0
    }

    val isAttendanceAtRisk = totalDays > 0 && attendanceRate < 75.0

    val studentGrades = gradesList.filter { it.studentId == student.id }
    val totalExams = studentGrades.size
    val avgPercentage = if (totalExams > 0) {
      studentGrades.map { it.percentage }.average()
    } else {
      0.0
    }

    val gpa = if (totalExams > 0) {
      studentGrades.map { it.gradePoint }.average()
    } else {
      0.0
    }

    val letterGrade = when {
      avgPercentage >= 90.0 -> "A+"
      avgPercentage >= 80.0 -> "A"
      avgPercentage >= 70.0 -> "B"
      avgPercentage >= 60.0 -> "C"
      avgPercentage >= 50.0 -> "D"
      totalExams == 0 -> "N/A"
      else -> "F"
    }

    val isAcademicAtRisk = totalExams > 0 && (gpa < 2.0 || avgPercentage < 60.0)

    val standing = when {
      gpa >= 3.8 -> "Highest Honors"
      gpa >= 3.5 -> "Dean's Honors"
      gpa >= 2.5 -> "Good Standing"
      totalExams == 0 -> "Enrolled"
      else -> "Academic Warning"
    }

    return StudentAnalytics(
      student = student,
      totalAttendanceDays = totalDays,
      daysPresent = present,
      daysAbsent = absent,
      daysLate = late,
      daysExcused = excused,
      attendanceRate = attendanceRate,
      isAttendanceAtRisk = isAttendanceAtRisk,
      totalExamsTaken = totalExams,
      averagePercentage = avgPercentage,
      cumulativeGpa = gpa,
      letterGrade = letterGrade,
      isAcademicAtRisk = isAcademicAtRisk,
      academicStanding = standing
    )
  }

  fun computeDashboardMetrics(
    classes: List<SchoolClass>,
    students: List<Student>,
    teachers: List<Teacher>,
    subjects: List<Subject>,
    attendance: List<AttendanceRecord>,
    grades: List<GradeRecord>
  ): SchoolDashboardMetrics {
    if (students.isEmpty()) {
      return SchoolDashboardMetrics(
        totalStudents = 0,
        totalTeachers = teachers.size,
        totalClasses = classes.size,
        totalSubjects = subjects.size
      )
    }

    val analyticsList = students.map { calculateStudentAnalytics(it, attendance, grades) }

    val overallAttendance = if (analyticsList.isNotEmpty()) {
      analyticsList.map { it.attendanceRate }.average()
    } else 0.0

    val validGpas = analyticsList.filter { it.totalExamsTaken > 0 }.map { it.cumulativeGpa }
    val overallGpa = if (validGpas.isNotEmpty()) validGpas.average() else 0.0

    val atRiskList = analyticsList.filter { it.isAttendanceAtRisk || it.isAcademicAtRisk }
    val topPerformers = analyticsList
      .filter { it.totalExamsTaken > 0 }
      .sortedByDescending { it.cumulativeGpa }
      .take(5)

    return SchoolDashboardMetrics(
      totalStudents = students.size,
      totalTeachers = teachers.size,
      totalClasses = classes.size,
      totalSubjects = subjects.size,
      overallAttendanceRate = overallAttendance,
      overallSchoolGpa = overallGpa,
      atRiskStudentsCount = atRiskList.size,
      topPerformers = topPerformers,
      atRiskStudents = atRiskList
    )
  }
}
