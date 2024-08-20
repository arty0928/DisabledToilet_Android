//package com.example.disabledtoilet_android
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.Font
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.disabledtoilet_android.ui.theme.blackColor
//import com.example.disabledtoilet_android.ui.theme.GreyColor
//
//@Composable
//fun DetailPage() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Detail Page",
//            style = TextStyle(
//                fontSize = 24.sp,
//                fontFamily = FontFamily(Font(R.font.notosanskr_bold)),
//                color = blackColor
//            )
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = "This is a detailed description of the selected item. You can provide more information here.",
//            style = TextStyle(
//                fontSize = 16.sp,
//                fontFamily = FontFamily(Font(R.font.notosanskr_regular)),
//                color = GreyColor
//            )
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { /* TODO: Add action here */ }) {
//            Text(text = "Action Button")
//        }
//    }
//}
