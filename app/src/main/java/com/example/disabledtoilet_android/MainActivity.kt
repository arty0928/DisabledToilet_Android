package com.example.disabledtoilet_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.disabledtoilet_android.ui.theme.DisabledToilet_AndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DisabledToilet_AndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { SearchBar() },
                    bottomBar = { NavigationBar() },
                    containerColor = Color.White // 배경색 설정
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
//                        Spacer(modifier = Modifier.height(16.dp))
                        //전체 공간에서 weight 비율만큼 지금은 1배
                        Box(modifier = Modifier.weight(1f)){
                            ImageContent(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageContent(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = "지도 넣기",
        modifier = Modifier
            .fillMaxSize()
    )
}

@Composable
fun NavigationBar() {
    val navBarHeight = 60.dp
    BottomAppBar(
        containerColor = Color.Cyan,
        modifier = Modifier.height(navBarHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(navBarHeight),

            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                Icon(
                    painter = painterResource(id = R.drawable.save_icon),
                    contentDescription = "찜 아이콘",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plustoilet_icon),
                    contentDescription = "화장실 추가 아이콘",
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mypage_icon),
                    contentDescription = "마이페이지 아이콘",
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    val textState = remember{ mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp)
    ){
        OutlinedTextField(
            value = textState.value,
            onValueChange = {textState.value = it},
            placeholder = {Text("화장실을 검색하세요")},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black
            ),
            textStyle = TextStyle(color = Color.Black)
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DisabledToilet_AndroidTheme {
        Greeting("Android")
    }
}
