package dev.hassanabid.ioextendedsg.studioui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hassanabid.ioextendedsg.R

@SuppressLint("UnusedMaterial3Api")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PlantDetailsScreen() {
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFFF0EEE6)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
                        label = { Text("Favorites") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
                        label = { Text("Cart") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agave), // Replace with your image
                    contentDescription = "Plant Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF0EEE6),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "$45",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_share), // Replace with your share icon
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_info_details), // Replace with your heart icon
                                contentDescription = "Favorite",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Agave",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = android.R.drawable.checkbox_on_background), // Replace with your check icon
                        contentDescription = "In stock",
                        tint = Color.Green
                    )
                    Text(
                        text = "In stock",
                        color = Color.Green
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent sollicitudin nibh at libero fermentum rutrum. Integer ut massa mattis, tempus enim sed, commodo lorem. Mauris tincidunt mi sapien, eget volutpat elit consequat laoreet. Integer hendrerit. Integer hendrerit.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6149)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Add to cart",
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                DropdownMenuItem(
                    text = { Text("Care instructions") },
                    onClick = {
                        Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show()
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.arrow_down_float), // Replace with your down arrow icon
                            contentDescription = "Expand"
                        )
                    }
                )
                Divider(Modifier.padding(vertical = 8.dp))
                DropdownMenuItem(
                    text = { Text("FAQ") },
                    onClick = { /*TODO*/ },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.arrow_down_float), // Replace with your down arrow icon
                            contentDescription = "Expand"
                        )
                    }
                )
            }
        }
    }
}