package smartcampus.attendance.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.UUID;

/**
 * Standard event envelope for all Kafka events published by the Attendance service.
 * The {@code payload} field uses {@link JsonTypeInfo} for polymorphic serialization
 * so consumers can deserialize the correct concrete payload type.
 */
public class KafkaEventEnvelope {

    private UUID eventId;
    private String eventType;
    private UUID tenantId;
    private Instant occurredAt;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private Object payload;

    public KafkaEventEnvelope() {
    }

    public KafkaEventEnvelope(UUID eventId, String eventType, UUID tenantId, Instant occurredAt, Object payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.tenantId = tenantId;
        this.occurredAt = occurredAt;
        this.payload = payload;
    }

    /**
     * Convenience factory — generates a random {@code eventId} and sets {@code occurredAt} to now.
     */
    public static KafkaEventEnvelope of(String eventType, UUID tenantId, Object payload) {
        return new KafkaEventEnvelope(UUID.randomUUID(), eventType, tenantId, Instant.now(), payload);
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
