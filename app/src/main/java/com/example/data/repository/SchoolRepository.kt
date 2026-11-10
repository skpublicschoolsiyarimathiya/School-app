package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.SampleSchoolData
import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.Teacher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SchoolRepository(private val database: AppDatabase) {
  private val classDao = database.classDao()
  private val studentDao = database.studentDao()
  private val teacherDao = database.teacherDao()
  private val subjectDao = database.subjectDao()
  private val attendanceDao = database.attendanceDao()
  private val gradeDao = database.gradeDao()

  // Observables
  val allClasses: Flow<List<SchoolClass>> = classDao.getAllClasses()
  val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
  val allTeachers: Flow<List<Teacher>> = teacherDao.getAllTeachers()
  val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()
  val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()
  val allGrades: Flow<List<GradeRecord>> = gradeDao.getAllGrades()
  val allExamNames: Flow<List<String>> = gradeDao.getAllExamNames()

  suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
    val existingCount = studentDao.getStudentCount().first()
    if (existingCount == 0) {
      seedDemoData()
    }
  }

  suspend fun seedDemoData() = withContext(Dispatchers.IO) {
    classDao.insertClasses(SampleSchoolData.getInitialClasses())
    teacherDao.insertTeachers(SampleSchoolData.getInitialTeachers())
    subjectDao.insertSubjects(SampleSchoolData.getInitialSubjects())
    studentDao.insertStudents(SampleSchoolData.getInitialStudents())
    attendanceDao.insertAttendanceBatch(SampleSchoolData.getInitialAttendance())
    gradeDao.insertGrades(SampleSchoolData.getInitialGrades())
  }

  suspend fun clearAllData() = withContext(Dispatchers.IO) {
    database.clearAllTables()
  }

  // Class CRUD
  suspend fun insertClass(schoolClass: SchoolClass) = classDao.insertClass(schoolClass)
  suspend fun updateClass(schoolClass: SchoolClass) = classDao.updateClass(schoolClass)
  suspend fun deleteClass(id: Long) = classDao.deleteClassById(id)

  // Student CRUD
  fun getStudentsByClass(classId: Long): Flow<List<Student>> = studentDao.getStudentsByClass(classId)
  suspend fun insertStudent(student: Student) = studentDao.insertStudent(student)
  suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)
  suspend fun deleteStudent(id: Long) = withContext(Dispatchers.IO) {
    attendanceDao.deleteAttendanceByStudent(id)
    gradeDao.deleteGradesByStudent(id)
    studentDao.deleteStudentById(id)
  }

  // Teacher CRUD
  suspend fun insertTeacher(teacher: Teacher) = teacherDao.insertTeacher(teacher)
  suspend fun updateTeacher(teacher: Teacher) = teacherDao.updateTeacher(teacher)
  suspend fun deleteTeacher(id: Long) = teacherDao.deleteTeacherById(id)

  // Subject CRUD
  fun getSubjectsByClass(classId: Long): Flow<List<Subject>> = subjectDao.getSubjectsByClass(classId)
  suspend fun insertSubject(subject: Subject) = subjectDao.insertSubject(subject)
  suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)
  suspend fun deleteSubject(id: Long) = subjectDao.deleteSubjectById(id)

  // Attendance CRUD
  fun getAttendanceByDateAndClass(date: String, classId: Long): Flow<List<AttendanceRecord>> =
    attendanceDao.getAttendanceByDateAndClass(date, classId)

  fun getAttendanceByStudent(studentId: Long): Flow<List<AttendanceRecord>> =
    attendanceDao.getAttendanceByStudent(studentId)

  suspend fun saveAttendanceRecord(record: AttendanceRecord) = attendanceDao.insertAttendance(record)
  suspend fun saveAttendanceBatch(records: List<AttendanceRecord>) = attendanceDao.insertAttendanceBatch(records)
  suspend fun updateAttendanceRecord(record: AttendanceRecord) = attendanceDao.updateAttendance(record)
  suspend fun deleteAttendanceRecord(id: Long) = attendanceDao.deleteAttendanceById(id)

  // Grade CRUD
  fun getGradesByStudent(studentId: Long): Flow<List<GradeRecord>> = gradeDao.getGradesByStudent(studentId)
  suspend fun insertGrade(grade: GradeRecord) = gradeDao.insertGrade(grade)
  suspend fun saveGradesBatch(grades: List<GradeRecord>) = gradeDao.insertGrades(grades)
  suspend fun updateGrade(grade: GradeRecord) = gradeDao.updateGrade(grade)
  suspend fun deleteGrade(id: Long) = gradeDao.deleteGradeById(id)
}
