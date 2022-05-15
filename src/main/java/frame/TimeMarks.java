package frame;

import lombok.Getter;

@Getter
public class TimeMarks {
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
