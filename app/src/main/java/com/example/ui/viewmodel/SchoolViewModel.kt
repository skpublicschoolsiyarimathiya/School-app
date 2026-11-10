package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.Teacher
import com.example.data.repository.SchoolRepository
import com.example.domain.ReportGenerator
import com.example.domain.SchoolAnalyticsCalculator
import com.example.domain.SchoolDashboardMetrics
import com.example.domain.StudentAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: SchoolRepository
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  init {
    val db = AppDatabase.getDatabase(application)
    repository = SchoolRepository(db)
    viewModelScope.launch {
      repository.checkAndSeedDatabase()
    }
  }

  // Raw data flows from repository
  val classes: StateFlow<List<SchoolClass>> = repository.allClasses
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val students: StateFlow<List<Student>> = repository.allStudents
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val teachers: StateFlow<List<Teacher>> = repository.allTeachers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val subjects: StateFlow<List<Subject>> = repository.allSubjects
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val attendance: StateFlow<List<AttendanceRecord>> = repository.allAttendance
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val grades: StateFlow<List<GradeRecord>> = repository.allGrades
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val examNames: StateFlow<List<String>> = repository.allExamNames
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dashboard Metrics derived reactively
  val dashboardMetrics: StateFlow<SchoolDashboardMetrics> = combine(
    combine(classes, students, teachers) { cls, stu, tch -> Triple(cls, stu, tch) },
    combine(subjects, attendance, grades) { sub, att, grd -> Triple(sub, att, grd) }
  ) { (cls, stu, tch), (sub, att, grd) ->
    SchoolAnalyticsCalculator.computeDashboardMetrics(cls, stu, tch, sub, att, grd)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SchoolDashboardMetrics())

  // Student Analytics Map (Student ID -> Analytics)
  val studentAnalyticsMap: StateFlow<Map<Long, StudentAnalytics>> = combine(
    students, attendance, grades
  ) { stuList, attList, grdList ->
    stuList.associate { student ->
      student.id to SchoolAnalyticsCalculator.calculateStudentAnalytics(student, attList, grdList)
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

  // Filter & Navigation UI states
  val selectedAttendanceDate = MutableStateFlow(LocalDate.now().format(dateFormatter))
  val selectedAttendanceClassId = MutableStateFlow<Long?>(null) // null = all

  val studentSearchQuery = MutableStateFlow("")
  val selectedStudentClassFilter = MutableStateFlow<Long?>(null) // null = all

  // Admin Permission Toggle: Administrator has full permissions to add, correct, and delete everything
  val isAdminMode = MutableStateFlow(true)

  // Status/Feedback Toast/Snackbar
  val userFeedbackMessage = MutableStateFlow<String?>(null)

  fun clearFeedbackMessage() {
    userFeedbackMessage.value = null
  }

  // --- ATTENDANCE ACTIONS ---
  fun setAttendanceDate(date: String) {
    selectedAttendanceDate.value = date
  }

  fun setAttendanceClass(classId: Long?) {
    selectedAttendanceClassId.value = classId
  }

  fun updateAttendance(studentId: Long, classId: Long, date: String, status: AttendanceStatus, remarks: String = "") {
    viewModelScope.launch {
      val existing = attendance.value.find { it.studentId == studentId && it.date == date }
      if (existing != null) {
        repository.updateAttendanceRecord(existing.copy(status = status, remarks = remarks))
      } else {
        repository.saveAttendanceRecord(
          AttendanceRecord(studentId = studentId, classId = classId, date = date, status = status, remarks = remarks)
        )
      }
    }
  }

  fun markAllAttendanceForClass(classId: Long, date: String, status: AttendanceStatus) {
    viewModelScope.launch {
      val classStudents = students.value.filter { it.classId == classId }
      val currentAttendance = attendance.value.filter { it.date == date && it.classId == classId }
      val newRecords = classStudents.map { stu ->
        val existing = currentAttendance.find { it.studentId == stu.id }
        AttendanceRecord(
          id = existing?.id ?: 0,
          studentId = stu.id,
          classId = stu.classId,
          date = date,
          status = status,
          remarks = if (status == AttendanceStatus.PRESENT) "Marked Present" else "Marked Absent"
        )
      }
      repository.saveAttendanceBatch(newRecords)
      userFeedbackMessage.value = "All ${classStudents.size} students marked as ${status.name.lowercase().replaceFirstChar { it.uppercase() }}"
    }
  }

  // --- MASTER ADMIN CRUD: STUDENTS ---
  fun saveStudent(student: Student) {
    viewModelScope.launch {
      if (student.id == 0L) {
        repository.insertStudent(student)
        userFeedbackMessage.value = "Student '${student.name}' added successfully"
      } else {
        repository.updateStudent(student)
        userFeedbackMessage.value = "Student '${student.name}' updated successfully"
      }
    }
  }

  fun deleteStudent(studentId: Long, studentName: String) {
    viewModelScope.launch {
      repository.deleteStudent(studentId)
      userFeedbackMessage.value = "Student '$studentName' and all associated records deleted"
    }
  }

  // --- MASTER ADMIN CRUD: CLASSES ---
  fun saveClass(schoolClass: SchoolClass) {
    viewModelScope.launch {
      if (schoolClass.id == 0L) {
        repository.insertClass(schoolClass)
        userFeedbackMessage.value = "Class '${schoolClass.displayName}' created"
      } else {
        repository.updateClass(schoolClass)
        userFeedbackMessage.value = "Class '${schoolClass.displayName}' updated"
      }
    }
  }

  fun deleteClass(classId: Long, className: String) {
    viewModelScope.launch {
      repository.deleteClass(classId)
      userFeedbackMessage.value = "Class '$className' deleted"
    }
  }

  // --- MASTER ADMIN CRUD: TEACHERS ---
  fun saveTeacher(teacher: Teacher) {
    viewModelScope.launch {
      if (teacher.id == 0L) {
        repository.insertTeacher(teacher)
        userFeedbackMessage.value = "Teacher '${teacher.name}' added"
      } else {
        repository.updateTeacher(teacher)
        userFeedbackMessage.value = "Teacher '${teacher.name}' updated"
      }
    }
  }

  fun deleteTeacher(teacherId: Long, teacherName: String) {
    viewModelScope.launch {
      repository.deleteTeacher(teacherId)
      userFeedbackMessage.value = "Teacher '$teacherName' removed"
    }
  }

  // --- MASTER ADMIN CRUD: SUBJECTS ---
  fun saveSubject(subject: Subject) {
    viewModelScope.launch {
      if (subject.id == 0L) {
        repository.insertSubject(subject)
        userFeedbackMessage.value = "Subject '${subject.title}' added"
      } else {
        repository.updateSubject(subject)
        userFeedbackMessage.value = "Subject '${subject.title}' updated"
      }
    }
  }

  fun deleteSubject(subjectId: Long, title: String) {
    viewModelScope.launch {
      repository.deleteSubject(subjectId)
      userFeedbackMessage.value = "Subject '$title' removed"
    }
  }

  // --- MASTER ADMIN CRUD: GRADES ---
  fun saveGrade(grade: GradeRecord) {
    viewModelScope.launch {
      if (grade.id == 0L) {
        repository.insertGrade(grade)
        userFeedbackMessage.value = "Grade record saved"
      } else {
        repository.updateGrade(grade)
        userFeedbackMessage.value = "Grade record updated"
      }
    }
  }

  fun deleteGrade(gradeId: Long) {
    viewModelScope.launch {
      repository.deleteGrade(gradeId)
      userFeedbackMessage.value = "Grade record removed"
    }
  }

  fun deleteAttendanceRecord(recordId: Long) {
    viewModelScope.launch {
      repository.deleteAttendanceRecord(recordId)
      userFeedbackMessage.value = "Attendance record deleted"
    }
  }

  // --- REPORT GENERATION ---
  fun getExecutiveReportText(): String {
    val metrics = dashboardMetrics.value
    val clsList = classes.value
    val analyticsList = studentAnalyticsMap.value.values.toList()
    return ReportGenerator.generateExecutiveSummary(metrics, clsList, analyticsList)
  }

  fun getStudentReportCardText(studentId: Long): String {
    val student = students.value.find { it.id == studentId } ?: return "Student not found"
    val analytics = studentAnalyticsMap.value[studentId] ?: SchoolAnalyticsCalculator.calculateStudentAnalytics(student, emptyList(), emptyList())
    val stuGrades = grades.value.filter { it.studentId == studentId }
    val subList = subjects.value
    val attList = attendance.value.filter { it.studentId == studentId }
    return ReportGenerator.generateStudentReportCard(student, analytics, stuGrades, subList, attList)
  }

  // --- DEMO SEED / RESET ---
  fun resetToDemoData() {
    viewModelScope.launch {
      repository.clearAllData()
      repository.seedDemoData()
      userFeedbackMessage.value = "School database restored with demo dataset"
    }
  }

  fun wipeAllData() {
    viewModelScope.launch {
      repository.clearAllData()
      userFeedbackMessage.value = "All school records wiped clean"
    }
  }
}
