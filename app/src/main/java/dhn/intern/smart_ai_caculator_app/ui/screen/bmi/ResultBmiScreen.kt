package dhn.intern.smart_ai_caculator_app.ui.screen.bmi


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.source.bmi.BmiResultData
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.bmi.BmiGauge
import dhn.intern.smart_ai_caculator_app.ui.components.bmi.BmiResultList

@Composable
fun ResultBMIScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    weight: Float,
    heightFt: Int,
    heightIn: Int,
    age: Int,
    gender: String,
    bmi: Float
){
    val results = BmiResultData.getBmiResults(bmi)
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            NavBar(
                navController = navController,
                title = R.string.menu_bmi_calculator,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            Text(
                text = stringResource(R.string.bmi_calculator_your_bmi),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%.1f", bmi),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                BmiBadge(
                    text = results.first { it.isActive }.label,
                    color = results.first { it.isActive }.color
                )
            }
            Spacer(Modifier.height(150.dp))

            BmiGauge(
                bmi = bmi,
                needleRes = R.drawable.bmi
            )
            Text(
                text = "${String.format("%.2f", weight)} kg | ${heightFt}ft ${heightIn}in | $gender | $age years old",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                BmiResultList(bmi = bmi)
            }
        }

    }
}
@Composable
fun BmiBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}