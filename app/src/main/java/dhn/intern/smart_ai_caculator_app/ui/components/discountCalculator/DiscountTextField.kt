package dhn.intern.smart_ai_caculator_app.ui.components.discountCalculator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.R

@Composable
fun SplitInput(
    label: Int,
    persent: String? = null,
    value: String,
    isActive: Boolean,
    onFocus: () -> Unit,
    onClickUnit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.primary),
        ) {

        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(14.dp)
                )
                .background(MaterialTheme.colorScheme.background)
                .clickable {
                    onFocus()
                }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onClickUnit() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (persent != null){
                    Text(
                        text = if (value.isEmpty()) "0"+persent else value+persent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }else{
                    Text(
                        text = if (value.isEmpty()) "0" else value,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

            }
        }
    }
}
@Composable
fun ResultOutput(
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.background.copy(0.7f),
                shape = RoundedCornerShape(14.dp)
            ) ,
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.height(7.dp))
        ChildResultOutput(
            modifier = modifier,
            title = R.string.discount_calculator_amount_saved,
            value = value
        )
        Spacer(modifier = Modifier.height(7.dp))
        ChildResultOutput(
            modifier = modifier,
            title = R.string.discount_calculator_tax,
            value = value
        )
        Spacer(modifier = Modifier.height(7.dp))
        ChildResultOutput(
            modifier = modifier,
            title = R.string.discount_calculator_final_prices,
            value = value
        )
        Spacer(modifier = Modifier.height(7.dp))
    }
}
@Composable
fun ChildResultOutput(
    modifier: Modifier,
    title: Int,
    value: String
){
    Row(
        modifier = modifier) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
