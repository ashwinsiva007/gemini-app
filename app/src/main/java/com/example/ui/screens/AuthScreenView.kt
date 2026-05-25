package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthScreen
import com.example.ui.viewmodel.DashcamViewModel

@Composable
fun AuthScreenView(viewModel: DashcamViewModel) {
    val authScreen by viewModel.authScreen.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsState()

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 768

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        // Glowing background accents
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentBlue.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(DangerRed.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        if (isCompact) {
            // Elegant Mobile Layout: single-column vertical flow with custom header and scrolling
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Dashcam Pro Logo",
                    tint = AccentBlue,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "DASHCAM PRO",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Drive Safe. Stay Alert.",
                    fontSize = 14.sp,
                    color = AccentNeonBlue,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardSurfaceElevated, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(
                            targetState = authScreen,
                            transitionSpec = {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            }, label = "auth_form"
                        ) { screen ->
                            when (screen) {
                                AuthScreen.SignIn -> SignInForm(viewModel)
                                AuthScreen.SignUp -> SignUpForm(viewModel)
                                AuthScreen.ForgotPassword -> ForgotPasswordForm(viewModel)
                                AuthScreen.OTPVerification -> OtpVerificationForm(viewModel)
                            }
                        }

                        // Success / Error Popups
                        if (authError.isNotEmpty()) {
                            Text(
                                text = authError,
                                color = DangerRed,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .testTag("auth_error_message")
                            )
                        }
                        if (authSuccessMessage.isNotEmpty()) {
                            Text(
                                text = authSuccessMessage,
                                color = SuccessGreen,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .testTag("auth_success_message")
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        } else {
            // Elegant Widescreen Desktop / Tablet Layout: Dual side-by-side columns
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // LEFT COLUMN - Brand Visuals
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .background(CardSurface)
                        .padding(40.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Dashcam Pro Logo",
                        tint = AccentBlue,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "DASHCAM PRO",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Drive Safe. Stay Alert.",
                        fontSize = 18.sp,
                        color = AccentNeonBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Tech badges
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeatureBadge(icon = Icons.Default.Face, title = "AI FaceMesh Drowsiness Detection")
                        FeatureBadge(icon = Icons.Default.Sensors, title = "Impact Sensor Detection (G-Force)")
                        FeatureBadge(icon = Icons.Default.Map, title = "Live GPS Route Logs & Tracking")
                        FeatureBadge(icon = Icons.Default.Sos, title = "Auto SOS Countdown SMS & Emails")
                    }
                }

                // RIGHT COLUMN - User Authentication Forms
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .border(1.dp, CardSurfaceElevated, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedContent(
                                targetState = authScreen,
                                transitionSpec = {
                                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                                            slideOutHorizontally { width -> -width } + fadeOut()
                                }, label = "auth_form"
                            ) { screen ->
                                when (screen) {
                                    AuthScreen.SignIn -> SignInForm(viewModel)
                                    AuthScreen.SignUp -> SignUpForm(viewModel)
                                    AuthScreen.ForgotPassword -> ForgotPasswordForm(viewModel)
                                    AuthScreen.OTPVerification -> OtpVerificationForm(viewModel)
                                }
                            }

                            // Success / Error Popups
                            if (authError.isNotEmpty()) {
                                Text(
                                    text = authError,
                                    color = DangerRed,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .testTag("auth_error_message")
                                )
                            }
                            if (authSuccessMessage.isNotEmpty()) {
                                Text(
                                    text = authSuccessMessage,
                                    color = SuccessGreen,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .testTag("auth_success_message")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .background(CardSurfaceElevated, RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun SignInForm(viewModel: DashcamViewModel) {
    val email by viewModel.loginEmail.collectAsState()
    val password by viewModel.loginPassword.collectAsState()
    val rememberMe by viewModel.loginRememberMe.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Welcome Back",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Sign in to access your dashboard telemetry",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.loginEmail.value = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.loginPassword.value = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, "Password") },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { viewModel.loginRememberMe.value = it }
                )
                Text("Remember me", color = TextSecondary, fontSize = 12.sp)
            }
            Text(
                text = "Forgot Password?",
                color = AccentNeonBlue,
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { viewModel.setAuthScreen(AuthScreen.ForgotPassword) }
                    .padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.handleSignIn() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("login_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Sign In Now", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account? ", color = TextSecondary, fontSize = 12.sp)
            Text(
                text = "Register",
                color = AccentNeonBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { viewModel.setAuthScreen(AuthScreen.SignUp) }
            )
        }
    }
}

@Composable
fun SignUpForm(viewModel: DashcamViewModel) {
    val name by viewModel.registerName.collectAsState()
    val email by viewModel.registerEmail.collectAsState()
    val phone by viewModel.registerPhone.collectAsState()
    val password by viewModel.registerPassword.collectAsState()
    val confirm by viewModel.registerConfirmPassword.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Dynamic strength bar
    val strength = when {
        password.isEmpty() -> 0f
        password.length < 5 -> 0.3f
        password.any { it.isDigit() } && password.any { it.isUpperCase() } -> 1f
        else -> 0.6f
    }
    val strengthColor = when (strength) {
        0.3f -> DangerRed
        0.6f -> WarningAmber
        else -> SuccessGreen
    }
    val strengthText = when (strength) {
        0.3f -> "Weak"
        0.6f -> "Medium"
        1f -> "Strong"
        else -> "Empty"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Create Pro Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Register and configure your personal HUD",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.registerName.value = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, "Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.registerEmail.value = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, "Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { viewModel.registerPhone.value = it },
            label = { Text("Emergency Mobile") },
            leadingIcon = { Icon(Icons.Default.Phone, "Phone") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.registerPassword.value = it },
            label = { Text("Create Master Password") },
            leadingIcon = { Icon(Icons.Default.Lock, "Password") },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )
        
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { strength },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = strengthColor,
                    trackColor = CardSurfaceElevated,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Strength: $strengthText",
                    fontSize = 11.sp,
                    color = strengthColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { viewModel.registerConfirmPassword.value = it },
            label = { Text("Confirm Master Password") },
            leadingIcon = { Icon(Icons.Default.Lock, "Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.handleSignUp() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Register Account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already registered? ", color = TextSecondary, fontSize = 12.sp)
            Text(
                text = "Sign In",
                color = AccentNeonBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { viewModel.setAuthScreen(AuthScreen.SignIn) }
            )
        }
    }
}

@Composable
fun ForgotPasswordForm(viewModel: DashcamViewModel) {
    val email by viewModel.forgotEmail.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Reset Master Key",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Provide your email to receive a recovery token",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.forgotEmail.value = it },
            label = { Text("Recovery Email") },
            leadingIcon = { Icon(Icons.Default.Email, "Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.sendForgotPasswordOTP() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Send Recovery Token", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Back to Sign In",
            color = AccentNeonBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { viewModel.setAuthScreen(AuthScreen.SignIn) }
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
fun OtpVerificationForm(viewModel: DashcamViewModel) {
    val code by viewModel.otpCode.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Verify OTP Code",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Enter '1234' recovery code to bypass security check",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = code,
            onValueChange = { viewModel.otpCode.value = it },
            label = { Text("Verification OTP") },
            leadingIcon = { Icon(Icons.Default.LockClock, "OTP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
                .testTag("otp_code_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = CardSurfaceElevated
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.verifyOTP() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("otp_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Confirm OTP Bypass", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Resend Code",
            color = AccentNeonBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { viewModel.otpCode.value = "" }
                .padding(vertical = 4.dp)
        )
    }
}
