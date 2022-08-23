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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andlill.keynotes.R
import com.andlill.keynotes.app.AppState
import com.andlill.keynotes.app.label.composables.CreateLabel
import com.andlill.keynotes.app.label.composables.EditLabel
import com.andlill.keynotes.model.Label
import com.andlill.keynotes.ui.shared.button.MenuIconButton
import com.andlill.keynotes.utils.DialogUtils

@Composable
fun LabelScreen(appState: AppState) {
    val viewModel: LabelViewModel = viewModel(factory = LabelViewModel.Factory(LocalContext.current.applicationContext as Application))
    val context = LocalContext.current
    val state = rememberScaffoldState()

    Scaffold(
        scaffoldState = state,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
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
                },
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .imePadding()
                    .navigationBarsPadding(),
                color = MaterialTheme.colors.surface,
                content = {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            CreateLabel(
                                onCreate = {
                                    viewModel.onCreateLabel(Label(value = it))
                                }
                            )
                        }
                        items(items = viewModel.labels, key = { it.id }) { label ->
                            EditLabel(
                                initialText = label.value,
                                onUpdate = {
                                    viewModel.onUpdateLabel(label.copy(value = it))
                                },
                                onDelete = {
                                    DialogUtils.showConfirmDialog(String.format(context.resources.getString(R.string.label_screen_dialog_confirm_label_delete), label.value)) {
                                        viewModel.onDeleteLabel(label)
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    )
}