package com.example.fitalog.ui.screen.nutrinote

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitalog.ui.theme.*

@Composable
fun MealSectionList(
    meals: Map<MealType, List<FoodItem>>,
    onAdd: (String) -> Unit,
    onFoodClick: (MealType, FoodItem) -> Unit
) {
    val order = listOf(
        MealType.SARAPAN,
        MealType.MAKAN_SIANG,
        MealType.MAKAN_MALAM,
        MealType.KUDAPAN
    )

    Column {
        order.forEach { mealType ->

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
                    .padding(vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = CARD_BG),
                shape = RectangleShape
            ) {
                Column(Modifier.padding(12.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(title, color = WHITE, fontSize = 18.sp)
                        Spacer(Modifier.weight(1f))
                        Text("$total", color = WHITE)
                    }

                    Spacer(Modifier.height(8.dp))

                    items.forEach { food ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onFoodClick(mealType, food) }
                        ) {
                            Text(food.name, color = WHITE)
                            Text(
                                "${food.calories} kal • ${food.servingText}",
                                color = WHITE.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = { onAdd(title) },
                        colors = ButtonDefaults.buttonColors(containerColor = ACCENT)
                    ) {
                        Text("Tambah Makanan", color = CARD_BG)
                    }
                }
            }
        }
    }
}
