package com.example.fitalog.ui.screen.sleepsync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitalog.ui.theme.AccentGreen
import com.example.fitalog.ui.theme.CardBackground
import com.example.fitalog.ui.theme.DarkBackground
import com.example.fitalog.ui.theme.TextSecondary

// Card dasar untuk konten Sleepsync
@Composable
fun SleepCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        content = content
    )
}

// Item metrik tunggal untuk card durasi/waktu
@Composable
fun MetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ikon
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(4.dp))
        // Nilai
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

// Tombol hijau utama di bagian bawah screen
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
    ) {
        Text(
            text = text,
            color = DarkBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

// Header dasar dengan judul "Sleepsync"
@Composable
fun AppHeader(title: String = "Sleepsync") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            tint = TextSecondary
        )
    }
    Spacer(Modifier.height(16.dp))
}

// Spacer kustom untuk pembatas
@Composable
fun CustomDivider(color: Color, thickness: Dp, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}