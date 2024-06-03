package zfr.mobile.submissionsatu.story

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import zfr.mobile.submissionsatu.api.ApiService
import zfr.mobile.submissionsatu.api.response.story.StoryItem
import java.io.IOException

class StoryRepository(
    private val apiService: ApiService
) {
    fun getStories(): Flow<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).flow
    }

    suspend fun getStoriesWithLocation(): List<StoryItem> {
        return try {
            val response = apiService.getStoriesWithLocation()
            if (!response.error) {
                response.listStory
            } else {
                Log.e("ApiService", "Failed to fetch stories: ${response.message}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Failed to fetch stories with location: ${e.message}")
            emptyList()
        }
    }

    suspend fun addStory(description: RequestBody, photo: MultipartBody.Part): Boolean {
        return try {
            val response = withContext(Dispatchers.IO) { apiService.addStory(description, photo) }
            !response.error
        } catch (e: HttpException) {
            Log.e("ApiService", "Failed to add story: ${e.message}")
            false
        } catch (e: IOException) {
            Log.e("ApiService", "Network error: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("ApiService", "Unknown error: ${e.message}")
            false
        }
    }
}
