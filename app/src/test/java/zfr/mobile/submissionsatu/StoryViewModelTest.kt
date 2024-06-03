package zfr.mobile.submissionsatu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.flow.collectLatest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import zfr.mobile.submissionsatu.api.response.story.StoryItem
import zfr.mobile.submissionsatu.story.StoryRepository
import zfr.mobile.submissionsatu.story.StoryViewModel

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var storyRepository: StoryRepository
    private lateinit var storyViewModel: StoryViewModel

    @Before
    fun setUp() {
        storyRepository = mock(StoryRepository::class.java)
        storyViewModel = StoryViewModel(storyRepository)
    }

    @Test
    fun `load stories successfully`() = runBlockingTest {
        val dummyStories = DataDummy.generateDummyStories()
        val expectedData = PagingData.from(dummyStories)

        `when`(storyRepository.getStories()).thenReturn(flowOf(expectedData))

        val actualData = LiveDataTestUtil.getOrAwaitValue(storyViewModel.stories)

        assertNotNull(actualData)
        val storyList = actualData.collectDataForTest()
        assertEquals(dummyStories.size, storyList.size)
        assertEquals(dummyStories[0], storyList[0])
    }

    @Test
    fun `no stories returned`() = runBlockingTest {
        val expectedData = PagingData.empty<StoryItem>()

        `when`(storyRepository.getStories()).thenReturn(flowOf(expectedData))

        val actualData = LiveDataTestUtil.getOrAwaitValue(storyViewModel.stories)

        assertNotNull(actualData)
        val storyList = actualData.collectDataForTest()
        assertTrue(storyList.isEmpty())
    }

    private fun <T : Any> PagingData<T>.collectDataForTest(): List<T> {
        val result = mutableListOf<T>()
        runBlocking {
            this@collectDataForTest.collectLatest { pagingData ->
                pagingData.map { item ->
                    result.add(item)
                }
            }
        }
        return result
    }

}
