package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
  @Query("SELECT * FROM subjects ORDER BY title ASC")
  fun getAllSubjects(): Flow<List<Subject>>

  @Query("SELECT * FROM subjects WHERE classId = :classId OR classId = 0 ORDER BY title ASC")
  fun getSubjectsByClass(classId: Long): Flow<List<Subject>>

  @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
  suspend fun getSubjectById(id: Long): Subject?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSubject(subject: Subject): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSubjects(subjects: List<Subject>)

  @Update
  suspend fun updateSubject(subject: Subject)

  @Delete
  suspend fun deleteSubject(subject: Subject)

  @Query("DELETE FROM subjects WHERE id = :id")
  suspend fun deleteSubjectById(id: Long)

  @Query("SELECT COUNT(*) FROM subjects")
  fun getSubjectCount(): Flow<Int>
}
