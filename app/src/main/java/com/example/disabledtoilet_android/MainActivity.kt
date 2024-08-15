package com.example.disabledtoilet_android


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.ui.theme.BlackColor
import com.example.disabledtoilet_android.ui.theme.DisabledToilet_AndroidTheme
import com.example.disabledtoilet_android.ui.theme.WhiteColor
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DisabledToilet_AndroidTheme {
                MainScreen()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false

    )
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = bottomSheetState
        ),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "This is a Bottom Sheet", style = TextStyle(fontSize = 16.sp))
            }
        },
        sheetPeekHeight = 0.dp,
        topBar = { SearchBar() },
        containerColor = Color.White
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { NavigationBar() },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /*TODO*/ },
                    shape = CircleShape,
                    containerColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier.size(45.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plustoilet_icon),
                        contentDescription = "Add",
                        modifier = Modifier.size(40.dp)
                    )
                }
            },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(horizontal = 10.dp),

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                   Icon(
                       imageVector = Icons.Default.Search,
                       contentDescription = "Search",
                       modifier = Modifier.size(24.dp)
                   )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier =  Modifier
                            .clickable {

                            },
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "필터적용",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.notosanskr_bold))
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.rightarrow_icon),
                            contentDescription = "필터 열기",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            // BottomSheet의 상태를 토글합니다.
//                            if (bottomSheetState.isVisible) {
//                                bottomSheetState.hide() // 이미 열려있다면 숨깁니다.
//                            } else {
//                                bottomSheetState.expand() // 닫혀있다면 엽니다.
//                            }
                            bottomSheetState.expand()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.LightGray)
                ) {
                    Text("Show Bottom Sheet")
                }
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
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


