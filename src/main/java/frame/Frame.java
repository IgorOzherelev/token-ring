package frame;

import lombok.Getter;
import lombok.Setter;

import static frame.Status.*;

@Setter
@Getter
public class Frame {
    private String UUID;
    private long fromId;
    private long toId;
    private String message;

    private TimeMarks timeMarks;
    private Status status;

    public static Frame createFrame(long fromId, long toId) {
        Frame frame = new Frame();
        frame.setUUID(java.util.UUID.randomUUID().toString());
        frame.setFromId(fromId);
        frame.setToId(toId);
        frame.setStatus(FLYING);
        frame.setMessage("Heading from " + fromId + " to " + toId);
        frame.setTimeMarks(new TimeMarks());
        return frame;
    }
}
