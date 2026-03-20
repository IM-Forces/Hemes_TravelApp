package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.applyLocale
import com.example.hermes_travelapp.data.PreferencesManager
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.ThemeViewModel

@Composable
fun PreferencesScreen(
    onBack: () -> Unit = {},
    themeViewModel: ThemeViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefsManager = remember { PreferencesManager(context) }
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    
    val currentLangCode = prefsManager.language
    
    PreferencesScreenContent(
        isDarkMode = isDarkMode,
        currentLangCode = currentLangCode,
        onBack = onBack,
        onToggleDarkMode = { themeViewModel.toggleDarkMode(it) },
        onLanguageChange = { langCode ->
            prefsManager.language = langCode
            applyLocale(context, langCode)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreenContent(
    isDarkMode: Boolean,
    currentLangCode: String,
    onBack: () -> Unit = {},
    onToggleDarkMode: (Boolean) -> Unit = {},
    onLanguageChange: (String) -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emailUpdatesEnabled by remember { mutableStateOf(false) }
    
    val currentLangDisplay = when (currentLangCode) {
        "es" -> stringResource(R.string.prefs_lang_spanish)
        "ca" -> stringResource(R.string.prefs_lang_catalan)
        else -> stringResource(R.string.prefs_lang_english)
    }
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    // Fixed: stringResource() must be called in a @Composable context, not inside remember's non-composable lambda
    val initialCurrency = stringResource(R.string.prefs_currency_default)
    var selectedCurrency by remember { mutableStateOf(initialCurrency) }
    
    val initialDateFormat = stringResource(R.string.prefs_date_format_default)
    var selectedDateFormat by remember { mutableStateOf(initialDateFormat) }
    
    val initialTextSize = stringResource(R.string.prefs_text_size_default)
    var selectedTextSize by remember { mutableStateOf(initialTextSize) }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.prefs_language)) },
            text = {
                Column {
                    LanguageOption(stringResource(R.string.prefs_lang_english)) { 
                        onLanguageChange("en")
                        showLanguageDialog = false
                    }
                    LanguageOption(stringResource(R.string.prefs_lang_spanish)) { 
                        onLanguageChange("es")
                        showLanguageDialog = false
                    }
                    LanguageOption(stringResource(R.string.prefs_lang_catalan)) { 
                        onLanguageChange("ca")
                        showLanguageDialog = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.prefs_title), style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: App Settings
            Text(
                text = stringResource(R.string.prefs_app_settings),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )

            PreferenceItem(
                title = stringResource(R.string.prefs_language),
                subtitle = currentLangDisplay,
                icon = Icons.Default.Language,
                onClick = { showLanguageDialog = true }
            )

            PreferenceItem(
                title = stringResource(R.string.prefs_currency),
                subtitle = selectedCurrency,
                icon = Icons.Default.Payments,
                onClick = { /* Mock: Open Currency Dialog */ }
            )

            PreferenceItem(
                title = stringResource(R.string.prefs_date_format),
                subtitle = selectedDateFormat,
                icon = Icons.Default.CalendarToday,
                onClick = { /* Mock: Open Date Format Dialog */ }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            // Section: Appearance
            Text(
                text = stringResource(R.string.prefs_appearance),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

            PreferenceSwitchItem(
                title = stringResource(R.string.prefs_dark_mode),
                subtitle = if (isDarkMode) stringResource(R.string.prefs_on) else stringResource(R.string.prefs_off),
                icon = Icons.Default.DarkMode,
                checked = isDarkMode,
                onCheckedChange = onToggleDarkMode
            )

            PreferenceItem(
                title = stringResource(R.string.prefs_text_size),
                subtitle = selectedTextSize,
                icon = Icons.Default.FormatSize,
                onClick = { /* Mock: Open Text Size Dialog */ }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

            // Section: Notifications
            Text(
                text = stringResource(R.string.prefs_notifications),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

            PreferenceSwitchItem(
                title = stringResource(R.string.prefs_push_notifications),
                subtitle = stringResource(R.string.prefs_push_notifications_subtitle),
                icon = Icons.Default.NotificationsActive,
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            PreferenceSwitchItem(
                title = stringResource(R.string.prefs_email_updates),
                subtitle = stringResource(R.string.prefs_email_updates_subtitle),
                icon = Icons.Default.Mail,
                checked = emailUpdatesEnabled,
                onCheckedChange = { emailUpdatesEnabled = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Mock: Save Preferences */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.prefs_save), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LanguageOption(label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun PreferenceItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PreferenceSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Prefs Mode Light")
@Composable
fun PreferencesScreenPreviewLight() {
    Hermes_travelappTheme(darkTheme = false) {
        PreferencesScreenContent(
            isDarkMode = false,
            currentLangCode = "en"
        )
    }
}

@Preview(showBackground = true, name = "Prefs Mode Dark")
@Composable
fun PreferencesScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        PreferencesScreenContent(
            isDarkMode = true,
            currentLangCode = "en"
        )
    }
}
