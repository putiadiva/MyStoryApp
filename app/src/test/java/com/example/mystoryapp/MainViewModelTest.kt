package com.example.mystoryapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.mystoryapp.data.remote.paging.StoryRepository
import com.example.mystoryapp.data.remote.response.Story
import com.example.mystoryapp.ui.StoryAdapter
import com.example.mystoryapp.viewmodel.MainViewModel
import com.example.mystoryapp.viewmodel.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var preferences: UserPreferences

    @Test
    fun `when Get Story Not Empty Should Return Success`() = runTest {
        // kasus: story ada. size tidak nol.
        val dummyStory = DummyData.generateDummyStory()
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStory)

        val expected = MutableLiveData<PagingData<Story>>()
        expected.value = data

        Mockito.`when`(storyRepository.getStory()).thenReturn(expected)

        val mainViewModel = MainViewModel(preferences, storyRepository)
        val actual: PagingData<Story> = mainViewModel.listStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actual)

        // memastikan data tidak null.
        Assert.assertNotNull(differ.snapshot())
        // memastikan jumlah data sesuai dengan yang diharapkan.
        Assert.assertEquals(dummyStory.size, differ.snapshot().items.size)
        // memastikan data pertama yang dikembalikan sesuai.
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        // kasus: story tidak ada. size nol.
        val dummyStory = listOf<Story>()
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStory)

        val expected = MutableLiveData<PagingData<Story>>()
        expected.value = data

        Mockito.`when`(storyRepository.getStory()).thenReturn(expected)

        val mainViewModel = MainViewModel(preferences, storyRepository)
        val actual: PagingData<Story> = mainViewModel.listStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)

        Assert.assertNotNull(differ.snapshot())
        // memastikan jumlah data yang dikembalikan nol.
        Assert.assertEquals(0, differ.snapshot().items.size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}