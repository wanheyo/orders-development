package com.prim.orders.notificationhandler;

public class UniqueIdRegistry {
    private static int lastUsedScheduleId = 0;
    private static int lastUsedCancelId = 1000; // Offset for cancellation IDs

    public static synchronized int generateScheduleId() {
        return ++lastUsedScheduleId;
    }

    public static synchronized int generateCancelId() {
        return ++lastUsedCancelId;
    }
}
