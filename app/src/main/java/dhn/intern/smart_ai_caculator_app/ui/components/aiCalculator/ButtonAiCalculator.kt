package dhn.intern.smart_ai_caculator_app.ui.components.aiCalculator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.enum.AiScanKey

@Composable
fun ButtonScanChatAi(
    onGalleryClick: () -> Unit = {},
    onCaptureClick: () -> Unit = {},
    onFlashClick: () -> Unit = {},
    isFlashOn: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButtonScan(
            drawable = R.drawable.library,
            onClick = onGalleryClick
        )
        Spacer(modifier = Modifier.weight(1f))
        ImageButtonScan(
            drawable = R.drawable.ic_camera,
            onClick = onCaptureClick
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButtonScan(
            drawable = R.drawable.flash,
            onClick = onFlashClick,
            tint = if (isFlashOn) Color(0xFFFFD600) else Color.Unspecified
        )
    }
}
@Composable
fun BottomScanAndChat(
    selected: AiScanKey,
    onSelect: (AiScanKey) -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .height(95.dp)
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                )
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonScanChatAiChild(
                icon = R.drawable.scan,
                title = R.string.ai_calculator_scan,
                state = selected == AiScanKey.SCAN,
                onClick = { onSelect(AiScanKey.SCAN) }
            )

            Spacer(modifier = Modifier.width(100.dp))

            ButtonScanChatAiChild(
                icon = R.drawable.chatai,
                title = R.string.ai_calculator_chat,
                state = selected == AiScanKey.CHAT,
                onClick = { onSelect(AiScanKey.CHAT) }
            )
        }
    }
}
@Composable
fun IconButtonScan(
    drawable: Int,
    onClick: () -> Unit = {},
    tint: Color = Color.Unspecified
){
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp),
    ) {
        Icon(
            painter = painterResource(drawable),
            contentDescription = null,
            modifier = Modifier.size(65.dp),
            tint = tint,
        )
    }
}

@Composable
fun ImageButtonScan(
    drawable: Int,
    onClick: () -> Unit = {}
){
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(90.dp),
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = null,
            modifier = Modifier.size(90.dp)
        )
    }
}


@Composable
fun ButtonScanChatAiChild(
    icon: Int,
    title: Int,
    state: Boolean,
    onClick: () -> Unit
){
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            modifier = Modifier.size(28.dp),
            contentDescription = null,
            tint = if(state){
                MaterialTheme.colorScheme.outline
            }else{
                MaterialTheme.colorScheme.onSurface
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if(state){
                MaterialTheme.colorScheme.outline
            }else{
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun ButtonSendChatAiChild(
    image: Int,
    icon: Int,
    onClick: () -> Unit = {}
){
    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit
        )

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(15.dp)
        )
    }
}


