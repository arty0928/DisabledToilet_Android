package com.example.disabledtoilet_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}



//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.widget.Space
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.semantics.Role.Companion.Image
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.Font
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.disabledtoilet_android.R
//import com.example.disabledtoilet_android.ui.theme.BlackColor
//import com.example.disabledtoilet_android.ui.theme.DisabledToilet_AndroidTheme
//import com.example.disabledtoilet_android.ui.theme.GreyColor
//import com.example.disabledtoilet_android.ui.theme.MainColor
//import com.example.disabledtoilet_android.ui.theme.WhiteColor
//import kotlinx.coroutines.launch
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            DisabledToilet_AndroidTheme {
//                MainScreen()
//            }
//        }
//    }
//}
//
//
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen() {
//    var isSaved by remember { mutableStateOf(false) }
//
//    val bottomSheetState = rememberStandardBottomSheetState(
//        initialValue = SheetValue.Hidden,
//        skipHiddenState = false
//
//    )
//    val coroutineScope = rememberCoroutineScope()
//
//    BottomSheetScaffold(
//        scaffoldState = rememberBottomSheetScaffoldState(
//            bottomSheetState = bottomSheetState
//        ),
//        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
//        sheetPeekHeight = 0.dp,
//        sheetSwipeEnabled = true,
//        topBar = { SearchBar() },
//        containerColor = WhiteColor,
//        sheetContainerColor = WhiteColor,
//        sheetDragHandle = null,
//        sheetContent = {
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(250.dp)
//                    .background(Color.White)
//                    .padding(horizontal = 10.dp),
//            ) {
//
//                Box(
//                    modifier = Modifier
//                        .padding(top = 13.dp)
//                        .wrapContentHeight()
//                        .fillMaxWidth(),
//                    contentAlignment = Alignment.Center // Box의 내용을 중앙에 정렬
//                ) {
//                    Spacer(
//                        modifier = Modifier
//                            .height(3.dp)
//                            .width(40.dp)
//                            .background(GreyColor)
//                    )
//                }
//
//
//                Box(
//                    modifier = Modifier
//                        .weight(1.3f)
//                        .fillMaxWidth()
//                ){
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                    ){
//                        Box(
//                            modifier = Modifier
//                                .weight(1.5f)
//                                .fillMaxWidth()
//                        )
//                        {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(horizontal = 16.dp)
//                            ){
//                                Text(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .fillMaxHeight()
//                                        .wrapContentHeight(Alignment.CenterVertically),
//
//                                    text = "세진공원",
//                                    style = TextStyle(
//                                        color = MainColor,
//                                        fontSize = 20.sp,
//                                        fontFamily = FontFamily(Font(R.font.notosanskr_bold))
//                                    ),
//                                    maxLines = 1,
//                                    overflow = TextOverflow.Ellipsis
//                                )
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxHeight()
//                                        .weight(1f),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ){
//                                    IconToggleButton(isSaved, 15.dp) {
//                                        isSaved = !isSaved
//                                    }
//                                    Text(
//                                        modifier = Modifier
//                                            .fillMaxHeight()
//                                            .padding(start = 3.dp)
//                                            .wrapContentHeight(Alignment.CenterVertically),
//
//                                        text = "저장 (10) ",
//                                        style = TextStyle(
//                                            color = Color.Black,
//                                            fontSize = 14.sp,
//                                            fontFamily = FontFamily(Font(R.font.notosanskr_medium))
//                                        )
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.weight(1f)) // 나머지 공간을 채워 "더보기"를 끝으로 밀기
//
//                                Text(
//                                    modifier = Modifier
//                                        .fillMaxHeight()
//                                        .wrapContentWidth(Alignment.End)
//                                        .wrapContentHeight(Alignment.CenterVertically),
//
//                                    text = "더보기",
//                                    style = TextStyle(
//                                        color = MainColor,
//                                        fontSize = 16.sp,
//                                        fontFamily = FontFamily(Font(R.font.notosanskr_bold))
//                                    )
//                                )
//
//
//                            }
//                        }
//                        HorizontalDivider(
//                            modifier = Modifier
//                                .padding(horizontal = 16.dp)
//                                .height(1.dp)
//                                .fillMaxWidth(),
//                            color = GreyColor
//                        )
//                        Box(
//                            modifier = Modifier
//                                .weight(1f)
//                                .fillMaxWidth()
//                                .padding(end = 10.dp)
//                        )
//                        {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                            ){
//
//                                Text(
//                                    modifier = Modifier
//                                        .fillMaxHeight()
//                                        .padding(horizontal = 16.dp)
//                                        .wrapContentHeight(Alignment.CenterVertically),
//
//
//                                    text = "영업 중",
//                                    style = TextStyle(
//                                        color = Color.Black,
//                                        fontSize = 15.sp,
//                                        fontFamily = FontFamily(Font(R.font.notosanskr_semibold))
//                                    ),
//                                )
//
//                                Text(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .fillMaxHeight()
//                                        .wrapContentHeight(Alignment.CenterVertically),
//
//                                    text = "08 : 00 - 22 : 00 ",
//                                    style = TextStyle(
//                                        color = Color.Black,
//                                        fontSize = 14.sp,
//                                        fontFamily = FontFamily(Font(R.font.notosanskr_medium))
//                                    )
//                                )
//                            }
//                        }
//                        Box(
//                            modifier = Modifier
//                                .weight(1f)
//                                .fillMaxWidth()
//                        )
//                        {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(horizontal = 16.dp)
//                            ){
//                                Text(
//                                    modifier = Modifier
//                                        .fillMaxHeight()
//                                        .padding(end = 15.dp)
//                                        .wrapContentHeight(Alignment.CenterVertically),
//
//
//                                    text = "864 m",
//                                    style = TextStyle(
//                                        color = BlackColor,
//                                        fontSize = 15.sp,
//                                        fontFamily = FontFamily(Font(R.font.notosanskr_bold))
//                                    ),
//                                )
//
//                                Text(
//                                    modifier = Modifier
//                                        .weight(1f)
//                                        .fillMaxHeight()
//                                        .wrapContentHeight(Alignment.CenterVertically),
//
//                                    text = "서울특별시 세진구",
//                                    style = TextStyle(
//                                        color = GreyColor,
//                                        fontSize = 14.sp,
//                                        fontFamily = FontFamily(Font(R.font.notosanskr_medium))
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//                HorizontalDivider(
//                    modifier = Modifier
//                        .padding(horizontal = 16.dp)
//                        .height(1.dp)
//                        .fillMaxWidth(),
//                    color = GreyColor
//                )
//                Box(
//                    modifier = Modifier
//                        .weight(1f) // 1:1 비율로 나누기 위해 weight 사용
//                        .fillMaxWidth()
//                ){
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight(),
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        verticalAlignment = Alignment.CenterVertically
//                    ){
//                        Column(
//                            modifier = Modifier
//                                .weight(1f)
//                                .fillMaxHeight()
//                                .clickable { },
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ){
//
//                            IconToggleButton(isSaved, 40.dp) {
//                                isSaved = !isSaved
//                            }
//                            Text(
//                                text = "저장",
//                                style = TextStyle(fontSize = 15.sp),
//                                modifier = Modifier.align(Alignment.CenterHorizontally),
//                                fontFamily = FontFamily(Font(R.font.notosanskr_bold))
//
//                            )
//
//                        }
//                        Column(
//                            modifier = Modifier
//                                .weight(1f)
//                                .fillMaxHeight()
//                                .clickable { },
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ){
//                            Icon(
//                                painter = painterResource(id = R.drawable.car_icon),
//                                contentDescription = "네비게이션 아이콘",
//                                modifier = Modifier.size(40.dp)
//                            )
//                            Text(
//                                text = "네비게이션",
//                                style = TextStyle(fontSize = 15.sp),
//                                modifier = Modifier.align(Alignment.CenterHorizontally),
//                                fontFamily = FontFamily(Font(R.font.notosanskr_bold))
//
//                            )
//                        }
//                        Column(
//                            modifier = Modifier
//                                .weight(1f)
//                                .fillMaxHeight()
//                                .clickable { },
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ){
//                            Icon(
//                                painter = painterResource(id = R.drawable.share_icon),
//                                contentDescription = "공유 아이콘",
//                                modifier = Modifier.size(40.dp)
//                            )
//                            Text(
//                                text = "공유",
//                                style = TextStyle(fontSize = 15.sp),
//                                modifier = Modifier.align(Alignment.CenterHorizontally),
//                                fontFamily = FontFamily(Font(R.font.notosanskr_bold))
//
//                            )
//                        }
//                    }
//                }
////                DetailPage()
//            }
//        },
//
//
//    ) {
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            bottomBar = { NavigationBar() },
//            floatingActionButton = {
//                FloatingActionButton(
//                    onClick = { /*TODO*/ },
//                    shape = CircleShape,
//                    containerColor = Color.White,
//                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
//                    modifier = Modifier.size(45.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.plustoilet_icon),
//                        contentDescription = "Add",
//                        modifier = Modifier.size(40.dp)
//                    )
//                }
//            },
//            containerColor = Color.White
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(30.dp)
//                        .padding(horizontal = 10.dp),
//
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                   Icon(
//                       imageVector = Icons.Default.Search,
//                       contentDescription = "Search",
//                       modifier = Modifier.size(24.dp)
//                   )
//                    Icon(
//                        imageVector = Icons.Default.Search,
//                        contentDescription = "Search",
//                        modifier = Modifier.size(24.dp)
//                    )
//
//                    Spacer(modifier = Modifier.weight(1f))
//                    Row(
//                        modifier =  Modifier
//                            .clickable {
//
//                            },
//                        verticalAlignment = Alignment.CenterVertically
//                    ){
//                        OutlinedButton(
//                            onClick = { /* TODO: Handle button click */ },
//                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MainColor), // 배경색을 설정
//                            modifier = Modifier
//                                .height(40.dp) // 버튼 높이를 설정
//                                .wrapContentWidth(), // 너비를 원하는 대로 설정
//                            shape = RoundedCornerShape(12.dp), // 버튼 모양을 둥글게 설정
//                            border = BorderStroke(2.dp, Color.Red), // 빨간색 테두리를 추가
//                            contentPadding = PaddingValues(horizontal = 16.dp) // 내부 패딩 조정
//                        ) {
//                            Text(
//                                text = "조건 검색",
//                                style = TextStyle(
//                                    color = Color.White, // 텍스트 색상을 흰색으로 설정하여 버튼 배경과 대비되도록 함
//                                    fontSize = 14.sp,
//                                    fontFamily = FontFamily(Font(R.font.notosanskr_bold))
//                                )
//                            )
//                        }
//
//
//                    }
//                }
//                Spacer(modifier = Modifier.height(5.dp))
//                Button(
//                    onClick = {
//                        coroutineScope.launch {
//                            // BottomSheet의 상태를 토글합니다.
////                            if (bottomSheetState.isVisible) {
////                                bottomSheetState.hide() // 이미 열려있다면 숨깁니다.
////                            } else {
////                                bottomSheetState.expand() // 닫혀있다면 엽니다.
////                            }
//                            bottomSheetState.expand()
//                        }
//                    },
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()
//                        .background(Color.LightGray)
//                ) {
//                    Text("Show Bottom Sheet")
//                }
//            }
//        }
//    }
//}
//
//
