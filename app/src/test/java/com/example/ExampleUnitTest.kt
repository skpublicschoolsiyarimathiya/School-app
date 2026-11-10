package com.example

import com.example.data.local.SampleSchoolData
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.GradeRecord
import com.example.data.model.Student
import com.example.domain.ReportGenerator
import com.example.domain.SchoolAnalyticsCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `grade calculations and letter grades are accurate`() {
    val highGrade = GradeRecord(
      studentId = 1L,
      subjectId = 1L,
      examName = "Midterm",
      marksObtained = 95.0,
      maxMarks = 100.0
    )
    assertEquals(95.0, highGrade.percentage, 0.01)
    assertEquals("A+", highGrade.letterGrade)
    assertEquals(4.0, highGrade.gradePoint, 0.01)

    val lowGrade = GradeRecord(
      studentId = 2L,
      subjectId = 1L,
      examName = "Midterm",
      marksObtained = 55.0,
      maxMarks = 100.0
    )
    assertEquals(55.0, lowGrade.percentage, 0.01)
    assertEquals("D", lowGrade.letterGrade)
  }

  @Test
  fun `student attendance at risk detection`() {
    val student = Student(
      id = 10L,
      rollNumber = "STU-9999",
      name = "Test Student",
      classId = 1L,
      className = "Grade 10-A",
      gender = "Male",
      guardianName = "Guardian",
      contactNumber = "555-0101",
      email = "test@school.edu",
      dob = "2009-01-01"
    )

    // 4 days absent out of 5 -> 20% attendance -> At risk!
    val attendance = listOf(
      AttendanceRecord(studentId = 10L, classId = 1L, date = "2026-11-06", status = AttendanceStatus.PRESENT),
      AttendanceRecord(studentId = 10L, classId = 1L, date = "2026-11-07", status = AttendanceStatus.ABSENT),
      AttendanceRecord(studentId = 10L, classId = 1L, date = "2026-11-08", status = AttendanceStatus.ABSENT),
      AttendanceRecord(studentId = 10L, classId = 1L, date = "2026-11-09", status = AttendanceStatus.ABSENT),
      AttendanceRecord(studentId = 10L, classId = 1L, date = "2026-11-10", status = AttendanceStatus.ABSENT)
    )

    val analytics = SchoolAnalyticsCalculator.calculateStudentAnalytics(student, attendance, emptyList())
    assertEquals(20.0, analytics.attendanceRate, 0.1)
    assertTrue("Should flag student as attendance at-risk", analytics.isAttendanceAtRisk)
  }

  @Test
  fun `report generator outputs institutional sections`() {
    val sampleClasses = SampleSchoolData.getInitialClasses()
    val sampleStudents = SampleSchoolData.getInitialStudents()
    val sampleTeachers = SampleSchoolData.getInitialTeachers()
    val sampleSubjects = SampleSchoolData.getInitialSubjects()
    val sampleAttendance = SampleSchoolData.getInitialAttendance()
    val sampleGrades = SampleSchoolData.getInitialGrades()

    val metrics = SchoolAnalyticsCalculator.computeDashboardMetrics(
      sampleClasses, sampleStudents, sampleTeachers, sampleSubjects, sampleAttendance, sampleGrades
    )
    val studentAnalytics = sampleStudents.map {
      SchoolAnalyticsCalculator.calculateStudentAnalytics(it, sampleAttendance, sampleGrades)
    }

    val report = ReportGenerator.generateExecutiveSummary(metrics, sampleClasses, studentAnalytics)
    assertNotNull(report)
    assertTrue(report.contains("EDUMASTER ACADEMY - MASTER EXECUTIVE REPORT"))
    assertTrue(report.contains("INSTITUTION OVERVIEW"))
    assertTrue(report.contains("SCHOOL-WIDE PERFORMANCE INDICATORS"))
  }
}
