package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
  @Query("SELECT * FROM attendance ORDER BY date DESC")
  fun getAllAttendance(): Flow<List<AttendanceRecord>>

  @Query("SELECT * FROM attendance WHERE date = :date AND classId = :classId")
  fun getAttendanceByDateAndClass(date: String, classId: Long): Flow<List<AttendanceRecord>>

  @Query("SELECT * FROM attendance WHERE date = :date")
  fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

  @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
  fun getAttendanceByStudent(studentId: Long): Flow<List<AttendanceRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAttendance(record: AttendanceRecord): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAttendanceBatch(records: List<AttendanceRecord>)

  @Update
  suspend fun updateAttendance(record: AttendanceRecord)

  @Delete
  suspend fun deleteAttendance(record: AttendanceRecord)

  @Query("DELETE FROM attendance WHERE id = :id")
  suspend fun deleteAttendanceById(id: Long)

  @Query("DELETE FROM attendance WHERE studentId = :studentId")
  suspend fun deleteAttendanceByStudent(studentId: Long)

  @Query("SELECT COUNT(*) FROM attendance")
  fun getAttendanceCount(): Flow<Int>
}
