package com.example.disabledtoilet_android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

@Composable
fun IconToggleButton(isSaved: Boolean, iconSize: Dp, onIconClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onIconClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = if (isSaved) R.drawable.saved_star_icon else R.drawable.save_icon),
            contentDescription = "저장 아이콘",
            modifier = Modifier.size(iconSize)
        )
    }
}
