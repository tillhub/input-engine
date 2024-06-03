package de.tillhub.inputengine;

import static androidx.test.internal.util.Checks.checkNotNull;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.test.core.app.ActivityScenario;
import org.junit.rules.ExternalResource;

public final class ActivityScenarioRule<A extends Activity> extends ExternalResource {

    /**
     * Same as {@link java.util.function.Supplier} which requires API level 24.
     */
    @RestrictTo(Scope.LIBRARY)
    interface Supplier<T> {
        T get();
    }

    private final Supplier<ActivityScenario<A>> scenarioSupplier;
    @Nullable private ActivityScenario<A> scenario;

    /**
     * Constructs ActivityScenarioRule with a given intent.
     *
     * @param startActivityIntent an intent to start an activity
     */
    public ActivityScenarioRule(Intent startActivityIntent) {
        scenarioSupplier = () -> ActivityScenario.launchActivityForResult(checkNotNull(startActivityIntent));
    }

    @Override
    protected void before() throws Throwable {
        scenario = scenarioSupplier.get();
    }

    @Override
    protected void after() {
        scenario.close();
    }

    /**
     * Returns {@link ActivityScenario} of the given activity class.
     *
     * @throws NullPointerException if you call this method while test is not running
     * @return a non-null {@link ActivityScenario} instance
     */
    public ActivityScenario<A> getScenario() {
        return checkNotNull(scenario);
    }
}
