package com.example.fitalog.ui.screen.nutrinote

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fitalog.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NutriHomeScreen(
    viewModel: NutriViewModel,
    onAddClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayIndex by viewModel.dayIndex.collectAsState()
    val allDayMeals by viewModel.dayMeals.collectAsState()
    val isLoadingMeals by viewModel.isLoadingMeals.collectAsState()

    val meals = allDayMeals[dayIndex] ?: emptyMap()
    val total = viewModel.totalCaloriesForCurrentDay()

    var showSheet by rememberSaveable { mutableStateOf(false) }
    var sheetMeal by rememberSaveable { mutableStateOf<MealType?>(null) }
    var sheetFood by rememberSaveable { mutableStateOf<FoodItem?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BG_MAIN)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    TOP_ACCENT,
                    RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
        )

        if (showSheet && sheetMeal != null && sheetFood != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = CARD_BG
            ) {
                val food = sheetFood!!
                val meal = sheetMeal!!

                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(110.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(food.name, color = WHITE, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${food.calories} kal", color = ACCENT)
                    Text(food.servingText, color = WHITE.copy(0.7f))

                    Spacer(Modifier.height(20.dp))

                    Row(Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                food.historyId?.let {
                                    viewModel.removeFoodFromCurrentDay(meal, it)
                                }
                                showSheet = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            enabled = food.historyId != null
                        ) {
                            Text("Hapus", color = WHITE)
                        }

                        Spacer(Modifier.width(12.dp))

                        Button(
                            onClick = { showSheet = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = ACCENT)
                        ) {
                            Text("Tutup", color = CARD_BG)
                        }
                    }
                }
            }
        }

        val sectionSpacing = 10.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(10.dp))

            val rotation by animateFloatAsState(
                targetValue = if (isLoadingMeals) 360f else 0f,
                animationSpec = tween(1000),
                label = "refresh"
            )

            Box(Modifier.fillMaxWidth()) {
                Text(
                    "NutriNote",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = BG_MAIN
                )

                IconButton(
                    onClick = {
                        if (!isLoadingMeals) {
                            viewModel.resetCurrentDay()
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    enabled = !isLoadingMeals
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        null,
                        tint = BG_MAIN,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeDay(-1) }) {
                    Icon(Icons.Default.ChevronLeft, null, tint = BG_MAIN)
                }

                AnimatedContent(
                    targetState = dayIndex,
                    transitionSpec = {
                        slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight }
                        ) + fadeIn(
                            animationSpec = tween(200)
                        ) togetherWith
                                slideOutVertically(
                                    targetOffsetY = { fullHeight -> -fullHeight }
                                ) + fadeOut(
                            animationSpec = tween(200)
                        )
                    },
                    label = "day"
                ) { index ->
                    Text(
                        if (index == 1) "Hari Ini" else WEEK_DAYS[index],
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BG_MAIN
                    )
                }


                IconButton(onClick = { viewModel.changeDay(1) }) {
                    Icon(Icons.Default.ChevronRight, null, tint = BG_MAIN)
                }
            }

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-12).dp),
                colors = CardDefaults.cardColors(containerColor = CARD_BG),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(18.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, null, tint = ACCENT)
                        Spacer(Modifier.width(8.dp))
                        Text("Kalori Harian", color = WHITE)
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "$total Kal",
                        color = WHITE,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Target: ${viewModel.targetCalories} kal",
                        color = WHITE.copy(0.7f),
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    val progress =
                        (total.toFloat() / viewModel.targetCalories).coerceIn(0f, 1f)

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = ACCENT,
                        trackColor = WHITE.copy(0.25f)
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "0",
                            fontSize = 14.sp,
                            color = WHITE.copy(0.7f)
                        )
                        Text(
                            "${viewModel.targetCalories}",
                            fontSize = 14.sp,
                            color = WHITE.copy(0.7f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    val remaining = (viewModel.targetCalories - total).coerceAtLeast(0)

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sisa ",
                            color = WHITE.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )

                        Text(
                            text = "$remaining kal",
                            color = ACCENT,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            if (isLoadingMeals) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ACCENT)
                }
            } else {
                MealSectionLazyList(
                    meals = meals,
                    onAdd = { onAddClick(it) },
                    onFoodClick = { meal, food ->
                        sheetMeal = meal
                        sheetFood = food
                        showSheet = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MealSectionLazyList(
    meals: Map<MealType, List<FoodItem>>,
    onAdd: (String) -> Unit,
    onFoodClick: (MealType, FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val order = listOf(
        MealType.SARAPAN,
        MealType.MAKAN_SIANG,
        MealType.MAKAN_MALAM,
        MealType.KUDAPAN
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(order) { mealType ->
            val title = when (mealType) {
                MealType.SARAPAN -> "Sarapan"
                MealType.MAKAN_SIANG -> "Makan Siang"
                MealType.MAKAN_MALAM -> "Makan Malam"
                MealType.KUDAPAN -> "Kudapan"
            }

            val items = meals[mealType] ?: emptyList()
            val total = items.sumOf { it.calories }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CARD_BG),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(14.dp)) {

                    Row {
                        Text(
                            title,
                            color = WHITE,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            "$total",
                            color = WHITE,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items.forEach { food ->
                        Spacer(Modifier.height(8.dp))
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onFoodClick(mealType, food) }
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(food.name, color = WHITE)
                                Text("${food.calories}", color = WHITE)
                            }
                            Text(
                                "${food.calories} kal, ${food.servingText}",
                                color = WHITE.copy(0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onAdd(title) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = ACCENT)
                        ) {
                            Text("Tambah Makanan", color = CARD_BG)
                        }
                    }
                }
            }
        }
    }
}

