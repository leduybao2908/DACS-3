package com.example.dacs3.ui.screens.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.dacs3.ui.viewmodels.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    onNavigateBack: () -> Unit,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser = (authState as? AuthState.Success)?.user
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBase64 by remember { mutableStateOf<String?>(currentUser?.profilePicture) }
    var username by remember { mutableStateOf(currentUser?.username ?: "") }
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var age by remember { mutableStateOf(currentUser?.age ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var interests by remember { mutableStateOf(currentUser?.interests ?: "") }
    var city by remember { mutableStateOf(currentUser?.city ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { 
            selectedImageUri = it
            // Convert Uri to Base64
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                inputStream?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                    val imageBytes = baos.toByteArray()
                    selectedImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                }
            } catch (e: Exception) {
                errorMessage = "Failed to process image: ${e.message}"
                showErrorDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cập nhật thông tin") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        val context = LocalContext.current
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                imageLoader = coil.ImageLoader(context)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Add Profile Picture",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Picture",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(4.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { newValue -> name = newValue },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = username,
                    onValueChange = { newValue -> username = newValue },
                    label = { Text("Tên đăng nhập") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = age,
                    onValueChange = { newValue -> age = newValue },
                    label = { Text("Tuổi") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { newValue -> phone = newValue },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = interests,
                    onValueChange = { newValue -> interests = newValue },
                    label = { Text("Sở thích") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = city,
                    onValueChange = { newValue -> city = newValue },
                    label = { Text("Thành phố") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.LocationCity, contentDescription = null)
                    }
                )
            }

            item {
                Button(
                    onClick = {
                        isLoading = true
                        val updatedFields = mutableMapOf<String, String>()
                        if (username != currentUser?.username) updatedFields["username"] = username
                        if (name != currentUser?.name) updatedFields["name"] = name
                        if (age != currentUser?.age) updatedFields["age"] = age
                        if (phone != currentUser?.phone) updatedFields["phone"] = phone
                        if (interests != currentUser?.interests) updatedFields["interests"] = interests
                        if (city != currentUser?.city) updatedFields["city"] = city
                        if (selectedImageBase64 != currentUser?.profilePicture) updatedFields["profilePictureBase64"] = selectedImageBase64 ?: ""
                        
                        authViewModel.updateUserProfile(
                            username = updatedFields["username"] ?: currentUser?.username ?: "",
                            age = updatedFields["age"] ?: currentUser?.age ?: "",
                            phone = updatedFields["phone"] ?: currentUser?.phone ?: "",
                            interests = updatedFields["interests"] ?: currentUser?.interests ?: "",
                            city = updatedFields["city"] ?: currentUser?.city ?: "",
                            profilePictureBase64 = updatedFields["profilePictureBase64"] ?: currentUser?.profilePicture ?: ""
                        )
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Cập nhật")
                }
            }
        }
    }
}
