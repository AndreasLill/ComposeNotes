package com.andlill.composenotes.app.note.composables

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andlill.composenotes.R
import com.andlill.composenotes.ui.shared.text.DialogTitle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReminderPermissionDialog(
    state: MutableState<Boolean>,
    onGrantedPermissions: () -> Unit,
) {
    if (state.value) {
        val permissionNotifications = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        val permissionAlarms = rememberPermissionState(Manifest.permission.SCHEDULE_EXACT_ALARM)

        LaunchedEffect(permissionNotifications.status, permissionAlarms.status) {
            if (permissionNotifications.status.isGranted && permissionAlarms.status.isGranted) {
                delay(50)
                onGrantedPermissions()
                state.value = false
            }
        }

        Dialog(
            onDismissRequest = { state.value = false }) {
            Column(
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
                    .padding(16.dp)
            ) {
                DialogTitle(text = stringResource(R.string.note_screen_dialog_reminder_permission_title))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = stringResource(R.string.note_screen_dialog_reminder_permission_description)
                )
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permissionNotifications.status.isGranted,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionNotifications.launchPermissionRequest()
                        }
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = if (permissionNotifications.status.isGranted) Icons.Outlined.Check else Icons.Outlined.Close,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.note_screen_dialog_reminder_permission_notifications)
                    )
                }
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !permissionAlarms.status.isGranted,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            permissionAlarms.launchPermissionRequest()
                        }
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = if (permissionAlarms.status.isGranted) Icons.Outlined.Check else Icons.Outlined.Close,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.note_screen_dialog_reminder_permission_alarms)
                    )
                }
            }
        }
    }
}