package zfr.mobile.submissionsatu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import zfr.mobile.submissionsatu.api.response.story.StoryItem
import zfr.mobile.submissionsatu.story.StoryAdapter
import zfr.mobile.submissionsatu.story.StoryRepository
import zfr.mobile.submissionsatu.story.StoryViewModel

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private val dummyStories = DataDummy.generateDummyStories()

    @Before
    fun setUp() {
        storyRepository = Mockito.mock(StoryRepository::class.java)
    }

    @Test
    fun loadStoriesSuccessfully() = runTest {
        val expectedData = StoryPagingSourceTest.snapshot(dummyStories)

        Mockito.`when`(storyRepository.getStories()).thenReturn(flowOf(expectedData))

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = Dispatchers.Main
        )

        val storyViewModel = StoryViewModel(storyRepository)
        val realStory = LiveDataTestUtil.getOrAwaitValue(storyViewModel.stories)

        differ.submitData(realStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories, differ.snapshot().items)
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun noStoriesReturned() = runTest {
        Mockito.`when`(storyRepository.getStories()).thenReturn(flowOf(PagingData.empty()))

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = Dispatchers.Main
        )

        val storyViewModel = StoryViewModel(storyRepository)
        val actualData = LiveDataTestUtil.getOrAwaitValue(storyViewModel.stories)

        differ.submitData(actualData)

        assertNotNull(differ.snapshot())
        assertTrue(differ.snapshot().items.isEmpty())
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class StoryPagingSourceTest : PagingSource<Int, StoryItem>() {
        override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = 1
            )
        }

        companion object {
            fun snapshot(items: List<StoryItem>): PagingData<StoryItem> {
                return PagingData.from(items)
            }
        }
    }
}

