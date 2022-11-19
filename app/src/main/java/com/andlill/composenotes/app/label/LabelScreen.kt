package com.andlill.composenotes.app.label

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlill.composenotes.R
import com.andlill.composenotes.app.AppState
import com.andlill.composenotes.app.label.composables.CreateLabel
import com.andlill.composenotes.app.label.composables.EditLabel
import com.andlill.composenotes.app.label.composables.SelectLabel
import com.andlill.composenotes.ui.shared.button.MenuIconButton
import com.andlill.composenotes.utils.DialogUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelScreen(appState: AppState, noteId: Int?) {
    val viewModel: LabelViewModel = viewModel(factory = LabelViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))
    val context = LocalContext.current

    val sortedLabels = remember {
        derivedStateOf {
            viewModel.labels.sortedBy { it.value.lowercase() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    MenuIconButton(
                        icon = Icons.Outlined.ArrowBack,
                        onClick = {
                            appState.navigation.navigateUp()
                        }
                    )
                },
                title = {
                    CreateLabel(
                        onCreate = viewModel::onCreateLabel
                    )
                },
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.surface,
                content = {
                    Column {
                        Divider()
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items = sortedLabels.value, key = { it.id }) { label ->
                                if (noteId != null) {
                                    SelectLabel(
                                        label = label,
                                        checked = viewModel.noteLabels.contains(label),
                                        onClick = viewModel::onToggleNoteLabel
                                    )
                                }
                                else {
                                    EditLabel(
                                        label = label,
                                        onUpdate = viewModel::onUpdateLabel,
                                        onDelete = {
                                            DialogUtils.showConfirmDialog(
                                                text = String.format(context.resources.getString(R.string.label_screen_dialog_confirm_label_delete), label.value),
                                                annotation = label.value,
                                                annotationStyle = SpanStyle(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                onConfirm = {
                                                    viewModel.onDeleteLabel(label)
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    )
}