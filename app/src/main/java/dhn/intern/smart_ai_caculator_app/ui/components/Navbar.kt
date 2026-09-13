package dhn.intern.smart_ai_caculator_app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R

@Composable
fun NavBar(
    navController: NavHostController,
    title: Int,
    contentColor: Color = MaterialTheme.colorScheme.onBackground
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.back_navbar),
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    navController.popBackStack()
                },
            contentDescription = null,
            tint = contentColor

        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
fun NavBar_basic(
    navController: NavHostController,
    title: Int,
    icon: Int,
    onclick: ()->Unit,
    modifier: Modifier
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.back_navbar),
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    navController.popBackStack()
                },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground

        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(icon),
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    onclick()
                },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground

        )

    }
}