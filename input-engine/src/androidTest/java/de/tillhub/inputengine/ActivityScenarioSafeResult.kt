package de.tillhub.inputengine

import android.app.Instrumentation
import androidx.test.core.app.ActivityScenario
import org.junit.Assert

// define our custom result property
val ActivityScenario<*>.safeResult: Instrumentation.ActivityResult
    get() {
        awaitBlock { state == androidx.lifecycle.Lifecycle.State.DESTROYED } // await for the activity to be destroyed
        return this.result // this will return quick as the result is already retrieved
    }

// util function to retry and await until the block is true or the timeout is reached
internal fun awaitBlock(timeOut: Int = 7500, block: () -> Boolean) {
    val start = System.currentTimeMillis()
    var value = block.invoke()
    while (!value && System.currentTimeMillis() < start + timeOut) {
        Thread.sleep(50)
        value = block.invoke()
    }
    Assert.assertTrue("Couldn't await the condition", value)
}