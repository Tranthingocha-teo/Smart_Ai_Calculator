package dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitData
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitUi
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.toCategory
import dhn.intern.smart_ai_caculator_app.enum.UnitCategory

@Composable
fun ItemsRowUnit(
    unitTab: UnitUi?,
    onSelectUnitTab: (UnitUi, UnitCategory) -> Unit
) {
    val unitItems = UnitData.getUnitData()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        items(unitItems) { item ->
            ItemsUnit(
                image = item.image,
                icontext = item.iconText,
                title = item.title,
                isSelected = unitTab == item,
                onClick = {
                    onSelectUnitTab(item, item.toCategory())
                }
            )
            Spacer(modifier = Modifier.width(15.dp))
        }
    }
}


@Composable
fun ItemsUnit(
    image: Int?,
    icontext: String?,
    title: Int,
    isSelected: Boolean,
    onClick: () -> Unit
){
    val background = if (isSelected)
        MaterialTheme.colorScheme.secondaryContainer.copy(0.3f)
    else
        MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(0.2f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .background(background)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        image?.let {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
        }
        icontext?.let {
            Text(
                text = icontext,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(title) ,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
