package com.zavgar.system.designsystem.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavgar.system.designsystem.theme.ZavGarBottomNavShape
import com.zavgar.system.designsystem.theme.accent
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.designsystem.theme.navBackground

enum class ZavGarTab { Home, History, Settings }

@Composable
fun ZavGarBottomNavBar(
    selected: ZavGarTab,
    onSelect: (ZavGarTab) -> Unit,
    modifier: Modifier = Modifier,
    homeLabel: String = "Главная",
    historyLabel: String = "История",
    settingsLabel: String = "Настройки",
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = ZavGarBottomNavShape, clip = false)
            .clip(ZavGarBottomNavShape)
            .background(colors.navBackground)
            .height(78.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Item(Icons.Outlined.Home, homeLabel, selected == ZavGarTab.Home) { onSelect(ZavGarTab.Home) }
        Item(Icons.Outlined.History, historyLabel, selected == ZavGarTab.History) { onSelect(ZavGarTab.History) }
        Item(Icons.Outlined.Settings, settingsLabel, selected == ZavGarTab.Settings) { onSelect(ZavGarTab.Settings) }
    }
}

@Composable
private fun Item(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val tint = if (active) colors.accent else colors.foregroundSecondary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                color = tint,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
