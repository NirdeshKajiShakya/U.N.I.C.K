package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.unick.model.SchoolForm
import com.example.unick.viewmodel.CompareSchoolState
import com.example.unick.viewmodel.CompareSchoolViewModel

// Custom Colors
private val PrimaryBlue = Color(0xFF1565C0)
private val LightBlue = Color(0xFF42A5F5)
private val AccentOrange = Color(0xFFFF7043)
private val BackgroundGray = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val SuccessGreen = Color(0xFF10B981)
private val DividerColor = Color(0xFFE2E8F0)


class CompareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = PrimaryBlue,
                    secondary = LightBlue,
                    tertiary = AccentOrange,
                    background = BackgroundGray,
                    surface = CardWhite,
                    onPrimary = Color.White,
                    onBackground = TextPrimary,
                    onSurface = TextPrimary
                )
            ) {
                SchoolCompareScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolCompareScreen(
    viewModel: CompareSchoolViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    // Collect state from ViewModel
    val schoolsState by viewModel.schoolsState.collectAsState()
    val school1 by viewModel.school1.collectAsState()
    val school2 by viewModel.school2.collectAsState()

    // Dialog State
    var showDialog by remember { mutableStateOf(false) }
    var activeSlot by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PrimaryBlue, LightBlue)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        Icons.AutoMirrored.Filled.CompareArrows,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Compare Schools",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
                }
            }
        },
        containerColor = BackgroundGray
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header instruction
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = LightBlue.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.School,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Select two schools to compare their features side by side",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Selection Area
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Slot 1
                    SelectionCard(
                        modifier = Modifier.weight(1f),
                        school = school1,
                        label = "School A",
                        cardColor = PrimaryBlue,
                        onAddClick = { activeSlot = 1; showDialog = true },
                        onRemoveClick = { viewModel.clearSchool1() }
                    )

                    // VS Badge
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .background(AccentOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "VS",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    // Slot 2
                    SelectionCard(
                        modifier = Modifier.weight(1f),
                        school = school2,
                        label = "School B",
                        cardColor = LightBlue,
                        onAddClick = { activeSlot = 2; showDialog = true },
                        onRemoveClick = { viewModel.clearSchool2() }
                    )
                }
            }

            // Comparison Table
            if (school1 != null && school2 != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Comparison Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                PrimaryBlue.copy(alpha = 0.1f),
                                                LightBlue.copy(alpha = 0.1f)
                                            )
                                        )
                                    )
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Feature",
                                    modifier = Modifier.weight(0.9f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    school1?.schoolName?.take(12) ?: "-",
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = PrimaryBlue
                                )
                                Text(
                                    school2?.schoolName?.take(12) ?: "-",
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = LightBlue
                                )
                            }

                            HorizontalDivider(color = DividerColor)

                            // Data Rows
                            val fields = getComparisonFields()
                            fields.forEachIndexed { index, field ->
                                ComparisonRowItem(
                                    field = field,
                                    s1 = school1,
                                    s2 = school2,
                                    isOdd = index % 2 != 0
                                )
                                if (index < fields.size - 1) {
                                    HorizontalDivider(
                                        color = DividerColor.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (school1 != null || school2 != null) {
                // One school selected
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = AccentOrange.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Select one more school to compare",
                                color = AccentOrange,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                // Empty State
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        LightBlue.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.CompareArrows,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                "No Schools Selected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tap the cards above to add schools\nand start comparing",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    // Selection Dialog
    if (showDialog) {
        SchoolSelectionDialog(
            schoolsState = schoolsState,
            onDismiss = { showDialog = false },
            onSchoolSelected = { selected ->
                if (activeSlot == 1) {
                    viewModel.selectSchool1(selected)
                } else {
                    viewModel.selectSchool2(selected)
                }
                showDialog = false
            },
            onRetry = { viewModel.fetchAllSchools() }
        )
    }
}

// --- UI COMPONENTS ---

@Composable
fun SelectionCard(
    modifier: Modifier = Modifier,
    school: SchoolForm?,
    label: String,
    cardColor: Color,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { if (school == null) onAddClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (school != null) CardWhite else CardWhite
        ),
        elevation = CardDefaults.cardElevation(if (school != null) 6.dp else 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (school != null) {
                // Color accent bar at top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(cardColor, cardColor.copy(alpha = 0.6f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // School Image or Placeholder
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(cardColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!school.imageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = school.imageUrl,
                                contentDescription = school.schoolName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                school.schoolName.take(1).uppercase(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = cardColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        school.schoolName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            school.location.take(20),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Close button
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Remove",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                // Empty state with dashed border effect
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 2.dp,
                            color = DividerColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(cardColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = null,
                            tint = cardColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Add $label",
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        "Tap to select",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonRowItem(field: CompField, s1: SchoolForm?, s2: SchoolForm?, isOdd: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isOdd) BackgroundGray.copy(alpha = 0.5f) else CardWhite)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label Column with icon
        Row(
            modifier = Modifier.weight(0.9f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (field.highlight) PrimaryBlue.copy(alpha = 0.1f)
                        else DividerColor.copy(alpha = 0.5f),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    field.icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = if (field.highlight) PrimaryBlue else TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                field.label,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        // Value 1
        Text(
            text = if (s1 != null) field.getter(s1) else "-",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = if (field.highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (field.highlight) PrimaryBlue else TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Value 2
        Text(
            text = if (s2 != null) field.getter(s2) else "-",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = if (field.highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (field.highlight) LightBlue else TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SchoolSelectionDialog(
    schoolsState: CompareSchoolState,
    onDismiss: () -> Unit,
    onSchoolSelected: (SchoolForm) -> Unit,
    onRetry: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 520.dp)
        ) {
            Column {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryBlue, LightBlue)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Select a School",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    when (schoolsState) {
                        is CompareSchoolState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = PrimaryBlue)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Loading schools...",
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                        is CompareSchoolState.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color.Red.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color.Red,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Failed to load schools",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    schoolsState.message,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onRetry,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryBlue
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retry")
                                }
                            }
                        }
                        is CompareSchoolState.Success -> {
                            val schools = schoolsState.schools
                            if (schools.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Outlined.School,
                                            contentDescription = null,
                                            tint = DividerColor,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            "No schools available",
                                            color = TextSecondary
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.heightIn(max = 320.dp)
                                ) {
                                    items(schools) { school ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onSchoolSelected(school) },
                                            colors = CardDefaults.cardColors(
                                                containerColor = BackgroundGray
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // School avatar
                                                Box(
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .shadow(2.dp, CircleShape)
                                                        .clip(CircleShape)
                                                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (!school.imageUrl.isNullOrEmpty()) {
                                                        AsyncImage(
                                                            model = school.imageUrl,
                                                            contentDescription = school.schoolName,
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    } else {
                                                        Text(
                                                            school.schoolName.take(1).uppercase(),
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = PrimaryBlue
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(14.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        school.schoolName,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = TextPrimary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            Icons.Default.LocationOn,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(12.dp),
                                                            tint = TextSecondary
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            school.location,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = TextSecondary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                                Icon(
                                                    Icons.Default.ChevronRight,
                                                    contentDescription = null,
                                                    tint = DividerColor,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            // Idle state
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryBlue)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// --- DATA & HELPERS ---

data class CompField(
    val label: String,
    val icon: ImageVector,
    val getter: (SchoolForm) -> String,
    val highlight: Boolean = false
)

fun getComparisonFields() = listOf(
    CompField("Tuition Fee", Icons.Default.Info, { it.tuitionFee.ifEmpty { "-" } }, true),
    CompField("Curriculum", Icons.Default.Edit, { it.curriculum.ifEmpty { "-" } }, true),
    CompField("Location", Icons.Default.Place, { it.location.ifEmpty { "-" } }),
    CompField("Established", Icons.Default.DateRange, { it.establishedYear.ifEmpty { "-" } }),
    CompField("Total Students", Icons.Default.Person, { it.totalStudents.ifEmpty { "-" } }),
    CompField("Programs", Icons.Default.List, { it.programsOffered.ifEmpty { "-" } }),
    CompField("Facilities", Icons.Default.Star, { it.facilities.ifEmpty { "-" } }),
    CompField("Activities", Icons.Default.Favorite, { it.extracurricular.ifEmpty { "-" } }),
    CompField("Transport", Icons.Default.Check, { if (it.transportFacility) "Yes" else "No" }),
    CompField("Hostel", Icons.Default.Home, { if (it.hostelFacility) "Yes" else "No" }),
    CompField("Scholarship", Icons.Default.Star, { if (it.scholarshipAvailable) "Yes" else "No" })
)

@Preview
@Composable
fun SchoolComparePreview() {
    SchoolCompareScreen()
}