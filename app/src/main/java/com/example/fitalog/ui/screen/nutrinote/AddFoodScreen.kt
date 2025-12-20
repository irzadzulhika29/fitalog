package com.example.fitalog.ui.screen.nutrinote


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fitalog.ui.theme.*

@Composable
fun AddFoodScreen(
    initialMeal: String,
    viewModel: NutriViewModel,
    onFoodAdded: () -> Unit,
    onBack: () -> Unit
) {
    var selectedMeal by remember { mutableStateOf(initialMeal) }
    var expanded by remember { mutableStateOf(false) }
    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }
    var servingCount by remember { mutableStateOf(1) }
    var query by remember { mutableStateOf("") }

    var showInputForm by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var inputCalories by remember { mutableStateOf("") }
    var inputServingText by remember { mutableStateOf("") }
    var inputImageUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val searchResult by viewModel.uiFoodList.collectAsState()
    val allFoods by viewModel.allFoods.collectAsState()

    val displayedFoods = if (query.isBlank()) allFoods else searchResult

    LaunchedEffect(Unit) {
        viewModel.fetchFoodsFromApi()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BG_MAIN)
            .statusBarsPadding() // 🔑 INI KUNCI
    )
    {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = WHITE,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
            )

            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Text(
                        selectedMeal,
                        color = WHITE,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = WHITE
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Sarapan", "Makan Siang", "Makan Malam", "Kudapan").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                selectedMeal = it
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.updateSearchQuery(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Cari makanan", color = WHITE.copy(.5f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ACCENT,
                unfocusedBorderColor = WHITE.copy(.3f),
                cursorColor = ACCENT,
                focusedTextColor = WHITE,
                unfocusedTextColor = WHITE
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showInputForm = !showInputForm },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showInputForm) CARD_BG else ACCENT
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                if (showInputForm) Icons.Default.Close else Icons.Default.Add,
                contentDescription = null,
                tint = if (showInputForm) WHITE else CARD_BG
            )
            Spacer(Modifier.width(8.dp))
            Text(
                if (showInputForm) "Tutup Form" else "Tambah Makanan Baru",
                color = if (showInputForm) WHITE else CARD_BG
            )
        }

        AnimatedVisibility(visible = showInputForm) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = CARD_BG),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Input Makanan Baru",
                        color = WHITE,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    // Nama Makanan
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nama Makanan", color = WHITE.copy(.7f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ACCENT,
                            unfocusedBorderColor = WHITE.copy(.3f),
                            cursorColor = ACCENT,
                            focusedTextColor = WHITE,
                            unfocusedTextColor = WHITE
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Kalori
                    OutlinedTextField(
                        value = inputCalories,
                        onValueChange = { inputCalories = it.filter { c -> c.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Kalori", color = WHITE.copy(.7f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ACCENT,
                            unfocusedBorderColor = WHITE.copy(.3f),
                            cursorColor = ACCENT,
                            focusedTextColor = WHITE,
                            unfocusedTextColor = WHITE
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Serving Text
                    OutlinedTextField(
                        value = inputServingText,
                        onValueChange = { inputServingText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Porsi (contoh: 1 porsi, 100g)", color = WHITE.copy(.7f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ACCENT,
                            unfocusedBorderColor = WHITE.copy(.3f),
                            cursorColor = ACCENT,
                            focusedTextColor = WHITE,
                            unfocusedTextColor = WHITE
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Image URL
                    OutlinedTextField(
                        value = inputImageUrl,
                        onValueChange = { inputImageUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("URL Gambar", color = WHITE.copy(.7f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ACCENT,
                            unfocusedBorderColor = WHITE.copy(.3f),
                            cursorColor = ACCENT,
                            focusedTextColor = WHITE,
                            unfocusedTextColor = WHITE
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Error message
                    errorMessage?.let {
                        Text(
                            it,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Submit button
                    Button(
                        onClick = {
                            // Validation
                            when {
                                inputName.isBlank() -> errorMessage = "Nama makanan harus diisi"
                                inputCalories.isBlank() -> errorMessage = "Kalori harus diisi"
                                inputServingText.isBlank() -> errorMessage = "Porsi harus diisi"
                                inputImageUrl.isBlank() -> errorMessage = "URL gambar harus diisi"
                                else -> {
                                    errorMessage = null
                                    isLoading = true
                                    viewModel.insertFoodToApi(
                                        name = inputName,
                                        calories = inputCalories.toIntOrNull() ?: 0,
                                        servingText = inputServingText,
                                        imageUrl = inputImageUrl,
                                        onSuccess = {
                                            isLoading = false
                                            // Clear form
                                            inputName = ""
                                            inputCalories = ""
                                            inputServingText = ""
                                            inputImageUrl = ""
                                            showInputForm = false
                                        },
                                        onError = { error ->
                                            isLoading = false
                                            errorMessage = error
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ACCENT),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = CARD_BG,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Simpan Makanan", color = CARD_BG)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            if (query.isBlank()) "Riwayat Makanan" else "Hasil Pencarian",
            color = WHITE,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(displayedFoods) { food ->
                FoodRow(
                    food = food,
                    selected = selectedFood?.id == food.id,
                    onSelect = { selectedFood = food }
                )
            }
        }

        AnimatedVisibility(visible = selectedFood != null) {
            selectedFood?.let { food ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = CARD_BG),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 24.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                food.name,
                                color = WHITE,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                "${food.calories * servingCount} kal",
                                color = ACCENT,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                if (servingCount > 1) "$servingCount x ${food.servingText}" else food.servingText,
                                color = WHITE.copy(0.65f),
                                fontSize = 14.sp
                            )
                        }

                        // Serving Count Selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            IconButton(
                                onClick = { if (servingCount > 1) servingCount-- },
                                enabled = servingCount > 1
                            ) {
                                Icon(Icons.Default.Remove, null, tint = if (servingCount > 1) ACCENT else WHITE.copy(.3f))
                            }

                            Text(
                                "$servingCount",
                                color = WHITE,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            IconButton(
                                onClick = { if (servingCount < 10) servingCount++ },
                                enabled = servingCount < 10
                            ) {
                                Icon(Icons.Default.Add, null, tint = if (servingCount < 10) ACCENT else WHITE.copy(.3f))
                            }
                        }

                        Button(
                            onClick = {
                                val mealType = try {
                                    MealType.valueOf(
                                        selectedMeal.uppercase().replace(" ", "_")
                                    )
                                } catch (_: IllegalArgumentException) {
                                    MealType.SARAPAN
                                }
                                viewModel.addFoodToCurrentDay(
                                    meal = mealType,
                                    food = food,
                                    servingCount = servingCount,
                                    onSuccess = {
                                        servingCount = 1 // Reset serving count
                                        onFoodAdded()
                                    },
                                    onError = { error ->
                                        // Could show toast or snackbar here
                                        println("Error adding food: $error")
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ACCENT)
                        ) {
                            Text("Tambah", color = CARD_BG, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun FoodRow(
    food: FoodItem,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = CARD_BG
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 18.dp), // 🔑 lebih tinggi
            verticalAlignment = Alignment.CenterVertically
        )
        {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    food.name,
                    color = WHITE,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "${food.calories} kal, ${food.servingText}",
                    fontSize = 15.sp,
                    color = WHITE.copy(0.7f)
                )
            }

            Icon(
                imageVector = if (selected) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                tint = if (selected) ACCENT else WHITE
            )
        }
    }
}