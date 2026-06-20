package smartcampus.attendance.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Utility class for common date and time operations used across the Attendance service.
 */
public final class DateUtils {

    public static final ZoneId UTC = ZoneId.of("UTC");

    private DateUtils() {
        // utility class
    }

    /**
     * Returns {@code true} if the given date is strictly before today (in UTC).
     */
    public static boolean isInPast(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isBefore(LocalDate.now(UTC));
    }

    /**
     * Returns {@code true} if the given instant is strictly before now.
     */
    public static boolean isInPast(Instant instant) {
        if (instant == null) {
            return false;
        }
        return instant.isBefore(Instant.now());
    }

    /**
     * Returns {@code true} if the given date is today or in the future (in UTC).
     */
    public static boolean isTodayOrFuture(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(LocalDate.now(UTC));
    }

    /**
     * Converts a {@link LocalDateTime} (assumed to be in UTC) to an {@link Instant}.
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    /**
     * Converts a {@link LocalDate} to the start-of-day {@link Instant} in UTC.
     */
    public static Instant toStartOfDayInstant(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(UTC).toInstant();
    }

    /**
     * Converts an {@link Instant} to a {@link ZonedDateTime} in UTC.
     */
    public static ZonedDateTime toUtc(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(UTC);
    }

    /**
     * Converts a {@link LocalDateTime} to a UTC {@link ZonedDateTime}.
     */
    public static ZonedDateTime toUtc(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(UTC);
    }

    /**
     * Returns the current UTC date.
     */
    public static LocalDate todayUtc() {
        return LocalDate.now(UTC);
    }

    /**
     * Returns the current UTC instant.
     */
    public static Instant nowUtc() {
        return Instant.now();
    }
}
