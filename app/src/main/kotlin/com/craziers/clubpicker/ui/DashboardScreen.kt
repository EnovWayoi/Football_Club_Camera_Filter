package com.craziers.clubpicker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.craziers.clubpicker.data.Club
import com.craziers.clubpicker.viewmodel.ClubPickerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ClubPickerViewModel,
    onBack: () -> Unit
) {
    val filteredClubs by viewModel.filteredClubs.collectAsState()
    val selectedNations by viewModel.selectedNations.collectAsState()
    val selectedLeagues by viewModel.selectedLeagues.collectAsState()
    val selectedStars by viewModel.selectedStars.collectAsState()
    val availableLeagues by viewModel.availableLeagues.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Club Filters (${filteredClubs.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            
            // Filter section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text("Filters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                
                FilterRow(
                    "Stars", 
                    viewModel.availableStars.map { if (it % 1.0f == 0.0f) it.toInt().toString() else it.toString() }, 
                    selectedStars.map { if (it % 1.0f == 0.0f) it.toInt().toString() else it.toString() }.toSet()
                ) { viewModel.toggleStar(it.toFloat()) }
                FilterRow("Nation", viewModel.availableNations, selectedNations) { viewModel.toggleNation(it) }
                FilterRow("League", availableLeagues, selectedLeagues) { viewModel.toggleLeague(it) }
            }

            // Club List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredClubs, key = { it.id }) { club ->
                    ClubListItem(club)
                }
            }
        }
    }
}

@Composable
fun FilterRow(
    title: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(title, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(options) { option ->
                val isSelected = selectedOptions.contains(option)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable { onToggle(option) },
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ClubListItem(club: Club) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(club.primaryColor.toColorInt())),
                contentAlignment = Alignment.Center
            ) {
                if (club.badgeUrl.isNotEmpty() && club.badgeUrl != "Unknown") {
                    AsyncImage(
                        model = club.badgeUrl,
                        contentDescription = "${club.name} Badge",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = club.name.take(1),
                        color = Color(club.secondaryColor.toColorInt()),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = club.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val starDisplay = if (club.starRating % 1.0f == 0.0f) club.starRating.toInt().toString() else club.starRating.toString()
                Text(
                    text = "${club.nation} • ${club.league} • $starDisplay Stars",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
