package com.andlill.keynotes.app.home.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun DrawerHeader() {
    Spacer(modifier = Modifier
        .statusBarsHeight()
        .fillMaxWidth()
    )
}