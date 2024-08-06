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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
                        ImageContent(modifier = Modifier.weight(1f))
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
            .fillMaxHeight()
    )
}

@Composable
fun NavigationBar() {
    BottomAppBar(
        containerColor = Color.White // 배경색 설정
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text("Button 1")
            }
            Button(onClick = { /*TODO*/ }) {
                Text("Button 2")
            }
            Button(onClick = { /*TODO*/ }) {
                Text("Button 3")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {Text("화장실을 검색하세요")},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = Color.Black
            )
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
