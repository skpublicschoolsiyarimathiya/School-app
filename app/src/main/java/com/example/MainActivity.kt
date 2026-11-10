package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Class
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ReportViewerDialog
import com.example.ui.components.SchoolTopBar
import com.example.ui.components.StudentFormDialog
import com.example.ui.screens.attendance.AttendanceScreen
import com.example.ui.screens.classes.ClassesTeachersScreen
import com.example.ui.screens.grades.GradesScreen
import com.example.ui.screens.master.MasterDashboardScreen
import com.example.ui.screens.students.StudentDetailDialog
import com.example.ui.screens.students.StudentsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalBlue40
import com.example.ui.viewmodel.SchoolViewModel

data class NavTab(
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        EduMasterApp()
      }
    }
  }
}

@Composable
fun EduMasterApp(viewModel: SchoolViewModel = viewModel()) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  var currentTabIndex by rememberSaveable { mutableIntStateOf(0) }
  var selectedStudentDetailId by rememberSaveable { mutableStateOf<Long?>(null) }
  var showExecutiveReport by remember { mutableStateOf(false) }

  val isAdmin by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val feedbackMessage by viewModel.userFeedbackMessage.collectAsStateWithLifecycle()

  val students by viewModel.students.collectAsStateWithLifecycle()
  val studentAnalyticsMap by viewModel.studentAnalyticsMap.collectAsStateWithLifecycle()
  val grades by viewModel.grades.collectAsStateWithLifecycle()
  val subjects by viewModel.subjects.collectAsStateWithLifecycle()
  val attendance by viewModel.attendance.collectAsStateWithLifecycle()
  val classes by viewModel.classes.collectAsStateWithLifecycle()

  var studentToEditFromDetail by remember { mutableStateOf<com.example.data.model.Student?>(null) }
  var reportStudentIdFromDetail by remember { mutableStateOf<Long?>(null) }

  // Handle toast/snackbar for user actions
  LaunchedEffect(feedbackMessage) {
    feedbackMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearFeedbackMessage()
    }
  }

  // Handle Back press when modal is open
  BackHandler(enabled = selectedStudentDetailId != null) {
    selectedStudentDetailId = null
  }

  val navTabs = listOf(
    NavTab("Master", Icons.Default.AdminPanelSettings, Icons.Outlined.AdminPanelSettings, "nav_master"),
    NavTab("Attendance", Icons.Default.CheckCircle, Icons.Outlined.CheckCircle, "nav_attendance"),
    NavTab("Grades", Icons.Default.Grade, Icons.Outlined.Grade, "nav_grades"),
    NavTab("Students", Icons.Default.Groups, Icons.Outlined.Groups, "nav_students"),
    NavTab("Classes", Icons.Default.Class, Icons.Outlined.Class, "nav_classes")
  )

  Scaffold(
    topBar = {
      SchoolTopBar(
        isAdmin = isAdmin,
        onToggleAdmin = {
          viewModel.isAdminMode.value = !isAdmin
          val roleStr = if (!isAdmin) "Master Administrator (Full permissions)" else "Faculty View (Standard)"
          Toast.makeText(context, "Switched role to: $roleStr", Toast.LENGTH_SHORT).show()
        },
        onOpenReport = { showExecutiveReport = true },
        onResetData = { viewModel.resetToDemoData() }
      )
    },
    bottomBar = {
      NavigationBar(
        tonalElevation = 6.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        navTabs.forEachIndexed { index, tab ->
          val isSelected = currentTabIndex == index
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentTabIndex = index },
            icon = {
              Icon(
                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = tab.title
              )
            },
            label = {
              Text(
                text = tab.title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoyalBlue40,
              indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag(tab.testTag)
          )
        }
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTabIndex) {
        0 -> MasterDashboardScreen(
          viewModel = viewModel,
          onNavigateToStudentDetail = { studentId ->
            selectedStudentDetailId = studentId
          }
        )
        1 -> AttendanceScreen(
          viewModel = viewModel,
          onNavigateToStudentDetail = { studentId ->
            selectedStudentDetailId = studentId
          }
        )
        2 -> GradesScreen(
          viewModel = viewModel,
          onNavigateToStudentDetail = { studentId ->
            selectedStudentDetailId = studentId
          }
        )
        3 -> StudentsScreen(
          viewModel = viewModel,
          onNavigateToDetail = { studentId ->
            selectedStudentDetailId = studentId
          }
        )
        4 -> ClassesTeachersScreen(viewModel = viewModel)
      }
    }
  }

  // Student Detail Dialog when selected from any screen
  if (selectedStudentDetailId != null) {
    val student = students.find { it.id == selectedStudentDetailId }
    if (student != null) {
      val analytics = studentAnalyticsMap[student.id]
      StudentDetailDialog(
        student = student,
        analytics = analytics,
        grades = grades,
        subjects = subjects,
        attendance = attendance,
        onEdit = {
          studentToEditFromDetail = student
          selectedStudentDetailId = null
        },
        onDelete = {
          viewModel.deleteStudent(student.id, student.name)
          selectedStudentDetailId = null
        },
        onViewReportCard = {
          reportStudentIdFromDetail = student.id
        },
        onDismiss = { selectedStudentDetailId = null }
      )
    } else {
      selectedStudentDetailId = null
    }
  }

  // Edit Student from Detail Dialog
  if (studentToEditFromDetail != null) {
    StudentFormDialog(
      student = studentToEditFromDetail,
      classes = classes,
      onDismiss = { studentToEditFromDetail = null },
      onSave = {
        viewModel.saveStudent(it)
        studentToEditFromDetail = null
      }
    )
  }

  // Student Report Card from Detail Dialog
  if (reportStudentIdFromDetail != null) {
    val targetStudent = students.find { it.id == reportStudentIdFromDetail }
    val reportText = viewModel.getStudentReportCardText(reportStudentIdFromDetail!!)
    ReportViewerDialog(
      title = "Official Report Card: ${targetStudent?.name ?: ""}",
      reportText = reportText,
      onDismiss = { reportStudentIdFromDetail = null }
    )
  }

  // Executive Report from TopBar
  if (showExecutiveReport) {
    ReportViewerDialog(
      title = "Executive Performance Report",
      reportText = viewModel.getExecutiveReportText(),
      onDismiss = { showExecutiveReport = false }
    )
  }
}
