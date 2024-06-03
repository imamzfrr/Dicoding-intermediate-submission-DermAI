package zfr.mobile.submissionsatu

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import zfr.mobile.submissionsatu.api.response.story.StoryItem
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object LiveDataTestUtil {

    fun getOrAwaitValue(
        flow: Flow<PagingData<StoryItem>>,
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): PagingData<StoryItem> {
        var data: PagingData<StoryItem>? = null
        val latch = CountDownLatch(1)

        runBlocking {
            afterObserve.invoke()
            data = flow.first()
            latch.countDown()

            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("Flow value was never emitted.")
            }
        }

        return data!!
    }

}
