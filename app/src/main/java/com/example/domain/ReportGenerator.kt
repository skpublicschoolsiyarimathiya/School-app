package com.example.domain

import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object ReportGenerator {

  private val currentDateStr: String
    get() = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.US))

  fun generateExecutiveSummary(
    metrics: SchoolDashboardMetrics,
    classes: List<SchoolClass>,
    studentAnalytics: List<StudentAnalytics>
  ): String {
    val sb = StringBuilder()
    sb.appendLine("==================================================")
    sb.appendLine("        EDUMASTER ACADEMY - MASTER EXECUTIVE REPORT")
    sb.appendLine("        Generated on: $currentDateStr")
    sb.appendLine("        Authorized by: Master Administrator")
    sb.appendLine("==================================================")
    sb.appendLine()
    sb.appendLine("1. INSTITUTION OVERVIEW")
    sb.appendLine("   - Total Students Enrolled: ${metrics.totalStudents}")
    sb.appendLine("   - Total Faculty & Staff:    ${metrics.totalTeachers}")
    sb.appendLine("   - Active Class Sections:    ${metrics.totalClasses}")
    sb.appendLine("   - Curriculum Subjects:      ${metrics.totalSubjects}")
    sb.appendLine()
    sb.appendLine("2. SCHOOL-WIDE PERFORMANCE INDICATORS")
    sb.appendLine("   - Average Attendance Rate:  ${"%.1f".format(Locale.US, metrics.overallAttendanceRate)}%")
    sb.appendLine("   - School-Wide Average GPA:  ${"%.2f".format(Locale.US, metrics.overallSchoolGpa)} / 4.00")
    sb.appendLine("   - Students on At-Risk Alert:${metrics.atRiskStudentsCount}")
    sb.appendLine()
    sb.appendLine("3. CLASS-BY-CLASS BREAKDOWN")
    classes.forEach { cls ->
      val classStudents = studentAnalytics.filter { it.student.classId == cls.id }
      val classAttendance = if (classStudents.isNotEmpty()) classStudents.map { it.attendanceRate }.average() else 0.0
      val validClassGpa = classStudents.filter { it.totalExamsTaken > 0 }.map { it.cumulativeGpa }
      val classGpa = if (validClassGpa.isNotEmpty()) validClassGpa.average() else 0.0
      sb.appendLine("   * ${cls.displayName} (${cls.roomNumber}):")
      sb.appendLine("     - Class Teacher: ${cls.classTeacherName}")
      sb.appendLine("     - Students: ${classStudents.size} | Avg Attendance: ${"%.1f".format(Locale.US, classAttendance)}% | Avg GPA: ${"%.2f".format(Locale.US, classGpa)}")
    }
    sb.appendLine()
    sb.appendLine("4. TOP ACADEMIC PERFORMERS")
    if (metrics.topPerformers.isNotEmpty()) {
      metrics.topPerformers.forEachIndexed { i, perf ->
        sb.appendLine("   ${i + 1}. ${perf.student.name} (${perf.student.className}) - GPA: ${"%.2f".format(Locale.US, perf.cumulativeGpa)} [${perf.letterGrade}] (${perf.academicStanding})")
      }
    } else {
      sb.appendLine("   No graded assessment records recorded yet.")
    }
    sb.appendLine()
    sb.appendLine("5. EARLY INTERVENTION / AT-RISK WATCHLIST")
    if (metrics.atRiskStudents.isNotEmpty()) {
      metrics.atRiskStudents.forEach { st ->
        val reasons = mutableListOf<String>()
        if (st.isAttendanceAtRisk) reasons.add("Low Attendance: ${"%.1f".format(Locale.US, st.attendanceRate)}%")
        if (st.isAcademicAtRisk) reasons.add("Low GPA: ${"%.2f".format(Locale.US, st.cumulativeGpa)} [${st.letterGrade}]")
        sb.appendLine("   ! ${st.student.name} (${st.student.className}) - ${reasons.joinToString(", ")}")
        sb.appendLine("     Guardian Contact: ${st.student.guardianName} (${st.student.contactNumber})")
      }
    } else {
      sb.appendLine("   All students meet standard attendance (>=75%) and academic thresholds.")
    }
    sb.appendLine()
    sb.appendLine("==================================================")
    sb.appendLine("End of Official Master Report. Signed: EduMaster Admin")
    sb.appendLine("==================================================")
    return sb.toString()
  }

  fun generateStudentReportCard(
    student: Student,
    analytics: StudentAnalytics,
    grades: List<GradeRecord>,
    subjects: List<Subject>,
    attendanceRecords: List<AttendanceRecord>
  ): String {
    val sb = StringBuilder()
    sb.appendLine("==================================================")
    sb.appendLine("         EDUMASTER ACADEMY - STUDENT REPORT CARD")
    sb.appendLine("==================================================")
    sb.appendLine("Student Name:    ${student.name}")
    sb.appendLine("Roll Number:     ${student.rollNumber}")
    sb.appendLine("Class / Section: ${student.className}")
    sb.appendLine("Date of Birth:   ${student.dob}")
    sb.appendLine("Guardian:        ${student.guardianName} (${student.contactNumber})")
    sb.appendLine("Date of Issue:   $currentDateStr")
    sb.appendLine("--------------------------------------------------")
    sb.appendLine("ACADEMIC RECORD")
    sb.appendLine("%-22s %-10s %-8s %-6s".format("Subject", "Exam", "Marks", "Grade"))
    sb.appendLine("--------------------------------------------------")

    val studentGrades = grades.filter { it.studentId == student.id }
    if (studentGrades.isEmpty()) {
      sb.appendLine("No exam records found for this student.")
    } else {
      studentGrades.forEach { g ->
        val subject = subjects.find { it.id == g.subjectId }
        val subjectName = subject?.title ?: "Subject #${g.subjectId}"
        val shortSubject = if (subjectName.length > 20) subjectName.take(19) + "…" else subjectName
        sb.appendLine(
          "%-22s %-10s %-8s %-6s".format(
            shortSubject,
            g.examName.take(9),
            "${g.marksObtained.toInt()}/${g.maxMarks.toInt()}",
            g.letterGrade
          )
        )
      }
    }
    sb.appendLine("--------------------------------------------------")
    sb.appendLine("ACADEMIC SUMMARY")
    sb.appendLine("Cumulative GPA:     ${"%.2f".format(Locale.US, analytics.cumulativeGpa)} / 4.00")
    sb.appendLine("Overall Grade:      ${analytics.letterGrade}")
    sb.appendLine("Average Percentage: ${"%.1f".format(Locale.US, analytics.averagePercentage)}%")
    sb.appendLine("Academic Standing:  ${analytics.academicStanding}")
    sb.appendLine("--------------------------------------------------")
    sb.appendLine("ATTENDANCE SUMMARY")
    val totalDays = analytics.totalAttendanceDays
    sb.appendLine("Total School Days Logged: $totalDays")
    sb.appendLine("Days Present: ${analytics.daysPresent}  |  Late: ${analytics.daysLate}  |  Absent: ${analytics.daysAbsent}  |  Excused: ${analytics.daysExcused}")
    sb.appendLine("Attendance Rate: ${"%.1f".format(Locale.US, analytics.attendanceRate)}%")
    if (analytics.isAttendanceAtRisk) {
      sb.appendLine("STATUS: ATTENDANCE WARNING (Below mandatory 75% threshold)")
    } else {
      sb.appendLine("STATUS: ATTENDANCE SATISFACTORY")
    }
    sb.appendLine("==================================================")
    sb.appendLine("School Principal / Administrator Signature: ______________")
    sb.appendLine("==================================================")
    return sb.toString()
  }
}
