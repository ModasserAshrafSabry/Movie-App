package com.example.movieapplication.ui.Signin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieapp.ui.theme.MovieAppTheme
import com.example.movieapplication.ui.Login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
class SigninActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private fun hasUppercase(password: String): Boolean {
        return password.any { it.isUpperCase() }
    }

    private fun hasLowercase(password: String): Boolean {
        return password.any { it.isLowerCase() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setContent {
            MovieAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0)
                ) {
                    SigninScreen(
                        onSignUpClick = { username, email, password, confirmPassword ->
                            // Basic validation
                            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                return@SigninScreen
                            }

                            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                                return@SigninScreen
                            }

                            if (password != confirmPassword) {
                                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                                return@SigninScreen
                            }

                            if (!hasUppercase(password)) {
                                Toast.makeText(this, "Password must contain at least 1 uppercase letter", Toast.LENGTH_SHORT).show()
                                return@SigninScreen
                            }

                            if (!hasLowercase(password)) {
                                Toast.makeText(this, "Password must contain at least 1 lowercase letter", Toast.LENGTH_SHORT).show()
                                return@SigninScreen
                            }

                            // Firebase create user
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser

                                        // Save username to Firestore
                                        saveUserToFirestore(user?.uid, username, email)

                                        user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                            if (verifyTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Account created! Please check your email to verify your account.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                auth.signOut()
                                                val intent = Intent(this, LoginActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Couldn't send verification email: ${verifyTask.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Error: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        },
                        onLoginClick = {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }

    //Save user data to Firestore
    private fun saveUserToFirestore(userId: String?, username: String, email: String) {
        if (userId == null) return

        val userData = hashMapOf(
            "username" to username,
            "email" to email,
            "favoriteGenres" to emptyList<String>(),
            "favoriteCelebrities" to emptyList<Map<String, String>>(),
            "hasCompletedGenreSelection" to false,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                println("User data saved successfully to Firestore")
            }
            .addOnFailureListener { e ->
                println("Error saving user data: ${e.message}")
            }
    }
}

@Composable
fun SigninScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    val bgColor = Color(0xFF080808)
    val buttonColor = Color(0xFF92B300)
    val textColor = Color(0xFFF1F0F3)

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                buildAnnotatedString {
                    append("Sign ")
                    withStyle(style = SpanStyle(color = buttonColor)) {
                        append("Up")
                    }
                },
                style = TextStyle(
                    color = textColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.testTag("signup_title")
            )

            Spacer(modifier = Modifier.height(40.dp))

            val fieldModifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.Transparent, RoundedCornerShape(10.dp))

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttonColor,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = buttonColor
            )

            // Username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = textColor.copy(alpha = 0.8f)) },
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = fieldModifier
                    .testTag("username_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = textColor.copy(alpha = 0.8f)) },
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = fieldModifier
                    .testTag("email_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = textColor.copy(alpha = 0.8f)) },
                singleLine = true,
                colors = fieldColors,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = null, tint = textColor)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(10.dp),
                modifier = fieldModifier
                    .testTag("password_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = textColor.copy(alpha = 0.8f)) },
                singleLine = true,
                colors = fieldColors,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = null, tint = textColor)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(10.dp),
                modifier = fieldModifier
                    .testTag("confirm_password_field")
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Sign Up Button
            Button(
                onClick = { onSignUpClick(username, email, password, confirmPassword) },
                modifier = Modifier
                    .testTag("sign_up_button")
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Sign Up",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login link
            Text(
                buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(style = SpanStyle(color = buttonColor, textDecoration = TextDecoration.Underline)) {
                        append("Login")
                    }
                },
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier.clickable(onClick = onLoginClick).testTag("login_text")
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SigninScreenPreview() {
    MovieAppTheme {
        SigninScreen()
    }
}