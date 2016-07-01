
package com.smart.mirrorer.event;


import org.greenrobot.eventbus.EventBus;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be
 * replaced with a more efficient means such as through injection directly into
 * interested classes.
 */
public final class BusProvider {

    private static final EventBus BUS = new EventBus();

    public static EventBus getInstance() {
        return BUS;
    }

    private BusProvider()
    {
        // No instances.
    }
}
