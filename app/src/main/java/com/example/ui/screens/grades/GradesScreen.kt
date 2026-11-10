package com.example.ui.screens.grades

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.ui.components.GradeEntryDialog
import com.example.ui.components.ReportViewerDialog
import com.example.ui.theme.Gold40
import com.example.ui.theme.RoyalBlue40
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusPresent
import com.example.ui.viewmodel.SchoolViewModel
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(
  viewModel: SchoolViewModel,
  onNavigateToStudentDetail: (Long) -> Unit
) {
  val classes by viewModel.classes.collectAsStateWithLifecycle()
  val students by viewModel.students.collectAsStateWithLifecycle()
  val subjects by viewModel.subjects.collectAsStateWithLifecycle()
  val grades by viewModel.grades.collectAsStateWithLifecycle()
  val examNames by viewModel.examNames.collectAsStateWithLifecycle()
  val studentAnalyticsMap by viewModel.studentAnalyticsMap.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Grade Entry, 1: Report Cards

  // Selections for Grade Entry
  var selectedClassId by remember(classes) {
    mutableLongStateOf(classes.firstOrNull()?.id ?: 1L)
  }
  var selectedSubjectId by remember(subjects) {
    mutableLongStateOf(subjects.firstOrNull()?.id ?: 1L)
  }
  var selectedExamName by remember(examNames) {
    mutableStateOf(if (examNames.isNotEmpty()) examNames.first() else "Midterm Examination")
  }

  // Active Grade Entry Dialog
  var activeGradeEdit by remember {
    mutableStateOf<Pair<Student, GradeRecord?>?>(null)
  }

  // Active Report Card Dialog
  var viewingStudentReportId by remember { mutableStateOf<Long?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("grades_screen")
  ) {
    // Top Tabs: Grade Entry vs Report Cards
    PrimaryTabRow(selectedTabIndex = selectedTab) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Grade Entry & Exams", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.testTag("tab_grade_entry")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Report Cards & GPA", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.testTag("tab_report_cards")
      )
    }

    if (selectedTab == 0) {
      // ---------------- TAB 0: GRADE ENTRY ----------------
      val classStudents = students.filter { it.classId == selectedClassId }
      val currentSubject = subjects.find { it.id == selectedSubjectId }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Class Selector
        item {
          Column {
            Text(
              text = "Select Class",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(classes) { cls ->
                FilterChip(
                  selected = selectedClassId == cls.id,
                  onClick = { selectedClassId = cls.id },
                  label = { Text(cls.displayName) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalBlue40,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }
          }
        }

        // Subject Selector
        item {
          Column {
            Text(
              text = "Select Subject",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(subjects) { sub ->
                FilterChip(
                  selected = selectedSubjectId == sub.id,
                  onClick = { selectedSubjectId = sub.id },
                  label = { Text("${sub.title} (${sub.code})") },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }
          }
        }

        // Exam Assessment Selector
        item {
          Column {
            Text(
              text = "Assessment / Exam",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
            val defaultExams = listOf("Midterm Examination", "Unit Test 1", "Final Examination", "Quiz 1")
            val availableExams = (examNames + defaultExams).distinct()
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(availableExams) { exam ->
                FilterChip(
                  selected = selectedExamName == exam,
                  onClick = { selectedExamName = exam },
                  label = { Text(exam) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }
          }
        }

        // Class Roster Header
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Roster Grades (${classStudents.size} Students)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${currentSubject?.title ?: ""}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Student Score Cards
        items(classStudents) { student ->
          val gradeRecord = grades.find {
            it.studentId == student.id &&
              it.subjectId == selectedSubjectId &&
              it.examName == selectedExamName
          }

          StudentGradeEntryCard(
            student = student,
            grade = gradeRecord,
            onEditGrade = {
              activeGradeEdit = Pair(student, gradeRecord)
            }
          )
        }

        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    } else {
      // ---------------- TAB 1: REPORT CARDS ----------------
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Comprehensive Academic Transcripts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Inspect any student's complete academic record, subject-by-subject grades, cumulative GPA, and generate official shareable report cards.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }

        items(students) { student ->
          val analytics = studentAnalyticsMap[student.id]
          val studentGrades = grades.filter { it.studentId == student.id }

          TranscriptSummaryCard(
            student = student,
            gpa = analytics?.cumulativeGpa ?: 0.0,
            letterGrade = analytics?.letterGrade ?: "N/A",
            attendanceRate = analytics?.attendanceRate ?: 100.0,
            standing = analytics?.academicStanding ?: "Enrolled",
            totalGrades = studentGrades.size,
            onViewReport = {
              viewingStudentReportId = student.id
            }
          )
        }

        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }

  // Active Grade Entry Dialog
  if (activeGradeEdit != null) {
    val (student, existingGrade) = activeGradeEdit!!
    val currentSubject = subjects.find { it.id == selectedSubjectId }

    GradeEntryDialog(
      grade = existingGrade,
      studentName = student.name,
      subjectName = currentSubject?.title ?: "Subject",
      examName = selectedExamName,
      onDismiss = { activeGradeEdit = null },
      onSave = { marks, maxMarks, remarks ->
        val record = GradeRecord(
          id = existingGrade?.id ?: 0L,
          studentId = student.id,
          subjectId = selectedSubjectId,
          examName = selectedExamName,
          marksObtained = marks,
          maxMarks = maxMarks,
          remarks = remarks,
          dateRecorded = LocalDate.now().toString()
        )
        viewModel.saveGrade(record)
        activeGradeEdit = null
      }
    )
  }

  // Active Student Report Card Dialog
  if (viewingStudentReportId != null) {
    val stId = viewingStudentReportId!!
    val student = students.find { it.id == stId }
    val reportText = viewModel.getStudentReportCardText(stId)

    ReportViewerDialog(
      title = "Official Report Card: ${student?.name ?: ""}",
      reportText = reportText,
      onDismiss = { viewingStudentReportId = null }
    )
  }
}

@Composable
fun StudentGradeEntryCard(
  student: Student,
  grade: GradeRecord?,
  onEditGrade: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
          modifier = Modifier.size(38.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = student.name.take(2).uppercase(),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(text = student.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
          Text(
            text = "Roll: ${student.rollNumber}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (grade != null) {
          Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${grade.marksObtained.toInt()} / ${grade.maxMarks.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              GradeBadge(letter = grade.letterGrade)
            }
            Text(
              text = "${"%.1f".format(grade.percentage)}%",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.outline
            )
          }
        } else {
          Text(
            text = "Not Graded",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        FilledTonalButton(
          onClick = onEditGrade,
          modifier = Modifier.testTag("grade_student_${student.rollNumber}")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (grade != null) "Edit" else "Grade", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
fun GradeBadge(letter: String) {
  val badgeColor = when (letter) {
    "A+", "A" -> Color(0xFF10B981)
    "B" -> Color(0xFF3B82F6)
    "C" -> Color(0xFFF59E0B)
    "D" -> Color(0xFFEA580C)
    else -> Color(0xFFEF4444)
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = badgeColor.copy(alpha = 0.15f)
  ) {
    Text(
      text = letter,
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.ExtraBold,
      color = badgeColor,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
  }
}

@Composable
fun TranscriptSummaryCard(
  student: Student,
  gpa: Double,
  letterGrade: String,
  attendanceRate: Double,
  standing: String,
  totalGrades: Int,
  onViewReport: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = student.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text(
            text = "${student.className} • Roll: ${student.rollNumber}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
        }
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer,
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = if (totalGrades > 0) "${"%.2f".format(Locale.US, gpa)}" else "N/A",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "GPA (4.0)",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 9.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = standing,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (attendanceRate >= 75.0) StatusPresent.copy(alpha = 0.12f) else StatusAbsent.copy(alpha = 0.12f)
          ) {
            Text(
              text = "${"%.0f".format(attendanceRate)}% Att.",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = if (attendanceRate >= 75.0) StatusPresent else StatusAbsent,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Button(
          onClick = onViewReport,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue40),
          modifier = Modifier.testTag("view_report_card_${student.rollNumber}")
        ) {
          Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Official Report Card", fontSize = 11.sp)
        }
      }
    }
  }
}
