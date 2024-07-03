package dev.hassanabid.ioextendedsg

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedMaterial3Api")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun ProductDetailsScreen() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Image Pager
        HorizontalPager(
            pageSize = PageSize.Fill, // Replace with the actual number of images
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp)),
        ) { page ->

            Box(
                modifier = Modifier
                    .padding(1.dp)
                    .background(Color.Blue)
                    .fillMaxWidth()
                    .aspectRatio(1f),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lettuce), // Replace with your image resource
                    contentDescription = "Product Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

        }

        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8F8F8)) // Light background color
                .padding(16.dp)
        ) {
            Text(
                text = "Boston Lettuce",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF2C3E50) // Dark text color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "1.10 € / piece",
                fontSize = 18.sp,
                color = Color(0xFF808080) // Gray color for price
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "~ 150 gr / piece",
                fontSize = 14.sp,
                color = Color(0xFF008000) // Green color for weight
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Spain",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF2C3E50)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Lettuce is an annual plant of the daisy family, Asteraceae. It is most often grown as a leaf vegetable, but sometimes for its stem and seeds. Lettuce is most often used for salads, although it is also seen in other kinds of food, such as soups, sandwiches and wraps; it can also be grilled.",
                fontSize = 16.sp,
                color = Color(0xFF2C3E50)
            )

        }

        // Buttons
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom, // Align buttons to the bottom
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Add padding around the buttons
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)), // Light gray background
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color(0xFF808080) // Light gray icon color
                    )
                }

                Spacer(modifier = Modifier.width(16.dp)) // Space between buttons

                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .weight(2f)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)) // Green button color
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Add to Cart",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADD TO CART", color = Color.White)
                }
            }
        }
    }
}