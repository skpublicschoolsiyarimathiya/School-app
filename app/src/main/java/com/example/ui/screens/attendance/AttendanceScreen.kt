package com.example.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.ui.theme.RoyalBlue40
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusAbsentContainer
import com.example.ui.theme.StatusExcused
import com.example.ui.theme.StatusExcusedContainer
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusLateContainer
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.StatusPresentContainer
import com.example.ui.viewmodel.SchoolViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AttendanceScreen(
  viewModel: SchoolViewModel,
  onNavigateToStudentDetail: (Long) -> Unit
) {
  val classes by viewModel.classes.collectAsStateWithLifecycle()
  val students by viewModel.students.collectAsStateWithLifecycle()
  val allAttendance by viewModel.attendance.collectAsStateWithLifecycle()
  val studentAnalyticsMap by viewModel.studentAnalyticsMap.collectAsStateWithLifecycle()

  val selectedDateStr by viewModel.selectedAttendanceDate.collectAsStateWithLifecycle()
  val selectedClassId by viewModel.selectedAttendanceClassId.collectAsStateWithLifecycle()

  val currentDate = remember(selectedDateStr) {
    try {
      LocalDate.parse(selectedDateStr, viewModel.dateFormatter)
    } catch (_: Exception) {
      LocalDate.now()
    }
  }

  // Filter students based on class selection
  val filteredStudents = remember(students, selectedClassId) {
    if (selectedClassId == null) students else students.filter { it.classId == selectedClassId }
  }

  // Attendance for the chosen date
  val dateAttendance = remember(allAttendance, selectedDateStr) {
    allAttendance.filter { it.date == selectedDateStr }
  }

  // Statistics for currently selected view
  val presentCount = filteredStudents.count { stu ->
    dateAttendance.find { it.studentId == stu.id }?.status == AttendanceStatus.PRESENT
  }
  val absentCount = filteredStudents.count { stu ->
    dateAttendance.find { it.studentId == stu.id }?.status == AttendanceStatus.ABSENT
  }
  val lateCount = filteredStudents.count { stu ->
    dateAttendance.find { it.studentId == stu.id }?.status == AttendanceStatus.LATE
  }
  val excusedCount = filteredStudents.count { stu ->
    dateAttendance.find { it.studentId == stu.id }?.status == AttendanceStatus.EXCUSED
  }
  val totalFiltered = filteredStudents.size
  val attendanceRateToday = if (totalFiltered > 0) {
    ((presentCount + (lateCount * 0.75)) / totalFiltered.toDouble()) * 100.0
  } else 0.0

  // Remark dialog state
  var remarkTargetStudent by remember { mutableStateOf<Student?>(null) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("attendance_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Date Selector Navigator
    item {
      ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("attendance_date_navigator")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                val prev = currentDate.minusDays(1).format(viewModel.dateFormatter)
                viewModel.setAttendanceDate(prev)
              },
              modifier = Modifier.size(36.dp).testTag("attendance_prev_date_button")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.DateRange,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = currentDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US)),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
              Text(
                text = if (currentDate == LocalDate.now()) "Today" else "Historical Date",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
              )
            }

            IconButton(
              onClick = {
                val next = currentDate.plusDays(1).format(viewModel.dateFormatter)
                viewModel.setAttendanceDate(next)
              },
              modifier = Modifier.size(36.dp).testTag("attendance_next_date_button")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
            }
          }
        }
      }
    }

    // 2. Class Selector Chips
    item {
      Column {
        Text(
          text = "Select Class Section",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            FilterChip(
              selected = selectedClassId == null,
              onClick = { viewModel.setAttendanceClass(null) },
              label = { Text("All Classes (${students.size})") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RoyalBlue40,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.testTag("filter_all_classes")
            )
          }
          items(classes) { cls ->
            val count = students.count { it.classId == cls.id }
            FilterChip(
              selected = selectedClassId == cls.id,
              onClick = { viewModel.setAttendanceClass(cls.id) },
              label = { Text("${cls.displayName} ($count)") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RoyalBlue40,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }
    }

    // 3. Batch Actions & Summary Bar
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Daily Attendance Summary",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "$presentCount Present • $absentCount Absent • $lateCount Late • $excusedCount Excused",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
            Surface(
              color = if (attendanceRateToday >= 75.0) StatusPresent.copy(alpha = 0.15f) else StatusAbsent.copy(alpha = 0.15f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "${"%.1f".format(Locale.US, attendanceRateToday)}%",
                fontWeight = FontWeight.Bold,
                color = if (attendanceRateToday >= 75.0) StatusPresent else StatusAbsent,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          LinearProgressIndicator(
            progress = { (attendanceRateToday / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = if (attendanceRateToday >= 75.0) StatusPresent else StatusAbsent,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Batch Action Buttons
          if (selectedClassId != null) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  viewModel.markAllAttendanceForClass(selectedClassId!!, selectedDateStr, AttendanceStatus.PRESENT)
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusPresent),
                modifier = Modifier.weight(1f).testTag("mark_all_present_button")
              ) {
                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("All Present", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = {
                  viewModel.markAllAttendanceForClass(selectedClassId!!, selectedDateStr, AttendanceStatus.ABSENT)
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusAbsent),
                modifier = Modifier.weight(1f).testTag("mark_all_absent_button")
              ) {
                Icon(Icons.Default.RemoveDone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("All Absent", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 4. Students Roster for Attendance
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Student Roll Call (${filteredStudents.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Tap P / A / L / E to mark",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }

    items(filteredStudents) { student ->
      val currentRecord = dateAttendance.find { it.studentId == student.id }
      val currentStatus = currentRecord?.status ?: AttendanceStatus.PRESENT
      val analytics = studentAnalyticsMap[student.id]

      StudentAttendanceCard(
        student = student,
        status = currentStatus,
        remarks = currentRecord?.remarks ?: "",
        overallRate = analytics?.attendanceRate ?: 100.0,
        isAtRisk = analytics?.isAttendanceAtRisk ?: false,
        onStatusChange = { newStatus ->
          viewModel.updateAttendance(
            studentId = student.id,
            classId = student.classId,
            date = selectedDateStr,
            status = newStatus,
            remarks = currentRecord?.remarks ?: ""
          )
        },
        onEditRemarks = { remarkTargetStudent = student },
        onClick = { onNavigateToStudentDetail(student.id) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Edit Remarks Dialog
  if (remarkTargetStudent != null) {
    val target = remarkTargetStudent!!
    val record = dateAttendance.find { it.studentId == target.id }
    var remarkText by remember { mutableStateOf(record?.remarks ?: "") }

    AlertDialog(
      onDismissRequest = { remarkTargetStudent = null },
      title = { Text("Attendance Note for ${target.name}") },
      text = {
        Column {
          Text(text = "Date: $selectedDateStr", style = MaterialTheme.typography.bodySmall)
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = remarkText,
            onValueChange = { remarkText = it },
            label = { Text("Remarks (e.g. Doctor's note, bus delay)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateAttendance(
              studentId = target.id,
              classId = target.classId,
              date = selectedDateStr,
              status = record?.status ?: AttendanceStatus.PRESENT,
              remarks = remarkText.trim()
            )
            remarkTargetStudent = null
          }
        ) {
          Text("Save Note")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { remarkTargetStudent = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun StudentAttendanceCard(
  student: Student,
  status: AttendanceStatus,
  remarks: String,
  overallRate: Double,
  isAtRisk: Boolean,
  onStatusChange: (AttendanceStatus) -> Unit,
  onEditRemarks: () -> Unit,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f).clickable { onClick() }
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = student.name.take(2).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = student.name,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${student.className} • ${student.rollNumber}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }

        // Overall Attendance Pill
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isAtRisk) StatusAbsent.copy(alpha = 0.15f) else StatusPresent.copy(alpha = 0.12f)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              if (isAtRisk) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusAbsent, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(2.dp))
              }
              Text(
                text = "${"%.0f".format(Locale.US, overallRate)}% Overall",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isAtRisk) StatusAbsent else StatusPresent,
                fontSize = 10.sp
              )
            }
          }

          IconButton(onClick = onEditRemarks, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.EditNote, contentDescription = "Add Remark", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
          }
        }
      }

      if (remarks.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Note: $remarks",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.padding(start = 46.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Attendance Status Selection Chips (P / A / L / E)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        StatusButton(
          label = "Present",
          shortLabel = "P",
          isSelected = status == AttendanceStatus.PRESENT,
          selectedColor = StatusPresent,
          selectedContainer = StatusPresentContainer,
          onClick = { onStatusChange(AttendanceStatus.PRESENT) },
          modifier = Modifier.weight(1f)
        )

        StatusButton(
          label = "Absent",
          shortLabel = "A",
          isSelected = status == AttendanceStatus.ABSENT,
          selectedColor = StatusAbsent,
          selectedContainer = StatusAbsentContainer,
          onClick = { onStatusChange(AttendanceStatus.ABSENT) },
          modifier = Modifier.weight(1f)
        )

        StatusButton(
          label = "Late",
          shortLabel = "L",
          isSelected = status == AttendanceStatus.LATE,
          selectedColor = StatusLate,
          selectedContainer = StatusLateContainer,
          onClick = { onStatusChange(AttendanceStatus.LATE) },
          modifier = Modifier.weight(1f)
        )

        StatusButton(
          label = "Excused",
          shortLabel = "E",
          isSelected = status == AttendanceStatus.EXCUSED,
          selectedColor = StatusExcused,
          selectedContainer = StatusExcusedContainer,
          onClick = { onStatusChange(AttendanceStatus.EXCUSED) },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
fun StatusButton(
  label: String,
  shortLabel: String,
  isSelected: Boolean,
  selectedColor: Color,
  selectedContainer: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) selectedColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = modifier
      .clickable { onClick() }
      .testTag("status_button_$shortLabel")
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.padding(vertical = 8.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
        }
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
          color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      }
    }
  }
}
