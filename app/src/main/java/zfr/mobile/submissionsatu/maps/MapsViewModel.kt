package zfr.mobile.submissionsatu.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import zfr.mobile.submissionsatu.api.response.story.StoryItem
import zfr.mobile.submissionsatu.story.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _storiesWithLocation = MutableLiveData<List<StoryItem>>()
    val storiesWithLocation: LiveData<List<StoryItem>> get() = _storiesWithLocation

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            val stories = repository.getStoriesWithLocation()
            _storiesWithLocation.value = stories
        }
    }
}

class MapsViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

