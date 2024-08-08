package com.example.disabledtoilet_android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disabledtoilet_android.ui.theme.DisabledToilet_AndroidTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DisabledToilet_AndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { SearchBar() },
                    bottomBar = { NavigationBar() },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /*TODO*/ },
                            shape = CircleShape,
                            containerColor = Color.White,
                            elevation = FloatingActionButtonDefaults.elevation(8.dp),
                            modifier = Modifier.size(45.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    },
                    containerColor = Color.White // 배경색 설정
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 55.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .padding(horizontal = 16.dp)
                                .background(Color.Red),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween

                        ){
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        //
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Text(
                                    text = "필터적용",
                                    style = TextStyle(
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.notosanskr_medium))
                                    )
                                )
                                //맨 오른쪽
                                Image(
                                    painter = painterResource(id = R.drawable.rightarrow_icon),
                                    contentDescription = "필터 열기",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color.LightGray)
                        ) {
                            // ImageContent 컴포저블을 여기에 넣을 수 있습니다.
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    )
}

@Composable
fun NavigationBar() {
    val navBarHeight = 80.dp
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    val textState = remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
