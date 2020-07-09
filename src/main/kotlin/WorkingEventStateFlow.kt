import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class WorkingEventStateFlow<T> {
    val events: Flow<Event<Result<T>>>
        get() = eventMLD.filterNotNull()
    private val eventMLD = MutableStateFlow<Event<Result<T>>?>(null)

    fun result(result: Result<T>) {
        eventMLD.value = Event(result)
    }
}