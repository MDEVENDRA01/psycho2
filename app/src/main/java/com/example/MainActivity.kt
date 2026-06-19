package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.LibraryData
import com.example.data.LibraryResource
import com.example.data.Reflection
import com.example.data.WellnessGoal
import com.example.ui.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val app = application as SerenityApplication
                val factory = SerenityViewModelFactory(app.repository)
                val viewModel: SerenityViewModel = viewModel(factory = factory)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SerenityAppMainView(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerenityAppMainView(viewModel: SerenityViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val scope = rememberCoroutineScope()
    
    // Dialog triggers
    var activeResourceForDetail by remember { mutableStateOf<LibraryResource?>(null) }
    var showSosDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clickable(
                                onClick = { viewModel.navigateTo(ScreenTab.SETTINGS) },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuB3iMcw6NGBgfL4lmHb9ecymkdswyzsBrW5k7IwQXVVVxemXGpERFPWBfi7KdNLhTTFi2RWC-5tN2wzxGN8vczn8M_Tw0T6ztDEZxNvBfo8VwD-VkaSQl_IIhfbwO0wOOCt8MdBN7WB2mmQ8XBR88JYmURb6tz4S1HPxsCL6T569g_IpfEa87mlFMd8zwMIV7xpxeJxfQIU-nkcRCG3urO2dOMtUNV6QafZ8ug7cyx41QVVEjvuFMmB",
                            contentDescription = "Profile photo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "SerenityPath",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "All is calm. You are completely up to date.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == ScreenTab.HOME,
                    onClick = { viewModel.navigateTo(ScreenTab.HOME) },
                    icon = { Icon(if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = currentTab == ScreenTab.INSIGHTS,
                    onClick = { viewModel.navigateTo(ScreenTab.INSIGHTS) },
                    icon = { Icon(if (currentTab == ScreenTab.INSIGHTS) Icons.Filled.Analytics else Icons.Outlined.Analytics, contentDescription = "Insights") },
                    label = { Text("Insights", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_insights")
                )
                NavigationBarItem(
                    selected = currentTab == ScreenTab.LIBRARY,
                    onClick = { viewModel.navigateTo(ScreenTab.LIBRARY) },
                    icon = { Icon(if (currentTab == ScreenTab.LIBRARY) Icons.Filled.MenuBook else Icons.Outlined.MenuBook, contentDescription = "Library") },
                    label = { Text("Library", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_library")
                )
                NavigationBarItem(
                    selected = currentTab == ScreenTab.SETTINGS || showSosDialog,
                    onClick = { showSosDialog = true },
                    icon = { Icon(Icons.Default.Emergency, contentDescription = "SOS", tint = SereneOnErrorContainer) },
                    label = { Text("SOS", fontSize = 11.sp, color = SereneOnErrorContainer) },
                    modifier = Modifier.testTag("nav_sos")
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(300),
                label = "TabTransition"
            ) { state ->
                when (state) {
                    ScreenTab.HOME -> HomeScreen(viewModel, onNavigateToInsights = {
                        viewModel.navigateTo(ScreenTab.INSIGHTS)
                    })
                    ScreenTab.INSIGHTS -> InsightsScreen(viewModel, onAddReflectionClick = {
                        viewModel.navigateTo(ScreenTab.HOME)
                    })
                    ScreenTab.LIBRARY -> LibraryScreen(
                        viewModel = viewModel,
                        onResourceClick = { activeResourceForDetail = it }
                    )
                    ScreenTab.SETTINGS -> SettingsScreen(viewModel, onSosClick = { showSosDialog = true })
                }
            }

            // --- Dialog Overlays ---
            activeResourceForDetail?.let { resource ->
                DetailResourceDialog(
                    resource = resource,
                    onDismiss = { activeResourceForDetail = null },
                    onComplete = {
                        viewModel.completePractice(resource.category, context)
                        activeResourceForDetail = null
                    }
                )
            }

            if (showSosDialog) {
                SosCalmingDialog(
                    onDismiss = { showSosDialog = false }
                )
            }
        }
    }
}

// ==========================================
// SCREEN 1: HOME TAB (DAILY MOOD CHECK-IN)
// ==========================================
@Composable
fun HomeScreen(
    viewModel: SerenityViewModel,
    onNavigateToInsights: () -> Unit
) {
    val selectedMood by viewModel.selectedMood.collectAsState()
    val journalText by viewModel.journalText.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val moodOptions = listOf(
        MoodData("GREAT", "Great", Icons.Default.SentimentVerySatisfied, Color(0xFF4A90E2)),
        MoodData("GOOD", "Good", Icons.Default.SentimentSatisfied, Color(0xFF2ECC71)),
        MoodData("OKAY", "Okay", Icons.Default.SentimentNeutral, Color(0xFFF5A623)),
        MoodData("ANXIOUS", "Anxious", Icons.Default.SentimentDissatisfied, Color(0xFF9B59B6)),
        MoodData("BAD", "Bad", Icons.Default.SentimentVeryDissatisfied, Color(0xFFE74C3C))
    )

    val preloadedTags = listOf("Work", "Family", "Health", "Sleep", "Social")
    var customTagText by remember { mutableStateOf("") }
    var showCustomTagInput by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome Header
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Hi Alex,",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "How are you feeling today?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Mood Grid selection
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Mood Balance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Render in a nice, rounded card layout
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 2 rows of moods or flow row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            moodOptions.take(3).forEach { mood ->
                                MoodSelectableButton(
                                    data = mood,
                                    isSelected = selectedMood == mood.id,
                                    onClick = { viewModel.setMood(mood.id) }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            moodOptions.drop(3).forEach { mood ->
                                MoodSelectableButton(
                                    data = mood,
                                    isSelected = selectedMood == mood.id,
                                    onClick = { viewModel.setMood(mood.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Journal and Custom tags block
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "What's on your mind?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Taking a moment to write can help process thoughts. This is a safe space.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    // Journal Text Field
                    OutlinedTextField(
                        value = journalText,
                        onValueChange = { viewModel.setJournalText(it) },
                        placeholder = { Text("I'm feeling...", color = MaterialTheme.colorScheme.outline) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("journal_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SereneSurfaceLow,
                            unfocusedContainerColor = SereneSurfaceLow,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        maxLines = 6
                    )

                    // Preloaded Tag Pills Flow
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Select Tags",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Tags Row
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            preloadedTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                Box(
                                    modifier = Modifier
                                        .clickable { viewModel.toggleTag(tag) }
                                        .background(
                                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else SereneBackground,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tag, 
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Dynamic "+ Add Tag" button
                            Box(
                                modifier = Modifier
                                    .clickable { showCustomTagInput = !showCustomTagInput }
                                    .background(
                                        color = SereneBackground,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Add Tag", 
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Sliding custom tag view
                        AnimatedVisibility(
                            visible = showCustomTagInput,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = customTagText,
                                    onValueChange = { customTagText = it },
                                    placeholder = { Text("Exercise, Class...", fontSize = 13.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        if (customTagText.isNotBlank()) {
                                            viewModel.addCustomTag(customTagText)
                                            customTagText = ""
                                            showCustomTagInput = false
                                        }
                                    })
                                )
                                Button(
                                    onClick = {
                                        if (customTagText.isNotBlank()) {
                                            viewModel.addCustomTag(customTagText)
                                            customTagText = ""
                                            showCustomTagInput = false
                                        }
                                    },
                                    modifier = Modifier.height(40.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    Text("Add", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Master Save Action Button
        item {
            Button(
                onClick = {
                    val entryNotes = journalText.trim()
                    viewModel.saveMoodEntry {
                        Toast.makeText(
                            context,
                            "Serene entry saved. Reflect well.",
                            Toast.LENGTH_SHORT
                        ).show()
                        onNavigateToInsights()
                    }
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
                    .testTag("save_entry_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Save Entry",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

data class MoodData(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun MoodSelectableButton(
    data: MoodData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1.0f, label = "MoodScale")
    val borderWith = if (isSelected) 2.dp else 1.dp
    val borderColor = if (isSelected) SereneSecondaryContainer else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    val backgroundColors = if (isSelected) SereneSurfaceLow else SereneSurfaceLowest

    Box(
        modifier = Modifier
            .size(width = 86.dp, height = 96.dp)
            .clickable(onClick = onClick)
            .border(borderWith, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColors, RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = data.title,
                tint = if (isSelected) SereneSecondary else data.color.copy(alpha = 0.8f),
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = data.title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (isSelected) SereneOnSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Simple FlowRow helper since Compose M3 flow is in separate library
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val xSpacing = mainAxisSpacing.roundToPx()
        val ySpacing = crossAxisSpacing.roundToPx()
        
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var rowWidth = 0
        var totalHeight = 0
        var maxRowHeight = 0

        val maxConstraintWidth = constraints.maxWidth

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
            if (rowWidth + placeable.width + xSpacing > maxConstraintWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                totalHeight += maxRowHeight + ySpacing
                currentRow = mutableListOf()
                rowWidth = 0
                maxRowHeight = 0
            }
            currentRow.add(placeable)
            rowWidth += placeable.width + xSpacing
            maxRowHeight = maxOf(maxRowHeight, placeable.height)
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            totalHeight += maxRowHeight
        }

        layout(constraints.maxWidth, maxOf(totalHeight, constraints.minHeight)) {
            var currentY = 0
            rows.forEach { row ->
                var currentX = 0
                var rowHeight = 0
                row.forEach { placeable ->
                    placeable.placeRelative(currentX, currentY)
                    currentX += placeable.width + xSpacing
                    rowHeight = maxOf(rowHeight, placeable.height)
                }
                currentY += rowHeight + ySpacing
            }
        }
    }
}


// ==========================================
// SCREEN 2: INSIGHTS TAB (GRAPH, GOALS, RECENT LIST)
// ==========================================
@Composable
fun InsightsScreen(
    viewModel: SerenityViewModel,
    onAddReflectionClick: () -> Unit
) {
    val reflections by viewModel.reflections.collectAsState()
    val goals by viewModel.goals.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcoming Headline
        item {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Your Insights",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "A gentle overview of your week. Take a moment to reflect on your journey without judgment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Mood Rhythm Chart Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mood Rhythm",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .background(SereneSurfaceLow, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "This Week",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Render beautiful custom bezier plot Canvas
                    MoodBezierChart(reflections = reflections)
                }
            }
        }

        // 2. Wellness Goals progress cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Wellness Goals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (goals.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        goals.forEach { goal ->
                            WellnessProgressRow(
                                goal = goal,
                                onIncrement = { viewModel.incrementGoal(goal.id) },
                                onDecrement = { viewModel.decrementGoal(goal.id) }
                            )
                        }
                    }
                }
            }
        }

        // 3. Recent Reflections list
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Recent Reflections",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Historical Notes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("all_history_label")
                )
            }
        }

        // Add pre-populated lists / newly added lists
        if (reflections.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clickable { onAddReflectionClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SereneSurfaceLow),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("No reflections yet. Add a new check-in.")
                        }
                    }
                }
            }
        } else {
            items(reflections) { reflection ->
                ReflectionItemCard(
                    reflection = reflection,
                    onDelete = { viewModel.deleteReflection(reflection.id) }
                )
            }

            // Always display a dashed Card at the very bottom: "Add New Reflection"
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(86.dp)
                        .clickable { onAddReflectionClick() }
                        .drawBehind {
                            val stroke = Stroke(
                                width = 2f,
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                    floatArrayOf(15f, 15f),
                                    0f
                                )
                            )
                            drawRoundRect(
                                color = Color(0xFFC1C7D3),
                                style = stroke,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                            )
                        }
                        .background(SereneSurfaceLow, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Add New Reflection",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// Bezier custom Canvas plotter
@Composable
fun MoodBezierChart(reflections: List<Reflection>) {
    // Standard Mon, Wed, Fri baseline points
    // Let's map real data index of latest 5 reflections if available, else static coordinates
    val values = remember(reflections) {
        if (reflections.size >= 2) {
            reflections.take(5).reversed().map { ref ->
                when (ref.mood) {
                    "GREAT" -> 2.3f
                    "GOOD" -> 1.8f
                    "OKAY" -> 1.2f
                    "ANXIOUS" -> 0.6f
                    "BAD" -> 0.1f
                    else -> 1.0f
                }
            }
        } else {
            // Smooth dummy curve matching screenshot (Low-Okay-Great waveform)
            listOf(1.0f, 1.1f, 1.3f, 1.8f, 2.3f)
        }
    }

    val daysText = remember(reflections) {
        if (reflections.size >= 2) {
            val sdf = SimpleDateFormat("EEE", Locale.getDefault())
            reflections.take(5).reversed().map { sdf.format(Date(it.timestamp)) }
        } else {
            listOf("Mon", "Wed", "Fri", "Sat", "Sun")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Y-axis labels
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Great", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Medium)
                Text("Okay", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Medium)
                Text("Low", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Graph Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Drawing background gridlines and bezier plot
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("chart_canvas")
                ) {
                    val width = size.width
                    val height = size.height

                    // 1. Draw horizontal grid lines
                    val linesCount = 3
                    val stepY = height / (linesCount - 1)
                    for (i in 0 until linesCount) {
                        val y = stepY * i
                        drawLine(
                            color = Color(0xFFC1C7D3).copy(alpha = 0.3f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // 2. Draw Bezier line
                    if (values.isNotEmpty()) {
                        val path = Path()
                        val points = mutableListOf<Offset>()
                        val spacingX = width / (values.size - 1)

                        values.forEachIndexed { index, value ->
                            val x = index * spacingX
                            // Invert Y mapping since Canvas top-left is 0,0
                            // Map score 0.0f - 2.5f to canvas height range (leave padding)
                            val normalizedHeight = (value / 2.5f) * (height - 30f)
                            val y = height - normalizedHeight - 15f
                            points.add(Offset(x, y))
                        }

                        // Connected draw curve path
                        path.moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val current = points[i]
                            val next = points[i + 1]
                            val conX1 = (current.x + next.x) / 2f
                            val conY1 = current.y
                            val conX2 = (current.x + next.x) / 2f
                            val conY2 = next.y

                            path.cubicTo(
                                x1 = conX1, y1 = conY1,
                                x2 = conX2, y2 = conY2,
                                x3 = next.x, y3 = next.y
                            )
                        }

                        // Draw path gradient stroke
                        drawPath(
                            path = path,
                            color = SerenePrimary,
                            style = Stroke(
                                width = 8f,
                                cap = StrokeCap.Round
                            )
                        )

                        // 3. Draw circle node points
                        points.forEach { offset ->
                            drawCircle(
                                color = SerenePrimary,
                                radius = 7f,
                                center = offset
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.5f,
                                center = offset
                            )
                        }
                    }
                }

                // X-axis days labels absolute mapping
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(top = 186.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysText.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WellnessProgressRow(
    goal: WellnessGoal,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val progress = if (goal.targetValue > 0) goal.currentValue.toFloat() / goal.targetValue.toFloat() else 0.0f
    val resolvedIcon = when (goal.id) {
        "meditation" -> Icons.Default.SelfImprovement
        "sleep" -> Icons.Default.Bedtime
        else -> Icons.Default.WaterDrop
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_row_${goal.id}"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.outline)
                }

                Icon(
                    imageVector = resolvedIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = goal.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${goal.currentValue}/${goal.targetValue} ${goal.unit}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Thin animated progress bar
        val animatedProgress by animateFloatAsState(progress, label = "ProgressAnimation")
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (progress >= 1.0f) SereneSecondary else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun ReflectionItemCard(
    reflection: Reflection,
    onDelete: () -> Unit
) {
    val showMenu = remember { mutableStateOf(false) }
    val formattedDate = remember(reflection.timestamp) {
        val sdf = SimpleDateFormat("EEEE, h:mm a", Locale.getDefault())
        sdf.format(Date(reflection.timestamp))
    }

    val resolvedMoodIcon = when (reflection.mood) {
        "GREAT" -> Icons.Default.SentimentVerySatisfied
        "GOOD" -> Icons.Default.SentimentSatisfied
        "OKAY" -> Icons.Default.SentimentNeutral
        "ANXIOUS" -> Icons.Default.SentimentDissatisfied
        else -> Icons.Default.SentimentVeryDissatisfied
    }

    val resolvedMoodColor = when (reflection.mood) {
        "GREAT" -> Color(0xFF4A90E2)
        "GOOD" -> Color(0xFF2ECC71)
        "OKAY" -> Color(0xFFF5A623)
        "ANXIOUS" -> Color(0xFF9B59B6)
        else -> Color(0xFFE74C3C)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reflection_card_${reflection.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )

                Box {
                    IconButton(
                        onClick = { showMenu.value = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = MaterialTheme.colorScheme.outline)
                    }

                    DropdownMenu(
                        expanded = showMenu.value,
                        onDismissRequest = { showMenu.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete Reflection Entry") },
                            onClick = {
                                onDelete()
                                showMenu.value = false
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = resolvedMoodIcon,
                    contentDescription = reflection.mood,
                    tint = resolvedMoodColor,
                    modifier = Modifier.size(24.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (reflection.note.isNotBlank()) reflection.note else "Felt peaceful, logged quiet check-in.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Tags row mapping
                    if (reflection.tags.isNotBlank()) {
                        FlowRow(
                            mainAxisSpacing = 6.dp,
                            crossAxisSpacing = 6.dp
                        ) {
                            reflection.tags.split(",").forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(SereneSurfaceLow, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// SCREEN 3: RESOURCE LIBRARY TAB
// ==========================================
@Composable
fun LibraryScreen(
    viewModel: SerenityViewModel,
    onResourceClick: (LibraryResource) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val libraryFilter by viewModel.libraryFilter.collectAsState()

    val categories = listOf("All", "Anxiety Relief", "Sleep Better", "Mindfulness")

    val filteredResources = remember(searchQuery, libraryFilter) {
        LibraryData.items.filter { item ->
            val matchesFilter = libraryFilter == "All" || item.category.equals(libraryFilter, ignoreCase = true)
            val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Explanatory Title
        item {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Resource Library",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Discover gentle practices, calming audios, and insightful reads to support your mental wellbeing journey.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Search Bar container
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search resources, topics, or exercises...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SereneSurfaceLowest,
                    unfocusedContainerColor = SereneSurfaceLowest,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                singleLine = true
            )
        }

        // Horizontal scrolling category list
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    val isActive = libraryFilter == cat
                    val containerColor = if (isActive) MaterialTheme.colorScheme.primary else SereneSurfaceLowest
                    val textColor = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .clickable { viewModel.setLibraryFilter(cat) }
                            .background(containerColor, RoundedCornerShape(20.dp))
                            .border(1.dp, if (isActive) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Bento layout Resources list
        if (filteredResources.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No resources found match your filter.", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            items(filteredResources) { resource ->
                BentoResourceCard(resource = resource, onClick = { onResourceClick(resource) })
            }
        }
    }
}

@Composable
fun BentoResourceCard(
    resource: LibraryResource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("resource_bento_${resource.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Render hero banner image overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFEFF4FF))
            ) {
                if (resource.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = resource.imageUrl,
                        contentDescription = resource.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback visual background with floating meditating logo
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(SerenePrimaryContainer, SereneSurfaceVariant)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                // Mini duration badge top-left
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (resource.iconName == "headphones") Icons.Default.Headphones else Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = resource.duration,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Cards details metadata
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = resource.category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = resource.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = resource.description,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = resource.type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


// ==========================================
// SCREEN 4: PROFILE SETTINGS TAB
// ==========================================
@Composable
fun SettingsScreen(
    viewModel: SerenityViewModel,
    onSosClick: () -> Unit
) {
    val pinLockEnabled by viewModel.pinLockEnabled.collectAsState()
    val encryptionEnabled by viewModel.encryptionEnabled.collectAsState()
    val morningReminderEnabled by viewModel.morningReminderEnabled.collectAsState()
    val eveningReminderEnabled by viewModel.eveningReminderEnabled.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Big profile photo header
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .background(SereneSurfaceLow)
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDf3JG9_o44FfTMHjhlnTf_puv9SVfo0MJ0Pu8tH-H655Jq9u5o9eTf2_tz6khcFGbobN-Cjl9bMwJm7dV1z7YVborW7f1mXGte2oFPdzLY8yB0A64bitFtD1hmmQDX7zjGJisqwcwWk_-ja54cg1jAtC5hWO9OcBs8gsM8G_45ijA1ZV6ga9owjTPXHK33L-8h79Eyn91KCV0Alro6m-OAPC2aq1VtRGciBuHaf2GIA83xMt0gGjK5",
                        contentDescription = "Alex Mercer Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Alex Mercer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Journeying towards mindfulness",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 1. Privacy & Security Config Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "Privacy & Security",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Switch 1: PIN lock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("App Pin Lock", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Require a PIN to open the app (Local security)", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = pinLockEnabled,
                            onCheckedChange = { viewModel.togglePinLock(it) },
                            modifier = Modifier.testTag("pin_lock_switch")
                        )
                    }

                    // Switch 2: End-to-end Encryption
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("End-to-End Encryption", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Your journal entries are fully locked", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = encryptionEnabled,
                            onCheckedChange = { viewModel.toggleEncryption(it) },
                            modifier = Modifier.testTag("encryption_switch")
                        )
                    }
                }
            }
        }

        // 2. Daily Reminders Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "Daily Reminders",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Switch 3: Morning Check-in
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Morning Check-in", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Gentle prompt to open the day peacefully", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = morningReminderEnabled,
                            onCheckedChange = { viewModel.toggleMorningReminder(it) },
                            modifier = Modifier.testTag("morning_reminder_switch")
                        )
                    }

                    // Switch 4: Evening check-in
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Evening Reflection", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Wind down and analyze your accomplishments", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Switch(
                            checked = eveningReminderEnabled,
                            onCheckedChange = { viewModel.toggleEveningReminder(it) },
                            modifier = Modifier.testTag("evening_reminder_switch")
                        )
                    }
                }
            }
        }

        // SOS Immediate Trigger Button
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSosClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("sos_button_settings"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SereneErrorContainer,
                        contentColor = SereneOnErrorContainer
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Emergency, contentDescription = null, tint = SereneOnErrorContainer)
                        Text(
                            text = "Immediate Support / SOS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                Text(
                    text = "Connect immediately to a crisis counselor if you feel overwhelmed.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}


// ==========================================
// COMPONENT: DETAIL OVERLAY DIALOGS
// ==========================================
@Composable
fun DetailResourceDialog(
    resource: LibraryResource,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    // Interactive variables for breathing practice timer or audio player mockup
    var activeBreathPhase by remember { mutableStateOf("Ready") } // Inhale, Hold, Exhale, Hold
    var breathAnimScale by remember { mutableStateOf(1.0f) }
    var runningBreathExercise by remember { mutableStateOf(false) }

    // Audio player variable
    var playingAudio by remember { mutableStateOf(false) }
    var audioProgress by remember { mutableStateOf(0.12f) }

    // Trigger visual loops for Box Breathing
    LaunchedEffect(runningBreathExercise) {
        if (runningBreathExercise) {
            while (true) {
                // Inhale 4s
                activeBreathPhase = "Inhale (4s)"
                animate(
                    initialValue = 1.0f,
                    targetValue = 2.0f,
                    animationSpec = tween(4000, easing = LinearEasing)
                ) { value, _ -> breathAnimScale = value }

                // Hold 4s
                activeBreathPhase = "Hold (4s)"
                delay(4000)

                // Exhale 4s
                activeBreathPhase = "Exhale (4s)"
                animate(
                    initialValue = 2.0f,
                    targetValue = 1.0f,
                    animationSpec = tween(4000, easing = LinearEasing)
                ) { value, _ -> breathAnimScale = value }

                // Hold 4s
                activeBreathPhase = "Hold (4s)"
                delay(4000)
            }
        } else {
            activeBreathPhase = "Ready"
            breathAnimScale = 1.0f
        }
    }

    // Trigger visual loops for Audio progression
    LaunchedEffect(playingAudio) {
        if (playingAudio) {
            while (playingAudio && audioProgress < 1.0f) {
                delay(1000)
                audioProgress = (audioProgress + 0.005f).coerceAtMost(1.0f)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .wrapContentHeight()
                .testTag("resource_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Chip / Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(SereneSurfaceLow, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = resource.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_detail_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.outline)
                    }
                }

                // Title
                Text(
                    text = resource.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Render dynamic content panel based on "exerciseType"
                when (resource.exerciseType) {
                    "breath" -> {
                        // Rendering absolute visual expanding circular interactor
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SereneSurfaceLow, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                "Box Breathing Engine",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            // Breath expanding Ball with custom canvas/animation scaling
                            Box(
                                modifier = Modifier
                                    .size(160.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background halo rings
                                Box(
                                    modifier = Modifier
                                        .size((80 * breathAnimScale).dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                )

                                // Solid ball
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SelfImprovement,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }

                            // Phase label status
                            Text(
                                text = activeBreathPhase,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(
                                onClick = { runningBreathExercise = !runningBreathExercise },
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(if (runningBreathExercise) "Pause Breathing" else "Start Breathing Loop")
                            }
                        }
                    }
                    "audio" -> {
                        // Rendering premium mockup of audio dashboard interactor
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SereneSurfaceLow, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Disk spin logo
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(SereneSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Ambient Centering Guide", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Music • SerenityPath Studio", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }

                            // Dynamic slider
                            Slider(
                                value = audioProgress,
                                onValueChange = { audioProgress = it },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { audioProgress = (audioProgress - 0.1f).coerceAtLeast(0f) }) {
                                    Icon(Icons.Default.Replay10, contentDescription = "Go Back 10s")
                                }

                                FloatingActionButton(
                                    onClick = { playingAudio = !playingAudio },
                                    modifier = Modifier.size(56.dp),
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ) {
                                    Icon(
                                        imageVector = if (playingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Playback control"
                                    )
                                }

                                IconButton(onClick = { audioProgress = (audioProgress + 0.1f).coerceAtMost(1f) }) {
                                    Icon(Icons.Default.Forward10, contentDescription = "Forward 10s")
                                }
                            }
                        }
                    }
                    else -> {
                        // Article static rendering layout
                        Text(
                            text = resource.content,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // If breathe or audio had content, display description at the bottom
                if (resource.exerciseType != "article") {
                    Text(
                        text = resource.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth().testTag("dismiss_dialog_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Complete Practice Session", fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SosCalmingDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .wrapContentHeight()
                .testTag("sos_calming_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SereneSurfaceLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Emergency header
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = null,
                    tint = SereneOnErrorContainer,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "You are not alone.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = SereneOnErrorContainer
                )

                Text(
                    text = "Take a slow, deep breath. Let's counts to 4. We are here with you. If you need sudden support or are in immediate crisis, tap below to dial the national help counselor lines.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Urgent Call Action Card 1
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:988"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot dial: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = SereneSurfaceLow),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Student Crisis Helpline", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Call 988 (Available 24/7, Toll-Free)", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

                // Urgent Call Action Card 2
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:741741")).apply {
                                    putExtra("sms_body", "HOME")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot initiate message: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = SereneSurfaceLow),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Sms, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Crisis Text Line", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Text HOME to 741741 to chat immediately", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("close_sos_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                ) {
                    Text("Close & Return", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
