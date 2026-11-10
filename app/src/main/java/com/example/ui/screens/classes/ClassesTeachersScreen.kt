package com.example.ui.screens.classes

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
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import com.example.data.model.SchoolClass
import com.example.data.model.Subject
import com.example.data.model.Teacher
import com.example.ui.components.ClassFormDialog
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.SubjectFormDialog
import com.example.ui.components.TeacherFormDialog
import com.example.ui.theme.RoyalBlue40
import com.example.ui.viewmodel.SchoolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesTeachersScreen(viewModel: SchoolViewModel) {
  val classes by viewModel.classes.collectAsStateWithLifecycle()
  val students by viewModel.students.collectAsStateWithLifecycle()
  val teachers by viewModel.teachers.collectAsStateWithLifecycle()
  val subjects by viewModel.subjects.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Classes & Subjects, 1: Faculty

  // Dialogs
  var showAddClassDialog by remember { mutableStateOf(false) }
  var editingClass by remember { mutableStateOf<SchoolClass?>(null) }
  var classToDelete by remember { mutableStateOf<SchoolClass?>(null) }

  var showAddSubjectDialog by remember { mutableStateOf(false) }
  var editingSubject by remember { mutableStateOf<Subject?>(null) }
  var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

  var showAddTeacherDialog by remember { mutableStateOf(false) }
  var editingTeacher by remember { mutableStateOf<Teacher?>(null) }
  var teacherToDelete by remember { mutableStateOf<Teacher?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("classes_teachers_screen")
  ) {
    PrimaryTabRow(selectedTabIndex = selectedTab) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Classes & Subjects", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Default.Class, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.testTag("tab_classes_subjects")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Faculty & Staff", fontWeight = FontWeight.Bold) },
        icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp)) },
        modifier = Modifier.testTag("tab_faculty")
      )
    }

    if (selectedTab == 0) {
      // ---------------- CLASSES & SUBJECTS TAB ----------------
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Classes Header with Add button
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Class Sections (${classes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Manage grades, sections, and room assignments",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
            FilledTonalButton(
              onClick = { showAddClassDialog = true },
              modifier = Modifier.testTag("add_class_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add Class", fontSize = 11.sp)
            }
          }
        }

        items(classes) { cls ->
          val studentCount = students.count { it.classId == cls.id }
          val classSubjects = subjects.filter { it.classId == cls.id || it.classId == 0L }

          ClassCardItem(
            schoolClass = cls,
            studentCount = studentCount,
            subjectCount = classSubjects.size,
            onEdit = { editingClass = cls },
            onDelete = { classToDelete = cls }
          )
        }

        // Subjects Section Header with Add button
        item {
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Curriculum Subjects (${subjects.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Academic courses and assigned instructors",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
            FilledTonalButton(
              onClick = { showAddSubjectDialog = true },
              modifier = Modifier.testTag("add_subject_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add Subject", fontSize = 11.sp)
            }
          }
        }

        items(subjects) { subject ->
          SubjectCardItem(
            subject = subject,
            onEdit = { editingSubject = subject },
            onDelete = { subjectToDelete = subject }
          )
        }

        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    } else {
      // ---------------- FACULTY & STAFF TAB ----------------
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Teaching Faculty & Staff (${teachers.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Instructors, departments, and course assignments",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
            FilledTonalButton(
              onClick = { showAddTeacherDialog = true },
              modifier = Modifier.testTag("add_teacher_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add Faculty", fontSize = 11.sp)
            }
          }
        }

        items(teachers) { teacher ->
          TeacherCardItem(
            teacher = teacher,
            onEdit = { editingTeacher = teacher },
            onDelete = { teacherToDelete = teacher }
          )
        }

        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }

  // Dialogs
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

  if (classToDelete != null) {
    val cls = classToDelete!!
    DeleteConfirmationDialog(
      title = "Confirm Delete Class",
      message = "Are you sure you want to delete class ${cls.displayName}?",
      itemName = cls.displayName,
      onConfirm = {
        viewModel.deleteClass(cls.id, cls.displayName)
        classToDelete = null
      },
      onDismiss = { classToDelete = null }
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

  if (subjectToDelete != null) {
    val sub = subjectToDelete!!
    DeleteConfirmationDialog(
      title = "Confirm Delete Subject",
      message = "Are you sure you want to remove subject ${sub.title}?",
      itemName = "${sub.title} (${sub.code})",
      onConfirm = {
        viewModel.deleteSubject(sub.id, sub.title)
        subjectToDelete = null
      },
      onDismiss = { subjectToDelete = null }
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

  if (teacherToDelete != null) {
    val tch = teacherToDelete!!
    DeleteConfirmationDialog(
      title = "Confirm Delete Faculty Member",
      message = "Are you sure you want to remove ${tch.name} from the school roster?",
      itemName = "${tch.name} (${tch.employeeId})",
      onConfirm = {
        viewModel.deleteTeacher(tch.id, tch.name)
        teacherToDelete = null
      },
      onDismiss = { teacherToDelete = null }
    )
  }
}

@Composable
fun ClassCardItem(
  schoolClass: SchoolClass,
  studentCount: Int,
  subjectCount: Int,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = schoolClass.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = schoolClass.roomNumber, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            Text(text = " • Teacher: ${schoolClass.classTeacherName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
          }
        }

        Row {
          IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Class", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Class", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
          Text(
            text = "$studentCount Enrolled Students",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ) {
          Text(
            text = "$subjectCount Subjects Mapped",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }
  }
}

@Composable
fun SubjectCardItem(
  subject: Subject,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = subject.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = subject.code,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
              fontSize = 9.sp
            )
          }
        }
        Text(
          text = "Instructor: ${subject.teacherName} • ${subject.credits} Credits",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline
        )
      }

      Row {
        IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit Subject", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete Subject", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@Composable
fun TeacherCardItem(
  teacher: Teacher,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
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
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(42.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = teacher.name.take(2).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(text = teacher.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
              text = "${teacher.department} • ID: ${teacher.employeeId}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }

        Row {
          IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Faculty", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Faculty", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Teaches: ${teacher.assignedSubjects}",
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = teacher.email, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = teacher.phone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
      }
    }
  }
}
