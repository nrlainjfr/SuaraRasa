
package com.example.SuaraRasa_A209008.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AssessmentCard(title: String, imageRes: Int, onClick: () -> Unit) {
    Card(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
            modifier = Modifier
                .padding(8.dp)
                .width(150.dp)
                .height(150.dp)
        ) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
              ) {
            Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.size(64.dp)
                 )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
        }
    }
}
