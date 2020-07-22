package com.cascade.easy.lang

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

/**
 * Provides lazy initialization depending on the lifecycle events.
 * Default lifecycle initialization event is [Lifecycle.Event.ON_CREATE].
 */
class LifecycleInit<T>(lifecycle: Lifecycle, initEvent: Lifecycle.Event, initializer: () -> T) {
    private var value: T? = null

    init {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == initEvent) {
                    value = initializer()
                }
            }
        })
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        value ?: throw IllegalStateException("Not initialized")
}

fun <T> lifecycle(
    lifecycle: Lifecycle,
    event: Lifecycle.Event = Lifecycle.Event.ON_CREATE,
    initializer: () -> T
) = LifecycleInit(lifecycle, event, initializer)