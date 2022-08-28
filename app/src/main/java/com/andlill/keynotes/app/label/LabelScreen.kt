package com.andlill.keynotes.app.label

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlill.keynotes.R
import com.andlill.keynotes.app.AppState
import com.andlill.keynotes.app.label.composables.CreateLabel
import com.andlill.keynotes.app.label.composables.EditLabel
import com.andlill.keynotes.app.label.composables.SelectLabel
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.ui.shared.button.MenuIconButton
import com.andlill.keynotes.ui.shared.modifier.orientationModifiers
import com.andlill.keynotes.utils.DialogUtils

@Composable
fun LabelScreen(appState: AppState, noteId: Int?) {
    val viewModel: LabelViewModel = viewModel(factory = LabelViewModel.Factory(LocalContext.current.applicationContext as Application, noteId))
    val context = LocalContext.current

    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .orientationModifiers(
                        portrait = Modifier.statusBarsPadding(),
                        landscape = Modifier.statusBarsPadding().navigationBarsPadding()
                    ),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                navigationIcon = {
                    MenuIconButton(
                        icon = Icons.Outlined.ArrowBack,
                        color = MaterialTheme.colors.onSurface,
                        onClick = {
                            appState.navigation.navigateUp()
                        }
                    )
                },
                title = {
                    CreateLabel(
                        onCreate = {
                            val label = Label(value = it)
                            viewModel.onCreateLabel(label)
                        }
                    )
                },
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .navigationBarsPadding()
                    .imePadding(),
                color = MaterialTheme.colors.surface,
                content = {
                    Column {
                        Divider()
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items = viewModel.labels, key = { it.id }) { label ->
                                if (noteId != null) {
                                    SelectLabel(
                                        text = label.value,
                                        checked = viewModel.noteLabels.contains(label),
                                        onClick = {
                                            viewModel.onToggleNoteLabel(label)
                                        }
                                    )
                                }
                                else {
                                    EditLabel(
                                        initialText = label.value,
                                        onUpdate = {
                                            viewModel.onUpdateLabel(label.copy(value = it))
                                        },
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