package frame;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Frame {
    private String UUID;
    private long fromId;
    private long toId;
    private String message;

    private TimeMarks timeMarks;
    private Status status;

    @Getter
    public static class TimeMarks {
        private long sent = 0;
        private long received = 0;
        private long returned = 0;

        public void setSent() {
            this.sent = System.nanoTime();
        }

        public void setReceived() {
            this.received = System.nanoTime();
        }

        public void setReturned() {
            this.returned = System.nanoTime();
        }
    }

    public static Frame createFrame(long fromId, long toId) {
        Frame frame = new Frame();
        frame.setUUID(java.util.UUID.randomUUID().toString());
        frame.setFromId(fromId);
        frame.setToId(toId);
        frame.setMessage("Heading from " + fromId + " to " + toId);
        frame.setTimeMarks(new TimeMarks());
        return frame;
    }
}
