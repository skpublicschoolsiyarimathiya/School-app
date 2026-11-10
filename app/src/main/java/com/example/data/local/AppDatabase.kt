package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.Teacher

@Database(
  entities = [
    SchoolClass::class,
    Student::class,
    Teacher::class,
    Subject::class,
    AttendanceRecord::class,
    GradeRecord::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun classDao(): ClassDao
  abstract fun studentDao(): StudentDao
  abstract fun teacherDao(): TeacherDao
  abstract fun subjectDao(): SubjectDao
  abstract fun attendanceDao(): AttendanceDao
  abstract fun gradeDao(): GradeDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "edumaster_school_database.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
