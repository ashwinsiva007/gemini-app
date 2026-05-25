package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserEntity
import com.example.data.model.EmergencyContact
import com.example.data.model.MedicalInfo
import com.example.data.model.VehicleInfo
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashcamViewModel

@Composable
fun ProfileScreenView(viewModel: DashcamViewModel) {
    val userEntity by viewModel.userEntity.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Personal, 1: Vehicle, 2: Emergency, 3: Medical

    val tabs = listOf("Personal", "Vehicle", "Emergency", "Medical")
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 768
    val paddingSize = if (isCompact) 12.dp else 16.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
            .padding(paddingSize),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Pilot Setup Dashboard",
                fontSize = if (isCompact) 20.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Keep telemetry files and crash responses up to date",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        userEntity?.let { user ->
            // Tab Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, CardSurfaceElevated, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) AccentBlue else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = if (isCompact) 8.dp else 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = if (isCompact) 10.sp else 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tab Content Window wrapper
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurface)
                    .border(1.dp, CardSurfaceElevated, RoundedCornerShape(16.dp))
                    .padding(if (isCompact) 12.dp else 16.dp)
            ) {
                when (selectedTab) {
                    0 -> PersonalTab(user, viewModel)
                    1 -> VehicleTab(user, viewModel)
                    2 -> EmergencyTab(user, viewModel)
                    3 -> MedicalTab(user, viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalTab(user: UserEntity, viewModel: DashcamViewModel) {
    var name by remember { mutableStateOf(user.name) }
    var dob by remember { mutableStateOf(user.dob) }
    var bloodGroup by remember { mutableStateOf(user.bloodGroup) }
    var phone by remember { mutableStateOf(user.phone) }
    val context = LocalContext.current

    val bloods = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Personal Specifications", color = AccentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Legal Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Date of Birth (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Personal Mobile Line") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            Text("Select Blood Group", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bloods.forEach { bg ->
                    val isSelected = bloodGroup == bg
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) AccentBlue else CardSurfaceElevated,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { bloodGroup = bg }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(text = bg, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updatePersonalProfile(name, dob, bloodGroup, phone)
                    Toast.makeText(context, "Personal profiles metrics updated!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Save Metrics", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VehicleTab(user: UserEntity, viewModel: DashcamViewModel) {
    var make by remember { mutableStateOf(user.vehicle.make) }
    var model by remember { mutableStateOf(user.vehicle.model) }
    var year by remember { mutableStateOf(user.vehicle.year) }
    var registrationNumber by remember { mutableStateOf(user.vehicle.registrationNumber) }
    var type by remember { mutableStateOf(user.vehicle.vehicleType) }
    var colorHex by remember { mutableStateOf(user.vehicle.colorHex) }
    val context = LocalContext.current

    val colorsGroup = listOf("#3B82F6", "#EF4444", "#22C55E", "#F59E0B", "#10B981", "#8B5CF6")
    val types = listOf("Car", "Truck", "Bus", "Auto")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Vehicle Specifications", color = AccentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        item {
            OutlinedTextField(
                value = make,
                onValueChange = { make = it },
                label = { Text("Vehicle Make (e.g. Tesla)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Vehicle Variant model") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year (YYYY)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardSurfaceElevated
                    )
                )
                OutlinedTextField(
                    value = registrationNumber,
                    onValueChange = { registrationNumber = it },
                    label = { Text("Plate number") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardSurfaceElevated
                    )
                )
            }
        }

        item {
            Text("Select Vehicle Type", color = TextSecondary, fontSize = 11.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { t ->
                    val isTSelected = type == t
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isTSelected) AccentBlue else CardSurfaceElevated,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { type = t }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = t, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Vehicle Accent Color Preset", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorsGroup.forEach { hex ->
                    val isColSelected = colorHex == hex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(hex)))
                            .border(
                                3.dp,
                                if (isColSelected) TextPrimary else Color.Transparent,
                                CircleShape
                            )
                            .clickable { colorHex = hex }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateVehicleProfile(
                        VehicleInfo(make, model, year, registrationNumber, colorHex, type)
                    )
                    Toast.makeText(context, "Vehicle attributes updated!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Save Vehicle specifications", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmergencyTab(user: UserEntity, viewModel: DashcamViewModel) {
    var cName by remember { mutableStateOf("") }
    var cRelation by remember { mutableStateOf("") }
    var cPhone by remember { mutableStateOf("") }
    var cEmail by remember { mutableStateOf("") }
    var cPrimary by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("SOS Emergency Responders", color = AccentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        // Current list
        if (user.emergencyContacts.isEmpty()) {
            item {
                Text("No contacts designated as emergency targets. Add at least one setup.", color = TextSecondary, fontSize = 12.sp)
            }
        } else {
            items(user.emergencyContacts) { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurfaceElevated, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(contact.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (contact.isPrimary) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(SuccessGreen.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("PRIMARY", color = SuccessGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text("${contact.relationship} • Tel: ${contact.phone}", color = TextSecondary, fontSize = 11.sp)
                    }

                    Row {
                        IconButton(onClick = {
                            Toast.makeText(context, "TEST TELEMETRY SMS dispatched successfully to ${contact.name}!", Toast.LENGTH_LONG).show()
                        }) {
                            Icon(Icons.Default.Sensors, contentDescription = "Test SMS Alert", tint = SuccessGreen)
                        }
                        IconButton(onClick = { viewModel.deleteEmergencyContact(contact.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                        }
                    }
                }
            }
        }

        item {
            Divider(color = CardSurfaceElevated, modifier = Modifier.padding(vertical = 8.dp))
            Text("Add Designate SOS Respondent", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        item {
            OutlinedTextField(
                value = cName,
                onValueChange = { cName = it },
                label = { Text("Contact Full Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = cRelation,
                    onValueChange = { cRelation = it },
                    label = { Text("Relation (e.g. Spouse)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardSurfaceElevated
                    )
                )
                OutlinedTextField(
                    value = cPhone,
                    onValueChange = { cPhone = it },
                    label = { Text("Mobile phone line") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardSurfaceElevated
                    )
                )
            }
        }

        item {
            OutlinedTextField(
                value = cEmail,
                onValueChange = { cEmail = it },
                label = { Text("Email Line Contact (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = cPrimary, onCheckedChange = { cPrimary = it })
                Text("Designate as PRIMARY quick SOS contact", color = TextSecondary, fontSize = 12.sp)
            }
        }

        item {
            Button(
                onClick = {
                    if (cName.isEmpty() || cPhone.isEmpty()) {
                        Toast.makeText(context, "Contact name and numbers are mandatory", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.addEmergencyContact(EmergencyContact(name = cName, relationship = cRelation, phone = cPhone, email = cEmail, isPrimary = cPrimary))
                    // reset
                    cName = ""; cRelation = ""; cPhone = ""; cEmail = ""; cPrimary = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Register SOS Respondent Line", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MedicalTab(user: UserEntity, viewModel: DashcamViewModel) {
    var knownConditions by remember { mutableStateOf(user.medicalInfo.knownConditions) }
    var medications by remember { mutableStateOf(user.medicalInfo.medications) }
    var docName by remember { mutableStateOf(user.medicalInfo.doctorName) }
    var docContact by remember { mutableStateOf(user.medicalInfo.doctorContact) }
    val context = LocalContext.current

    val conditions = listOf("Epilepsy", "Diabetes", "Heart Condition", "Vision Issues", "None")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Medical Specifications Disclosure", color = AccentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Text("Select known medical conditions", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                conditions.forEach { cond ->
                    val isIncluded = knownConditions.contains(cond)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isIncluded) AccentBlue.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable {
                                knownConditions = if (isIncluded) {
                                    knownConditions.filter { it != cond }
                                } else {
                                    knownConditions + cond
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isIncluded, onCheckedChange = {
                            knownConditions = if (isIncluded) {
                                knownConditions.filter { it != cond }
                            } else {
                                knownConditions + cond
                            }
                        })
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = cond, color = TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = medications,
                onValueChange = { medications = it },
                label = { Text("Daily/Regular Medications descriptions") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            OutlinedTextField(
                value = docName,
                onValueChange = { docName = it },
                label = { Text("Doctor Name Designation (optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            OutlinedTextField(
                value = docContact,
                onValueChange = { docContact = it },
                label = { Text("Doctor Phone Line details") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = CardSurfaceElevated
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateMedicalProfile(
                        MedicalInfo(knownConditions, medications, docName, docContact)
                    )
                    Toast.makeText(context, "Medical conditions updated!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Save Medical logs", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
