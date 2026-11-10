package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GradeRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
  @Query("SELECT * FROM grades ORDER BY dateRecorded DESC, examName ASC")
  fun getAllGrades(): Flow<List<GradeRecord>>

  @Query("SELECT * FROM grades WHERE studentId = :studentId ORDER BY examName ASC")
  fun getGradesByStudent(studentId: Long): Flow<List<GradeRecord>>

  @Query("SELECT * FROM grades WHERE subjectId = :subjectId")
  fun getGradesBySubject(subjectId: Long): Flow<List<GradeRecord>>

  @Query("SELECT * FROM grades WHERE examName = :examName")
  fun getGradesByExam(examName: String): Flow<List<GradeRecord>>

  @Query("SELECT DISTINCT examName FROM grades ORDER BY examName ASC")
  fun getAllExamNames(): Flow<List<String>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGrade(grade: GradeRecord): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGrades(grades: List<GradeRecord>)

  @Update
  suspend fun updateGrade(grade: GradeRecord)

  @Delete
  suspend fun deleteGrade(grade: GradeRecord)

  @Query("DELETE FROM grades WHERE id = :id")
  suspend fun deleteGradeById(id: Long)

  @Query("DELETE FROM grades WHERE studentId = :studentId")
  suspend fun deleteGradesByStudent(studentId: Long)

  @Query("SELECT COUNT(*) FROM grades")
  fun getGradeCount(): Flow<Int>
}
