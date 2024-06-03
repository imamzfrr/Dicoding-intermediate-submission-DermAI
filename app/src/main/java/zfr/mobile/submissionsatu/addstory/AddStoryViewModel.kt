package zfr.mobile.submissionsatu.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import zfr.mobile.submissionsatu.story.StoryRepository

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _addStoryResult = MutableLiveData<Boolean>()
    val addStoryResult: LiveData<Boolean> get() = _addStoryResult

    fun addStory(description: RequestBody, photo: MultipartBody.Part): LiveData<Boolean> {
        viewModelScope.launch {
            val result = repository.addStory(description, photo)
            _addStoryResult.value = result
        }
        return addStoryResult
    }
}

class AddStoryViewModelFactory(private val repository: StoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
