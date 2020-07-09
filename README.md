# Youtrack

https://youtrack.jetbrains.com/issue/KT-40163

# Use case

Trying to create a class that would handle progression and event emission.
This class (**EventStateFlow**) as a MutableStateFlow that emit Event class. 

The **Event** class will allow to process events 
only once (with the `getContentIfNotHandled()` method).

When subscribing to the **EventStateFlow**, the Event class will be hidden and it will only emit data that has not been 
processed yet.
Usually, the content of an **Event** is a Result so that the UI can easily differentiate success or error.

# Bug

My sample will try to emit a **FakeValue** through two **EventStateFlow**  :
The **WorkingEventStateFlow** has a `Flow<Event<Result<FakeValue>>>` attribute. The UI (the main function) will consume the event 
emitted through the flow and display its content. This part is working.

The **BuggedEventStateFlow** has a `Flow<Result<FakeValue>>` attribute. The Event part is hidden because we call 
`.mapNotNull { it?.getContentIfNotHandled() }` on the `MutableStateFlow<Event<Result<FakeValue>>>`
The flow should be emitting a `Result<FakeValue>` but as the following crash suggests :

```
java.lang.ClassCastException: kotlin.Result cannot be cast to FakeValue
	at MainKt$main$buggedJob$1.invokeSuspend(Main.kt:26)
	at MainKt$main$buggedJob$1.invoke(Main.kt)
	at kotlinx.coroutines.flow.FlowKt__TransformKt$onEach$$inlined$unsafeTransform$1$2.emit(Collect.kt:134)
	at BuggedEventStateFlow$events$$inlined$mapNotNull$1$2.emit(Collect.kt:135)
	at kotlinx.coroutines.flow.StateFlowImpl.collect(StateFlow.kt:274)
	at BuggedEventStateFlow$events$$inlined$mapNotNull$1.collect(SafeCollector.common.kt:114)
	at kotlinx.coroutines.flow.FlowKt__TransformKt$onEach$$inlined$unsafeTransform$1.collect(SafeCollector.common.kt:114)
	at kotlinx.coroutines.flow.FlowKt__LimitKt$take$$inlined$unsafeFlow$1.collect(SafeCollector.common.kt:116)
	at kotlinx.coroutines.flow.FlowKt__EmittersKt$onStart$$inlined$unsafeFlow$1.collect(SafeCollector.common.kt:120)
	at kotlinx.coroutines.flow.FlowKt__ErrorsKt.catchImpl(Errors.kt:229)
	at kotlinx.coroutines.flow.FlowKt.catchImpl(Unknown Source)
	at kotlinx.coroutines.flow.FlowKt__ErrorsKt$catch$$inlined$unsafeFlow$1.collect(SafeCollector.common.kt:113)
	at kotlinx.coroutines.flow.FlowKt__CollectKt.collect(Collect.kt:30)
	at kotlinx.coroutines.flow.FlowKt.collect(Unknown Source)
	at kotlinx.coroutines.flow.FlowKt__CollectKt$launchIn$1.invokeSuspend(Collect.kt:50)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:56)
	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:738)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)
```

It emits a `Result<Result<FakeValue>>` (the debugger show this when we evaluate the emitted value, but not in the watch view).
A very strange bug indeed!  


