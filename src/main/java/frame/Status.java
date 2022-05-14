package frame;

public enum Status {
    RETURNED,
    DELIVERED,
    FLYING;

    public boolean isReturned(Frame frame) {
        return frame.getStatus() == RETURNED;
    }

    public boolean isDelivered(Frame frame) {
        return frame.getStatus() == DELIVERED;
    }

    public boolean isFlying(Frame frame) {
        return frame.getStatus() == FLYING;
    }
}
