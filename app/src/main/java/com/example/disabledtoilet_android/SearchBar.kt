package com.example.disabledtoilet_android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.disabledtoilet_android.ui.theme.BlackColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    val textState = remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
            .height(50.dp)
    ) {
        OutlinedTextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            placeholder = { Text("화장실을 검색하세요") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = BlackColor,
                unfocusedBorderColor = BlackColor,
                cursorColor = BlackColor
            ),
            textStyle = TextStyle(color = BlackColor)
        )
    }
}
