package frame;

public enum Status {
    RETURNED,
    RECEIVED,
    FLYING;

    public boolean isReturned() {
        return this == RETURNED;
    }

    public boolean isReceived() {
        return this == RECEIVED;
    }

    public boolean isFlying() {
        return this == FLYING;
    }
}
