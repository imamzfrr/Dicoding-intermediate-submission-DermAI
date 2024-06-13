package zfr.mobile.submissionsatu.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import zfr.mobile.submissionsatu.api.response.story.StoryItem

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    val stories: Flow<PagingData<StoryItem>> by lazy {
        repository.getStories().cachedIn(viewModelScope)
    }
}

class StoryViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
