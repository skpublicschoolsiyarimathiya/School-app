package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Teacher
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {
  @Query("SELECT * FROM teachers ORDER BY name ASC")
  fun getAllTeachers(): Flow<List<Teacher>>

  @Query("SELECT * FROM teachers WHERE id = :id LIMIT 1")
  suspend fun getTeacherById(id: Long): Teacher?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTeacher(teacher: Teacher): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTeachers(teachers: List<Teacher>)

  @Update
  suspend fun updateTeacher(teacher: Teacher)

  @Delete
  suspend fun deleteTeacher(teacher: Teacher)

  @Query("DELETE FROM teachers WHERE id = :id")
  suspend fun deleteTeacherById(id: Long)

  @Query("SELECT COUNT(*) FROM teachers")
  fun getTeacherCount(): Flow<Int>
}
