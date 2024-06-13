package zfr.mobile.submissionsatu

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object LiveDataTestUtil {

    fun <T> getOrAwaitValue(
        flow: Flow<T>,
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)

        runBlocking {
            afterObserve.invoke()
            data = flow.first()
            latch.countDown()

            if (!withContext(Dispatchers.IO) {
                    latch.await(time, timeUnit)
                }) {
                throw TimeoutException("Flow value was never emitted.")
            }
        }

        return data!!
    }
}


