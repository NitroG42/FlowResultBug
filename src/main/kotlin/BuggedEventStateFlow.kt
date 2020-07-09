import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull

class BuggedEventStateFlow<T> {
    val events: Flow<Result<T>>
        get() = eventMLD.mapNotNull { it?.getContentIfNotHandled() }
    val eventMLD = MutableStateFlow<Event<Result<T>>?>(null)

    fun result(result: Result<T>) {
        eventMLD.value = Event(result)
    }
}