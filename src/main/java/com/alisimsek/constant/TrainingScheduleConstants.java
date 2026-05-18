package com.alisimsek.constant;

import java.time.ZoneId;
import java.util.List;

public final class TrainingScheduleConstants {

    public static final ZoneId ZONE = ZoneId.of("Europe/Istanbul");
    public static final int MAX_DURATION_MINUTES = 45;
    public static final List<Integer> ALLOWED_HOURS = List.of(9, 10, 11, 13, 14, 15, 16, 17, 18);

    private TrainingScheduleConstants() {}
}
