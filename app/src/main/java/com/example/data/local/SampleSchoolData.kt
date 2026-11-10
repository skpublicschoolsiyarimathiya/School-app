package com.example.data.local

import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.Teacher
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object SampleSchoolData {

  fun getInitialClasses(): List<SchoolClass> = listOf(
    SchoolClass(id = 1, name = "Grade 10", section = "A", roomNumber = "Room 301", classTeacherName = "Prof. Marcus Vance"),
    SchoolClass(id = 2, name = "Grade 10", section = "B", roomNumber = "Room 302", classTeacherName = "Ms. Elena Rostova"),
    SchoolClass(id = 3, name = "Grade 9", section = "A", roomNumber = "Room 205", classTeacherName = "Dr. Sarah Jenkins")
  )

  fun getInitialTeachers(): List<Teacher> = listOf(
    Teacher(id = 1, employeeId = "TCH-101", name = "Prof. Marcus Vance", email = "m.vance@edumaster.edu", phone = "+1 (555) 231-4401", department = "Mathematics", assignedSubjects = "Advanced Mathematics"),
    Teacher(id = 2, employeeId = "TCH-102", name = "Dr. Sarah Jenkins", email = "s.jenkins@edumaster.edu", phone = "+1 (555) 231-4402", department = "Sciences", assignedSubjects = "Physics Fundamentals, Chemistry & Lab"),
    Teacher(id = 3, employeeId = "TCH-103", name = "Ms. Elena Rostova", email = "e.rostova@edumaster.edu", phone = "+1 (555) 231-4403", department = "Humanities", assignedSubjects = "English Literature & Comp"),
    Teacher(id = 4, employeeId = "TCH-104", name = "Mr. David Kim", email = "d.kim@edumaster.edu", phone = "+1 (555) 231-4404", department = "Social Sciences", assignedSubjects = "Modern World History")
  )

  fun getInitialSubjects(): List<Subject> = listOf(
    Subject(id = 1, code = "MTH-101", title = "Advanced Mathematics", classId = 1, teacherName = "Prof. Marcus Vance", credits = 4),
    Subject(id = 2, code = "PHY-101", title = "Physics Fundamentals", classId = 1, teacherName = "Dr. Sarah Jenkins", credits = 4),
    Subject(id = 3, code = "ENG-101", title = "English Literature", classId = 1, teacherName = "Ms. Elena Rostova", credits = 3),
    Subject(id = 4, code = "CHM-101", title = "Chemistry & Lab", classId = 1, teacherName = "Dr. Sarah Jenkins", credits = 4),
    Subject(id = 5, code = "HIS-101", title = "Modern World History", classId = 1, teacherName = "Mr. David Kim", credits = 3)
  )

  fun getInitialStudents(): List<Student> = listOf(
    // Grade 10-A
    Student(id = 1, rollNumber = "STU-1001", name = "Alexander Clark", classId = 1, className = "Grade 10 - A", gender = "Male", guardianName = "Robert Clark", contactNumber = "+1 (555) 891-2301", email = "alex.clark@student.edu", dob = "2009-04-12"),
    Student(id = 2, rollNumber = "STU-1002", name = "Sophia Martinez", classId = 1, className = "Grade 10 - A", gender = "Female", guardianName = "Maria Martinez", contactNumber = "+1 (555) 891-2302", email = "sophia.m@student.edu", dob = "2009-08-23"),
    Student(id = 3, rollNumber = "STU-1003", name = "Ethan Zhang", classId = 1, className = "Grade 10 - A", gender = "Male", guardianName = "Wei Zhang", contactNumber = "+1 (555) 891-2303", email = "ethan.z@student.edu", dob = "2009-01-30"),
    Student(id = 4, rollNumber = "STU-1004", name = "Olivia Bennett", classId = 1, className = "Grade 10 - A", gender = "Female", guardianName = "Claire Bennett", contactNumber = "+1 (555) 891-2304", email = "olivia.b@student.edu", dob = "2009-11-14"),
    Student(id = 5, rollNumber = "STU-1005", name = "Lucas Rivera", classId = 1, className = "Grade 10 - A", gender = "Male", guardianName = "Carlos Rivera", contactNumber = "+1 (555) 891-2305", email = "lucas.r@student.edu", dob = "2009-06-05"),

    // Grade 10-B
    Student(id = 6, rollNumber = "STU-1006", name = "Emma Watson", classId = 2, className = "Grade 10 - B", gender = "Female", guardianName = "Hannah Watson", contactNumber = "+1 (555) 891-2306", email = "emma.w@student.edu", dob = "2009-03-21"),
    Student(id = 7, rollNumber = "STU-1007", name = "Noah Patel", classId = 2, className = "Grade 10 - B", gender = "Male", guardianName = "Dev Patel", contactNumber = "+1 (555) 891-2307", email = "noah.p@student.edu", dob = "2009-09-08"),
    Student(id = 8, rollNumber = "STU-1008", name = "Isabella Rossi", classId = 2, className = "Grade 10 - B", gender = "Female", guardianName = "Gianna Rossi", contactNumber = "+1 (555) 891-2308", email = "isabella.r@student.edu", dob = "2009-07-17"),
    Student(id = 9, rollNumber = "STU-1009", name = "Liam O'Connor", classId = 2, className = "Grade 10 - B", gender = "Male", guardianName = "Sean O'Connor", contactNumber = "+1 (555) 891-2309", email = "liam.o@student.edu", dob = "2009-02-11"),

    // Grade 9-A
    Student(id = 10, rollNumber = "STU-1010", name = "Mia Tanaka", classId = 3, className = "Grade 9 - A", gender = "Female", guardianName = "Kenji Tanaka", contactNumber = "+1 (555) 891-2310", email = "mia.t@student.edu", dob = "2010-05-19"),
    Student(id = 11, rollNumber = "STU-1011", name = "Benjamin Brooks", classId = 3, className = "Grade 9 - A", gender = "Male", guardianName = "Laura Brooks", contactNumber = "+1 (555) 891-2311", email = "ben.b@student.edu", dob = "2010-10-02"),
    Student(id = 12, rollNumber = "STU-1012", name = "Charlotte Davis", classId = 3, className = "Grade 9 - A", gender = "Female", guardianName = "Paul Davis", contactNumber = "+1 (555) 891-2312", email = "charlotte.d@student.edu", dob = "2010-12-28")
  )

  fun getInitialAttendance(): List<AttendanceRecord> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now()
    val records = mutableListOf<AttendanceRecord>()

    // Generate for last 5 school days
    for (dayOffset in 0..4) {
      val dateStr = today.minusDays(dayOffset.toLong()).format(formatter)
      getInitialStudents().forEach { student ->
        val status = when {
          student.id == 5L && (dayOffset == 0 || dayOffset == 1 || dayOffset == 3) -> AttendanceStatus.ABSENT // Lucas Rivera at risk
          student.id == 3L && dayOffset == 2 -> AttendanceStatus.LATE
          student.id == 8L && dayOffset == 4 -> AttendanceStatus.EXCUSED
          student.id == 9L && dayOffset == 1 -> AttendanceStatus.ABSENT
          else -> AttendanceStatus.PRESENT
        }
        records.add(
          AttendanceRecord(
            studentId = student.id,
            classId = student.classId,
            date = dateStr,
            status = status,
            remarks = if (status == AttendanceStatus.ABSENT) "Unexcused absence" else if (status == AttendanceStatus.EXCUSED) "Medical appointment" else ""
          )
        )
      }
    }
    return records
  }

  fun getInitialGrades(): List<GradeRecord> {
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val records = mutableListOf<GradeRecord>()

    // Midterm Exam for Grade 10-A students
    val midtermScores = mapOf(
      1L to listOf(88.0, 92.0, 85.0, 90.0, 86.0), // Alexander
      2L to listOf(98.0, 95.0, 96.0, 99.0, 94.0), // Sophia (Topper)
      3L to listOf(78.0, 82.0, 75.0, 80.0, 79.0), // Ethan
      4L to listOf(85.0, 88.0, 91.0, 84.0, 89.0), // Olivia
      5L to listOf(52.0, 48.0, 58.0, 50.0, 55.0)  // Lucas (At risk)
    )

    midtermScores.forEach { (studentId, scores) ->
      scores.forEachIndexed { index, score ->
        val subjectId = (index + 1).toLong()
        records.add(
          GradeRecord(
            studentId = studentId,
            subjectId = subjectId,
            examName = "Midterm Examination",
            marksObtained = score,
            maxMarks = 100.0,
            remarks = if (score >= 90.0) "Outstanding work" else if (score < 60.0) "Needs improvement and tutoring" else "Satisfactory progress",
            dateRecorded = today
          )
        )
      }
    }

    // Unit Test 1 for Grade 10-A students
    val unitTestScores = mapOf(
      1L to listOf(90.0, 94.0, 88.0),
      2L to listOf(100.0, 98.0, 95.0),
      3L to listOf(80.0, 85.0, 78.0),
      4L to listOf(89.0, 92.0, 90.0),
      5L to listOf(45.0, 50.0, 54.0)
    )

    unitTestScores.forEach { (studentId, scores) ->
      scores.forEachIndexed { index, score ->
        val subjectId = (index + 1).toLong()
        records.add(
          GradeRecord(
            studentId = studentId,
            subjectId = subjectId,
            examName = "Unit Test 1",
            marksObtained = score,
            maxMarks = 100.0,
            remarks = "Continuous assessment",
            dateRecorded = today
          )
        )
      }
    }

    return records
  }
}
