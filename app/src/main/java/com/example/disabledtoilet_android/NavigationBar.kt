package com.example.disabledtoilet_android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NavigationBar() {
    val navBarHeight = 80.dp
    BottomAppBar(
        containerColor = Color.White,
        modifier = Modifier.height(navBarHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(navBarHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Icon(
                    painter = painterResource(id = R.drawable.save_icon),
                    contentDescription = "찜 아이콘",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "저장",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = FontFamily(Font(R.font.notosanskr_bold))

                )

            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Icon(
                    painter = painterResource(id = R.drawable.plustoilet_icon),
                    contentDescription = "화장실 등록 아이콘",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "등록",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = FontFamily(Font(R.font.notosanskr_bold))

                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Icon(
                    painter = painterResource(id = R.drawable.mypage_icon),
                    contentDescription = "마이페이지 아이콘",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "MY",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = FontFamily(Font(R.font.notosanskr_bold))

                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Icon(
                    painter = painterResource(id = R.drawable.mypage_icon),
                    contentDescription = "마이페이지 아이콘",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "MY",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = FontFamily(Font(R.font.notosanskr_bold))

                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Icon(
                    painter = painterResource(id = R.drawable.mypage_icon),
                    contentDescription = "마이페이지 아이콘",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "MY",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = FontFamily(Font(R.font.notosanskr_bold))

                )
            }
        }
    }
}

