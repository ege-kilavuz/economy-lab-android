package com.egekilavuz.economylab.ui.screens.story

import androidx.compose.runtime.Composable
import com.egekilavuz.economylab.data.story.StoryViewModel

@Composable
fun StoryScreen(vm: StoryViewModel) {
    when (vm.screen) {
        StoryViewModel.SCREEN_PLAY -> StoryPlayScreen(vm)
        StoryViewModel.SCREEN_JOURNAL -> StoryJournalScreen(vm)
        StoryViewModel.SCREEN_CHECKPOINT -> StoryCheckpointScreen(vm)
        StoryViewModel.SCREEN_END -> StoryEndScreen(vm)
        else -> StoryIntroScreen(vm)
    }
}
