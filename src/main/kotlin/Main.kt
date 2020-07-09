import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking

fun main() {
    val scope = CoroutineScope(Dispatchers.Default)

    val wvm = WorkingViewModel()
    val workingJob = wvm.uiState.events.onEach {
        val content = it.getContentIfNotHandled()
        if (content != null) {
            content.onSuccess {
                println("${wvm.javaClass.simpleName} WORKED, got enum $it")
            }.onFailure {
                println("got error $it")
            }
        }

    }.take(1).onStart { wvm.start() }.launchIn(scope)

    val bvm = BuggedViewModel()
    val buggedJob = bvm.uiState.events.onEach { result: Result<FakeValue> ->
        //Here result should be a Result<TestEnum> but runtime evaluation indicate Result<Result<TestEnum>>
        result.onSuccess {
            println("got enum $it")
        }.onFailure {
            println("got error $it")
        }
    }.take(1).onStart { bvm.start() }.catch {
        println("${bvm.javaClass.simpleName} NOT WORKING")
        it.printStackTrace()
    }.launchIn(scope)

    runBlocking {
        listOf(workingJob, buggedJob).joinAll()
    }
}

private class BuggedViewModel {
    val uiState = BuggedEventStateFlow<FakeValue>()
    fun start() {
        uiState.result(Result.success(FakeValue.ONE))
    }
}

private class WorkingViewModel {
    val uiState = WorkingEventStateFlow<FakeValue>()
    fun start() {
        uiState.result(Result.success(FakeValue.ONE))
    }
}