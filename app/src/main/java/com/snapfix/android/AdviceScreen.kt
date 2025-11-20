package com.snapfix.android

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceScreen(
    viewModel: SharedViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val capturedData by viewModel.capturedData.collectAsState()

    capturedData?.let { data ->
        // Get top detections by confidence
        val topDetections = remember(data) {
            data.detections
                .sortedByDescending { it.score }
                .take(5)
        }

        // State for selected detection index
        var selectedIndex by remember { mutableStateOf(0) }

        // Get selected detection
        val selectedDetection = remember(topDetections, selectedIndex) {
            topDetections.getOrNull(selectedIndex)
        }

        // Generate professional advice for selected detection only
        val adviceList = remember(selectedDetection) {
            selectedDetection?.let { generateAdvice(listOf(it)) } ?: emptyList()
        }

        // Generate full text for sharing/copying
        val fullAdviceText = remember(adviceList, selectedDetection) {
            buildString {
                appendLine("🔧 SNAPFIX REPAIR ADVICE")
                appendLine("━━━━━━━━━━━━━━━━━━━━")
                appendLine()
                selectedDetection?.let { detection ->
                    appendLine("SELECTED ITEM:")
                    appendLine("${detection.label.replaceFirstChar { it.uppercase() }} (${(detection.score * 100).toInt()}% confidence)")
                    appendLine()
                }
                appendLine("PROFESSIONAL RECOMMENDATIONS:")
                adviceList.forEachIndexed { index, advice ->
                    appendLine()
                    appendLine("${index + 1}. ${advice.title.uppercase()}")
                    appendLine("   ${advice.description}")
                    appendLine()
                    advice.steps.forEachIndexed { stepIndex, step ->
                        appendLine("   ${stepIndex + 1}. $step")
                    }
                }
                appendLine()
                appendLine("━━━━━━━━━━━━━━━━━━━━")
                appendLine("⚠️ Always prioritize safety and consult professionals for complex repairs.")
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Repair Advice") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        // Copy button
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("SnapFix Advice", fullAdviceText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Advice copied to clipboard", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, "Copy")
                        }

                        // Share button
                        IconButton(onClick = {
                            context.shareText(fullAdviceText, "Share SnapFix Advice")
                        }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Object selector chips
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Select Object for Advice",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Selector chips for each detection
                            topDetections.forEachIndexed { index, detection ->
                                FilterChip(
                                    selected = selectedIndex == index,
                                    onClick = { selectedIndex = index },
                                    label = {
                                        Text(
                                            text = detection.label.replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    leadingIcon = if (selectedIndex == index) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    } else null,
                                    trailingIcon = {
                                        Surface(
                                            color = if (selectedIndex == index)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.outline,
                                            shape = MaterialTheme.shapes.small
                                        ) {
                                            Text(
                                                text = "${(detection.score * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selectedIndex == index)
                                                    MaterialTheme.colorScheme.onPrimary
                                                else
                                                    MaterialTheme.colorScheme.surface,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Professional advice cards
                items(adviceList) { advice ->
                    AdviceCard(advice = advice)
                }

                // Safety disclaimer
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Always prioritize safety. Consult licensed professionals for electrical, plumbing, or structural work.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdviceCard(advice: RepairAdvice) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = advice.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = advice.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Step-by-Step Instructions:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            advice.steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Data class for repair advice
 */
data class RepairAdvice(
    val title: String,
    val description: String,
    val steps: List<String>
)

/**
 * Generate professional advice based on detected objects
 */
fun generateAdvice(detections: List<Detection>): List<RepairAdvice> {
    val adviceList = mutableListOf<RepairAdvice>()
    val labels = detections.map { it.label.lowercase() }

    // Contextual advice based on combinations
    if (labels.contains("person") && (labels.contains("ladder") || labels.any { it.contains("tool") })) {
        adviceList.add(
            RepairAdvice(
                title = "Work-at-Height Safety",
                description = "When working at elevated positions with tools, proper safety equipment is essential to prevent falls and injuries.",
                steps = listOf(
                    "Always use a certified safety harness when working above 6 feet",
                    "Ensure ladder is on stable, level ground with proper angle (4:1 ratio)",
                    "Maintain three points of contact with ladder at all times",
                    "Use tool belt to keep hands free while climbing",
                    "Never work alone - have a spotter present"
                )
            )
        )
    }

    if (labels.contains("sink") || labels.contains("toilet")) {
        adviceList.add(
            RepairAdvice(
                title = "Plumbing Fixture Maintenance",
                description = "Regular maintenance of plumbing fixtures prevents water damage, reduces water bills, and extends the life of your fixtures.",
                steps = listOf(
                    "Turn off water supply before any repair work",
                    "For leaky faucets: Replace worn washers or O-rings",
                    "For running toilets: Check and replace flapper valve if needed",
                    "Use plumber's tape on threaded connections to prevent leaks",
                    "Test all connections for leaks before restoring full water pressure",
                    "Keep area dry and check for hidden leaks after 24 hours"
                )
            )
        )
    }

    if (labels.contains("refrigerator") || labels.contains("microwave") || labels.contains("oven")) {
        adviceList.add(
            RepairAdvice(
                title = "Appliance Maintenance & Safety",
                description = "Proper maintenance of kitchen appliances ensures energy efficiency, prevents breakdowns, and extends appliance lifespan.",
                steps = listOf(
                    "Unplug appliance before any maintenance or cleaning",
                    "Clean refrigerator coils every 6 months with vacuum or coil brush",
                    "Check door seals for tears or gaps - replace if necessary",
                    "For microwave: Never operate empty, clean with mild soap and water",
                    "Inspect power cords for fraying or damage - replace if compromised",
                    "Ensure proper ventilation around appliances for heat dissipation"
                )
            )
        )
    }

    if (labels.contains("tv") || labels.contains("laptop") || labels.contains("monitor")) {
        adviceList.add(
            RepairAdvice(
                title = "Electronics Care & Troubleshooting",
                description = "Proper care of electronic devices prevents overheating, extends lifespan, and maintains optimal performance.",
                steps = listOf(
                    "Clean air vents monthly with compressed air to prevent dust buildup",
                    "Check all cable connections - reseat loose cables",
                    "Use surge protector to protect against power fluctuations",
                    "Keep devices in well-ventilated areas, away from heat sources",
                    "For screen issues: Check brightness settings and cable connections first",
                    "Backup important data regularly before attempting any repairs"
                )
            )
        )
    }

    if (labels.contains("chair") || labels.contains("couch") || labels.contains("bed")) {
        adviceList.add(
            RepairAdvice(
                title = "Furniture Repair & Restoration",
                description = "Maintaining and repairing furniture saves money and extends the life of your investment pieces.",
                steps = listOf(
                    "Identify problem: wobbling, squeaking, or structural damage",
                    "Tighten all screws, bolts, and fasteners with appropriate tools",
                    "For loose joints: Apply wood glue and clamp for 24 hours",
                    "Replace worn furniture glides to prevent floor damage",
                    "For upholstery tears: Use fabric adhesive or sew with upholstery needle",
                    "Apply furniture polish or oil to wood surfaces for protection"
                )
            )
        )
    }

    if (labels.any { it.contains("bicycle") || it.contains("skateboard") }) {
        adviceList.add(
            RepairAdvice(
                title = "Bike & Board Maintenance",
                description = "Regular maintenance of bikes and boards ensures safe operation and prevents accidents.",
                steps = listOf(
                    "Check tire pressure weekly - inflate to recommended PSI",
                    "Inspect brakes - replace worn pads immediately for safety",
                    "Lubricate chain with bike-specific lubricant every 100-200 miles",
                    "Check and tighten all bolts, especially on handlebars and wheels",
                    "For skateboards: Replace worn grip tape and inspect trucks for cracks",
                    "Store in dry location to prevent rust and deterioration"
                )
            )
        )
    }

    if (labels.contains("car") || labels.contains("truck") || labels.contains("motorcycle")) {
        adviceList.add(
            RepairAdvice(
                title = "Vehicle Maintenance Basics",
                description = "Regular vehicle maintenance prevents breakdowns, improves safety, and maintains resale value.",
                steps = listOf(
                    "Check oil level monthly - change every 3,000-5,000 miles or per manual",
                    "Inspect tire tread depth and pressure - rotate every 6,000 miles",
                    "Test all lights (headlights, brake lights, turn signals) monthly",
                    "Check and top off all fluids: brake, coolant, windshield washer",
                    "Replace air filter annually for better fuel efficiency",
                    "Keep detailed maintenance records for warranty and resale"
                )
            )
        )
    }

    // Generic advice if no specific category matched
    if (adviceList.isEmpty()) {
        val primaryDetection = detections.firstOrNull()
        if (primaryDetection != null) {
            adviceList.add(
                RepairAdvice(
                    title = "General Maintenance for ${primaryDetection.label.replaceFirstChar { it.uppercase() }}",
                    description = "Basic maintenance and care guidelines for keeping your ${primaryDetection.label} in good condition.",
                    steps = listOf(
                        "Regular inspection: Check for visible damage, wear, or loose parts",
                        "Clean regularly with appropriate cleaning materials for the item type",
                        "Follow manufacturer's maintenance schedule if available",
                        "Address minor issues promptly before they become major problems",
                        "Consult professional if repair requires specialized tools or expertise",
                        "Document repairs and maintenance for future reference"
                    )
                )
            )
        }
    }

    return adviceList
}
