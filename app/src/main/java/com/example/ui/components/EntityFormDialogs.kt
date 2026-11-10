package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.GradeRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.Teacher

@Composable
fun StudentFormDialog(
  student: Student? = null,
  classes: List<SchoolClass>,
  onDismiss: () -> Unit,
  onSave: (Student) -> Unit
) {
  var rollNumber by remember { mutableStateOf(student?.rollNumber ?: "") }
  var name by remember { mutableStateOf(student?.name ?: "") }
  var selectedClassId by remember {
    mutableLongStateOf(student?.classId ?: classes.firstOrNull()?.id ?: 1L)
  }
  var gender by remember { mutableStateOf(student?.gender ?: "Male") }
  var guardianName by remember { mutableStateOf(student?.guardianName ?: "") }
  var contactNumber by remember { mutableStateOf(student?.contactNumber ?: "") }
  var email by remember { mutableStateOf(student?.email ?: "") }
  var dob by remember { mutableStateOf(student?.dob ?: "2009-01-01") }

  var classDropdownExpanded by remember { mutableStateOf(false) }
  var genderDropdownExpanded by remember { mutableStateOf(false) }

  val selectedClass = classes.find { it.id == selectedClassId }
  val className = selectedClass?.displayName ?: "Grade 10 - A"

  val isFormValid = rollNumber.isNotBlank() && name.isNotBlank()

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("student_form_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = if (student == null) "Add New Student" else "Edit Student (Master Correction)",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Full Name *") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_student_name"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = rollNumber,
          onValueChange = { rollNumber = it },
          label = { Text("Roll Number * (e.g. STU-1013)") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_student_roll"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Class Selector
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = className,
            onValueChange = {},
            label = { Text("Class / Section") },
            readOnly = true,
            trailingIcon = {
              androidx.compose.material3.Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.clickable { classDropdownExpanded = true }
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { classDropdownExpanded = true }
          )
          DropdownMenu(
            expanded = classDropdownExpanded,
            onDismissRequest = { classDropdownExpanded = false }
          ) {
            classes.forEach { cls ->
              DropdownMenuItem(
                text = { Text(cls.displayName) },
                onClick = {
                  selectedClassId = cls.id
                  classDropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gender Selector
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = gender,
            onValueChange = {},
            label = { Text("Gender") },
            readOnly = true,
            trailingIcon = {
              androidx.compose.material3.Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.clickable { genderDropdownExpanded = true }
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { genderDropdownExpanded = true }
          )
          DropdownMenu(
            expanded = genderDropdownExpanded,
            onDismissRequest = { genderDropdownExpanded = false }
          ) {
            listOf("Male", "Female", "Other").forEach { g ->
              DropdownMenuItem(
                text = { Text(g) },
                onClick = {
                  gender = g
                  genderDropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = guardianName,
          onValueChange = { guardianName = it },
          label = { Text("Guardian Name") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = contactNumber,
          onValueChange = { contactNumber = it },
          label = { Text("Parent / Guardian Phone") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Student Email") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = dob,
          onValueChange = { dob = it },
          label = { Text("Date of Birth (YYYY-MM-DD)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(onClick = onDismiss) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (isFormValid) {
                val newStudent = Student(
                  id = student?.id ?: 0L,
                  rollNumber = rollNumber.trim(),
                  name = name.trim(),
                  classId = selectedClassId,
                  className = className,
                  gender = gender,
                  guardianName = guardianName.trim(),
                  contactNumber = contactNumber.trim(),
                  email = email.trim(),
                  dob = dob.trim()
                )
                onSave(newStudent)
              }
            },
            enabled = isFormValid,
            modifier = Modifier.testTag("save_student_button")
          ) {
            Text(if (student == null) "Add Student" else "Save Changes")
          }
        }
      }
    }
  }
}

@Composable
fun ClassFormDialog(
  schoolClass: SchoolClass? = null,
  onDismiss: () -> Unit,
  onSave: (SchoolClass) -> Unit
) {
  var name by remember { mutableStateOf(schoolClass?.name ?: "") }
  var section by remember { mutableStateOf(schoolClass?.section ?: "") }
  var roomNumber by remember { mutableStateOf(schoolClass?.roomNumber ?: "") }
  var teacherName by remember { mutableStateOf(schoolClass?.classTeacherName ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (schoolClass == null) "Create New Class" else "Edit Class Details",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Grade / Name (e.g. Grade 11)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = section,
          onValueChange = { section = it },
          label = { Text("Section (e.g. A)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = roomNumber,
          onValueChange = { roomNumber = it },
          label = { Text("Room Number (e.g. Room 402)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = teacherName,
          onValueChange = { teacherName = it },
          label = { Text("Class Teacher Name") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onSave(
              SchoolClass(
                id = schoolClass?.id ?: 0L,
                name = name.trim(),
                section = section.trim().uppercase(),
                roomNumber = roomNumber.trim(),
                classTeacherName = teacherName.trim()
              )
            )
          }
        },
        enabled = name.isNotBlank()
      ) {
        Text("Save Class")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun TeacherFormDialog(
  teacher: Teacher? = null,
  onDismiss: () -> Unit,
  onSave: (Teacher) -> Unit
) {
  var employeeId by remember { mutableStateOf(teacher?.employeeId ?: "") }
  var name by remember { mutableStateOf(teacher?.name ?: "") }
  var email by remember { mutableStateOf(teacher?.email ?: "") }
  var phone by remember { mutableStateOf(teacher?.phone ?: "") }
  var department by remember { mutableStateOf(teacher?.department ?: "") }
  var assignedSubjects by remember { mutableStateOf(teacher?.assignedSubjects ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (teacher == null) "Add Faculty Member" else "Edit Faculty Profile",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Teacher Name *") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = employeeId,
          onValueChange = { employeeId = it },
          label = { Text("Employee ID (e.g. TCH-105)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = department,
          onValueChange = { department = it },
          label = { Text("Department (e.g. Science)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = assignedSubjects,
          onValueChange = { assignedSubjects = it },
          label = { Text("Assigned Subjects") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Address") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Phone Number") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onSave(
              Teacher(
                id = teacher?.id ?: 0L,
                employeeId = if (employeeId.isNotBlank()) employeeId.trim() else "TCH-${System.currentTimeMillis() % 1000}",
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                department = department.trim(),
                assignedSubjects = assignedSubjects.trim()
              )
            )
          }
        },
        enabled = name.isNotBlank()
      ) {
        Text("Save Teacher")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun SubjectFormDialog(
  subject: Subject? = null,
  classes: List<SchoolClass>,
  onDismiss: () -> Unit,
  onSave: (Subject) -> Unit
) {
  var code by remember { mutableStateOf(subject?.code ?: "") }
  var title by remember { mutableStateOf(subject?.title ?: "") }
  var teacherName by remember { mutableStateOf(subject?.teacherName ?: "") }
  var credits by remember { mutableIntStateOf(subject?.credits ?: 3) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (subject == null) "Add New Subject" else "Edit Subject",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Subject Title * (e.g. Biology)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = code,
          onValueChange = { code = it },
          label = { Text("Subject Code (e.g. BIO-101)") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = teacherName,
          onValueChange = { teacherName = it },
          label = { Text("Instructor / Teacher Name") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = credits.toString(),
          onValueChange = { credits = it.toIntOrNull() ?: 3 },
          label = { Text("Credits") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onSave(
              Subject(
                id = subject?.id ?: 0L,
                code = if (code.isNotBlank()) code.trim().uppercase() else "SUB-${System.currentTimeMillis() % 1000}",
                title = title.trim(),
                classId = classes.firstOrNull()?.id ?: 1L,
                teacherName = teacherName.trim(),
                credits = credits
              )
            )
          }
        },
        enabled = title.isNotBlank()
      ) {
        Text("Save Subject")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun GradeEntryDialog(
  grade: GradeRecord? = null,
  studentName: String,
  subjectName: String,
  examName: String,
  onDismiss: () -> Unit,
  onSave: (Double, Double, String) -> Unit
) {
  var marksObtainedText by remember { mutableStateOf(grade?.marksObtained?.toString() ?: "") }
  var maxMarksText by remember { mutableStateOf(grade?.maxMarks?.toString() ?: "100.0") }
  var remarks by remember { mutableStateOf(grade?.remarks ?: "") }

  val marks = marksObtainedText.toDoubleOrNull()
  val maxMarks = maxMarksText.toDoubleOrNull() ?: 100.0
  val isValid = marks != null && marks in 0.0..maxMarks

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(text = "Enter / Correct Grade", fontWeight = FontWeight.Bold)
    },
    text = {
      Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(text = "Student: $studentName", fontWeight = FontWeight.SemiBold)
        Text(text = "Subject: $subjectName", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Exam: $examName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = marksObtainedText,
            onValueChange = { marksObtainedText = it },
            label = { Text("Marks") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f).testTag("input_marks_obtained")
          )
          OutlinedTextField(
            value = maxMarksText,
            onValueChange = { maxMarksText = it },
            label = { Text("Max") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (marks != null && maxMarks > 0) {
          val pct = (marks / maxMarks) * 100.0
          val letter = when {
            pct >= 90.0 -> "A+"
            pct >= 80.0 -> "A"
            pct >= 70.0 -> "B"
            pct >= 60.0 -> "C"
            pct >= 50.0 -> "D"
            else -> "F"
          }
          Text(
            text = "Calculated: ${"%.1f".format(pct)}% ($letter Grade)",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = remarks,
          onValueChange = { remarks = it },
          label = { Text("Teacher Remarks / Notes") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (isValid && marks != null) {
            onSave(marks, maxMarks, remarks.trim())
          }
        },
        enabled = isValid,
        modifier = Modifier.testTag("submit_grade_button")
      ) {
        Text("Save Grade")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
