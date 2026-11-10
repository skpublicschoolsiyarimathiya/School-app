package com.example.ui.screens.students

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Student
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.ReportViewerDialog
import com.example.ui.components.StudentFormDialog
import com.example.ui.theme.RoyalBlue40
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusPresent
import com.example.ui.viewmodel.SchoolViewModel
import java.util.Locale

@Composable
fun StudentsScreen(
  viewModel: SchoolViewModel,
  onNavigateToDetail: (Long) -> Unit
) {
  val classes by viewModel.classes.collectAsStateWithLifecycle()
  val students by viewModel.students.collectAsStateWithLifecycle()
  val studentAnalyticsMap by viewModel.studentAnalyticsMap.collectAsStateWithLifecycle()

  var searchQuery by remember { mutableStateOf("") }
  var selectedClassFilter by remember { mutableStateOf<Long?>(null) }

  // Dialogs
  var showAddDialog by remember { mutableStateOf(false) }
  var editingStudent by remember { mutableStateOf<Student?>(null) }
  var studentToDelete by remember { mutableStateOf<Student?>(null) }
  var reportStudentId by remember { mutableStateOf<Long?>(null) }

  val filteredStudents = remember(students, searchQuery, selectedClassFilter) {
    students.filter { student ->
      val matchesClass = selectedClassFilter == null || student.classId == selectedClassFilter
      val matchesSearch = searchQuery.isBlank() ||
        student.name.contains(searchQuery, ignoreCase = true) ||
        student.rollNumber.contains(searchQuery, ignoreCase = true)
      matchesClass && matchesSearch
    }
  }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = RoyalBlue40,
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_add_student")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Student")
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .testTag("students_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Search Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by student name or roll number...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("student_search_input")
        )
      }

      // 2. Class Filter Chips
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          item {
            FilterChip(
              selected = selectedClassFilter == null,
              onClick = { selectedClassFilter = null },
              label = { Text("All (${students.size})") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RoyalBlue40,
                selectedLabelColor = Color.White
              )
            )
          }
          items(classes) { cls ->
            val count = students.count { it.classId == cls.id }
            FilterChip(
              selected = selectedClassFilter == cls.id,
              onClick = { selectedClassFilter = cls.id },
              label = { Text("${cls.displayName} ($count)") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RoyalBlue40,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      // 3. Header & Result Counter
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Enrolled Students (${filteredStudents.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Master Admin full access",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      // 4. Student Cards
      if (filteredStudents.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) {
              Text(
                text = "No students match the criteria. Tap '+' to add a student.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }
      } else {
        items(filteredStudents) { student ->
          val analytics = studentAnalyticsMap[student.id]

          DetailedStudentCard(
            student = student,
            attendanceRate = analytics?.attendanceRate ?: 100.0,
            isAttendanceAtRisk = analytics?.isAttendanceAtRisk ?: false,
            gpa = analytics?.cumulativeGpa ?: 0.0,
            letterGrade = analytics?.letterGrade ?: "N/A",
            onEdit = { editingStudent = student },
            onDelete = { studentToDelete = student },
            onViewReport = { reportStudentId = student.id },
            onClick = { onNavigateToDetail(student.id) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(72.dp)) // Space for FAB
      }
    }
  }

  // Dialogs
  if (showAddDialog) {
    StudentFormDialog(
      student = null,
      classes = classes,
      onDismiss = { showAddDialog = false },
      onSave = {
        viewModel.saveStudent(it)
        showAddDialog = false
      }
    )
  }

  if (editingStudent != null) {
    StudentFormDialog(
      student = editingStudent,
      classes = classes,
      onDismiss = { editingStudent = null },
      onSave = {
        viewModel.saveStudent(it)
        editingStudent = null
      }
    )
  }

  if (studentToDelete != null) {
    val target = studentToDelete!!
    DeleteConfirmationDialog(
      title = "Confirm Delete Student",
      message = "Are you sure you want to permanently remove ${target.name}? All their attendance logs and grade records will be deleted as well.",
      itemName = "${target.name} (${target.rollNumber})",
      onConfirm = {
        viewModel.deleteStudent(target.id, target.name)
        studentToDelete = null
      },
      onDismiss = { studentToDelete = null }
    )
  }

  if (reportStudentId != null) {
    val stId = reportStudentId!!
    val student = students.find { it.id == stId }
    ReportViewerDialog(
      title = "Official Report Card: ${student?.name ?: ""}",
      reportText = viewModel.getStudentReportCardText(stId),
      onDismiss = { reportStudentId = null }
    )
  }
}

@Composable
fun DetailedStudentCard(
  student: Student,
  attendanceRate: Double,
  isAttendanceAtRisk: Boolean,
  gpa: Double,
  letterGrade: String,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onViewReport: () -> Unit,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("student_card_${student.rollNumber}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(42.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = student.name.take(2).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = student.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${student.className} • Roll: ${student.rollNumber}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }

        // Master Admin Actions (Edit & Delete)
        Row {
          IconButton(onClick = onViewReport, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Assessment, contentDescription = "Report Card", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = onEdit, modifier = Modifier.size(34.dp).testTag("edit_student_${student.rollNumber}")) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Student", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(34.dp).testTag("delete_student_${student.rollNumber}")) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Student", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Guardian & Contact Info
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.Person,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Guardian: ${student.guardianName} (${student.contactNumber})",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Performance Summary Pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Attendance Pill
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isAttendanceAtRisk) StatusAbsent.copy(alpha = 0.12f) else StatusPresent.copy(alpha = 0.12f),
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            if (isAttendanceAtRisk) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = StatusAbsent, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
              text = "Att: ${"%.1f".format(Locale.US, attendanceRate)}%",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = if (isAttendanceAtRisk) StatusAbsent else StatusPresent
            )
          }
        }

        // GPA Pill
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "GPA: ${"%.2f".format(Locale.US, gpa)} ($letterGrade)",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}
