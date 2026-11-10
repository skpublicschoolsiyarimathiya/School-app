package com.example.ui.screens.master

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AttendanceRecord
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.Teacher
import com.example.domain.SchoolDashboardMetrics
import com.example.domain.StudentAnalytics
import com.example.ui.components.ClassFormDialog
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.ReportViewerDialog
import com.example.ui.components.StudentFormDialog
import com.example.ui.components.SubjectFormDialog
import com.example.ui.components.TeacherFormDialog
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.Gold40
import com.example.ui.theme.RoyalBlue30
import com.example.ui.theme.RoyalBlue40
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusPresent
import com.example.ui.viewmodel.SchoolViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterDashboardScreen(
  viewModel: SchoolViewModel,
  onNavigateToStudentDetail: (Long) -> Unit
) {
  val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
  val classes by viewModel.classes.collectAsStateWithLifecycle()
  val students by viewModel.students.collectAsStateWithLifecycle()
  val teachers by viewModel.teachers.collectAsStateWithLifecycle()
  val subjects by viewModel.subjects.collectAsStateWithLifecycle()
  val grades by viewModel.grades.collectAsStateWithLifecycle()
  val attendance by viewModel.attendance.collectAsStateWithLifecycle()

  var showExecutiveReport by remember { mutableStateOf(false) }
  var showDemoResetDialog by remember { mutableStateOf(false) }

  // CRUD state dialogs
  var editingStudent by remember { mutableStateOf<Student?>(null) }
  var showAddStudentDialog by remember { mutableStateOf(false) }

  var editingClass by remember { mutableStateOf<SchoolClass?>(null) }
  var showAddClassDialog by remember { mutableStateOf(false) }

  var editingTeacher by remember { mutableStateOf<Teacher?>(null) }
  var showAddTeacherDialog by remember { mutableStateOf(false) }

  var editingSubject by remember { mutableStateOf<Subject?>(null) }
  var showAddSubjectDialog by remember { mutableStateOf(false) }

  // Delete item pending confirmation
  var itemToDelete by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }

  // Oversight sub-tab
  var selectedOversightTab by remember { mutableIntStateOf(0) }
  val oversightTabs = listOf("Students", "Teachers", "Classes", "Subjects", "Grades", "Attendance")

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("master_dashboard_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Executive Master Hero Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("master_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                colors = listOf(RoyalBlue40, RoyalBlue30, AcademicNavy)
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Master Administration",
                  style = MaterialTheme.typography.titleMedium,
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 13.sp
                )
                Text(
                  text = "School Executive Overview",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
              Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color(0xFFFDE68A),
                    modifier = Modifier.size(24.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Full system permissions active: correct, add, and delete student data, faculty profiles, attendance logs, and exam marks.",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.9f),
              lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = { showExecutiveReport = true },
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFFFDE68A),
                  contentColor = Color(0xFF78350F)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("generate_report_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Assessment,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Performance Report", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }

              OutlinedButton(
                onClick = { showAddStudentDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                  brush = Brush.linearGradient(listOf(Color.White, Color.White))
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("quick_add_student_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Student", fontSize = 12.sp)
              }
            }
          }
        }
      }
    }

    // 2. KPI Metrics Grid (2x3)
    item {
      Text(
        text = "Key Performance Indicators",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          KpiCard(
            title = "Total Students",
            value = metrics.totalStudents.toString(),
            subtitle = "${metrics.totalClasses} Active Classes",
            icon = Icons.Default.Groups,
            iconTint = RoyalBlue40,
            modifier = Modifier.weight(1f)
          )
          KpiCard(
            title = "Avg Attendance",
            value = "${"%.1f".format(Locale.US, metrics.overallAttendanceRate)}%",
            subtitle = if (metrics.overallAttendanceRate >= 80.0) "Good Standing" else "Requires Attention",
            icon = Icons.Default.CheckCircle,
            iconTint = StatusPresent,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          KpiCard(
            title = "School-Wide GPA",
            value = "${"%.2f".format(Locale.US, metrics.overallSchoolGpa)} / 4.0",
            subtitle = "Based on all graded exams",
            icon = Icons.Default.Grade,
            iconTint = Gold40,
            modifier = Modifier.weight(1f)
          )
          KpiCard(
            title = "At-Risk Alerts",
            value = metrics.atRiskStudentsCount.toString(),
            subtitle = if (metrics.atRiskStudentsCount > 0) "Immediate Action Needed" else "All Thresholds Met",
            icon = Icons.Default.Warning,
            iconTint = if (metrics.atRiskStudentsCount > 0) StatusAbsent else StatusPresent,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 3. At-Risk Early Intervention Watchlist
    if (metrics.atRiskStudents.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
          ),
          modifier = Modifier.fillMaxWidth().testTag("at_risk_watchlist_card")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Academic & Attendance Early Warning Watchlist",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            metrics.atRiskStudents.forEach { alert ->
              Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clickable { onNavigateToStudentDetail(alert.student.id) }
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = alert.student.name,
                      fontWeight = FontWeight.Bold,
                      style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                      text = "${alert.student.className} • Roll: ${alert.student.rollNumber}",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.outline
                    )
                    Row(
                      modifier = Modifier.padding(top = 4.dp),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      if (alert.isAttendanceAtRisk) {
                        Surface(
                          color = StatusAbsent.copy(alpha = 0.15f),
                          shape = RoundedCornerShape(4.dp)
                        ) {
                          Text(
                            text = "Attendance: ${"%.1f".format(Locale.US, alert.attendanceRate)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusAbsent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                      if (alert.isAcademicAtRisk) {
                        Surface(
                          color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                          shape = RoundedCornerShape(4.dp)
                        ) {
                          Text(
                            text = "GPA: ${"%.2f".format(Locale.US, alert.cumulativeGpa)} (${alert.letterGrade})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                  }
                  IconButton(
                    onClick = { onNavigateToStudentDetail(alert.student.id) }
                  ) {
                    Icon(
                      imageVector = Icons.Default.Edit,
                      contentDescription = "Review Student",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 4. Master Data Oversight Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Master Data Oversight & Control",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Direct CRUD permissions across all school tables",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
        }

        // Add action button dynamically matching current tab
        FilledTonalButton(
          onClick = {
            when (selectedOversightTab) {
              0 -> showAddStudentDialog = true
              1 -> showAddTeacherDialog = true
              2 -> showAddClassDialog = true
              3 -> showAddSubjectDialog = true
              else -> showAddStudentDialog = true
            }
          },
          modifier = Modifier.testTag("master_add_entry_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            when (selectedOversightTab) {
              0 -> "Student"
              1 -> "Teacher"
              2 -> "Class"
              3 -> "Subject"
              else -> "Add"
            },
            fontSize = 11.sp
          )
        }
      }
    }

    // 5. Tabs for Master Data
    item {
      ScrollableTabRow(
        selectedTabIndex = selectedOversightTab,
        edgePadding = 0.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        oversightTabs.forEachIndexed { index, tabName ->
          Tab(
            selected = selectedOversightTab == index,
            onClick = { selectedOversightTab = index },
            text = { Text(tabName, fontWeight = if (selectedOversightTab == index) FontWeight.Bold else FontWeight.Normal) }
          )
        }
      }
    }

    // 6. Dynamic Content according to selected tab
    when (selectedOversightTab) {
      0 -> { // Students Table
        if (students.isEmpty()) {
          item { EmptyDataPlaceholder("No students found. Tap '+ Student' to add.") }
        } else {
          items(students) { student ->
            MasterStudentItem(
              student = student,
              onEdit = { editingStudent = student },
              onDelete = {
                itemToDelete = Pair("Student '${student.name}'") {
                  viewModel.deleteStudent(student.id, student.name)
                }
              },
              onClick = { onNavigateToStudentDetail(student.id) }
            )
          }
        }
      }
      1 -> { // Teachers Table
        if (teachers.isEmpty()) {
          item { EmptyDataPlaceholder("No faculty members found. Tap '+ Teacher' to add.") }
        } else {
          items(teachers) { teacher ->
            MasterTeacherItem(
              teacher = teacher,
              onEdit = { editingTeacher = teacher },
              onDelete = {
                itemToDelete = Pair("Teacher '${teacher.name}'") {
                  viewModel.deleteTeacher(teacher.id, teacher.name)
                }
              }
            )
          }
        }
      }
      2 -> { // Classes Table
        if (classes.isEmpty()) {
          item { EmptyDataPlaceholder("No classes found. Tap '+ Class' to create.") }
        } else {
          items(classes) { cls ->
            val classStudentsCount = students.count { it.classId == cls.id }
            MasterClassItem(
              schoolClass = cls,
              studentCount = classStudentsCount,
              onEdit = { editingClass = cls },
              onDelete = {
                itemToDelete = Pair("Class '${cls.displayName}'") {
                  viewModel.deleteClass(cls.id, cls.displayName)
                }
              }
            )
          }
        }
      }
      3 -> { // Subjects Table
        if (subjects.isEmpty()) {
          item { EmptyDataPlaceholder("No subjects found. Tap '+ Subject' to add.") }
        } else {
          items(subjects) { subject ->
            MasterSubjectItem(
              subject = subject,
              onEdit = { editingSubject = subject },
              onDelete = {
                itemToDelete = Pair("Subject '${subject.title}'") {
                  viewModel.deleteSubject(subject.id, subject.title)
                }
              }
            )
          }
        }
      }
      4 -> { // Grades Records
        if (grades.isEmpty()) {
          item { EmptyDataPlaceholder("No grade entries recorded yet. Go to Grades module.") }
        } else {
          items(grades.take(25)) { grade ->
            val student = students.find { it.id == grade.studentId }
            val subject = subjects.find { it.id == grade.subjectId }
            MasterGradeItem(
              grade = grade,
              studentName = student?.name ?: "Student #${grade.studentId}",
              subjectTitle = subject?.title ?: "Subject #${grade.subjectId}",
              onDelete = {
                itemToDelete = Pair("Grade record for ${student?.name} (${grade.examName})") {
                  viewModel.deleteGrade(grade.id)
                }
              }
            )
          }
        }
      }
      5 -> { // Attendance Records
        if (attendance.isEmpty()) {
          item { EmptyDataPlaceholder("No attendance records logged yet. Go to Attendance module.") }
        } else {
          items(attendance.take(25)) { att ->
            val student = students.find { it.id == att.studentId }
            MasterAttendanceItem(
              record = att,
              studentName = student?.name ?: "Student #${att.studentId}",
              onDelete = {
                itemToDelete = Pair("Attendance record for ${student?.name} on ${att.date}") {
                  viewModel.deleteAttendanceRecord(att.id)
                }
              }
            )
          }
        }
      }
    }

    // 7. Danger Zone / Database Seeder Actions
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Administrator Database Controls",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Easily reset or refresh with complete sample school dataset or clear all records for production setup.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilledTonalButton(
              onClick = { viewModel.resetToDemoData() },
              modifier = Modifier.weight(1f).testTag("reset_demo_data_button")
            ) {
              Text("Reload Demo Data", fontSize = 11.sp)
            }
            OutlinedButton(
              onClick = {
                itemToDelete = Pair("ALL SCHOOL RECORDS") {
                  viewModel.wipeAllData()
                }
              },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
              modifier = Modifier.weight(1f).testTag("wipe_data_button")
            ) {
              Text("Clear All Data", fontSize = 11.sp)
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  // Dialogs
  if (showExecutiveReport) {
    ReportViewerDialog(
      title = "Executive Performance Report",
      reportText = viewModel.getExecutiveReportText(),
      onDismiss = { showExecutiveReport = false }
    )
  }

  if (showAddStudentDialog) {
    StudentFormDialog(
      student = null,
      classes = classes,
      onDismiss = { showAddStudentDialog = false },
      onSave = {
        viewModel.saveStudent(it)
        showAddStudentDialog = false
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

  if (showAddClassDialog) {
    ClassFormDialog(
      schoolClass = null,
      onDismiss = { showAddClassDialog = false },
      onSave = {
        viewModel.saveClass(it)
        showAddClassDialog = false
      }
    )
  }

  if (editingClass != null) {
    ClassFormDialog(
      schoolClass = editingClass,
      onDismiss = { editingClass = null },
      onSave = {
        viewModel.saveClass(it)
        editingClass = null
      }
    )
  }

  if (showAddTeacherDialog) {
    TeacherFormDialog(
      teacher = null,
      onDismiss = { showAddTeacherDialog = false },
      onSave = {
        viewModel.saveTeacher(it)
        showAddTeacherDialog = false
      }
    )
  }

  if (editingTeacher != null) {
    TeacherFormDialog(
      teacher = editingTeacher,
      onDismiss = { editingTeacher = null },
      onSave = {
        viewModel.saveTeacher(it)
        editingTeacher = null
      }
    )
  }

  if (showAddSubjectDialog) {
    SubjectFormDialog(
      subject = null,
      classes = classes,
      onDismiss = { showAddSubjectDialog = false },
      onSave = {
        viewModel.saveSubject(it)
        showAddSubjectDialog = false
      }
    )
  }

  if (editingSubject != null) {
    SubjectFormDialog(
      subject = editingSubject,
      classes = classes,
      onDismiss = { editingSubject = null },
      onSave = {
        viewModel.saveSubject(it)
        editingSubject = null
      }
    )
  }

  if (itemToDelete != null) {
    DeleteConfirmationDialog(
      title = "Confirm Master Deletion",
      message = "Are you sure you want to permanently delete this item?",
      itemName = itemToDelete!!.first,
      onConfirm = {
        itemToDelete!!.second.invoke()
        itemToDelete = null
      },
      onDismiss = { itemToDelete = null }
    )
  }
}

@Composable
fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline,
          fontWeight = FontWeight.Medium
        )
        Surface(
          color = iconTint.copy(alpha = 0.12f),
          shape = CircleShape,
          modifier = Modifier.size(32.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = iconTint,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline,
        fontSize = 10.sp
      )
    }
  }
}

@Composable
fun MasterStudentItem(
  student: Student,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = student.name.take(2).uppercase(),
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(text = student.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
          Text(
            text = "${student.className} • Roll: ${student.rollNumber}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
        }
      }

      Row {
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@Composable
fun MasterTeacherItem(
  teacher: Teacher,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = teacher.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(
          text = "${teacher.department} • ${teacher.assignedSubjects}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline
        )
        Text(
          text = "${teacher.email} • ID: ${teacher.employeeId}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Row {
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@Composable
fun MasterClassItem(
  schoolClass: SchoolClass,
  studentCount: Int,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = schoolClass.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(
          text = "Teacher: ${schoolClass.classTeacherName} • ${schoolClass.roomNumber}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline
        )
        Text(
          text = "$studentCount Enrolled Students",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary
        )
      }
      Row {
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@Composable
fun MasterSubjectItem(
  subject: Subject,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = subject.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(
          text = "Code: ${subject.code} • Teacher: ${subject.teacherName}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline
        )
        Text(
          text = "${subject.credits} Credits",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
      }
      Row {
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@Composable
fun MasterGradeItem(
  grade: GradeRecord,
  studentName: String,
  subjectTitle: String,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(
          text = "$subjectTitle • ${grade.examName}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline
        )
        Text(
          text = "Marks: ${grade.marksObtained.toInt()} / ${grade.maxMarks.toInt()} (${"%.1f".format(grade.percentage)}% - ${grade.letterGrade})",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )
      }
      IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
      }
    }
  }
}

@Composable
fun MasterAttendanceItem(
  record: AttendanceRecord,
  studentName: String,
  onDelete: () -> Unit
) {
  val statusColor = when (record.status) {
    com.example.data.model.AttendanceStatus.PRESENT -> StatusPresent
    com.example.data.model.AttendanceStatus.ABSENT -> StatusAbsent
    com.example.data.model.AttendanceStatus.LATE -> com.example.ui.theme.StatusLate
    com.example.data.model.AttendanceStatus.EXCUSED -> com.example.ui.theme.StatusExcused
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(
          text = "Date: ${record.date} • ${if (record.remarks.isNotBlank()) record.remarks else "Normal"}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline
        )
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = statusColor.copy(alpha = 0.15f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = record.status.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = statusColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@Composable
fun EmptyDataPlaceholder(message: String) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.outline
      )
    }
  }
}
