package ring;

import frame.Frame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static frame.Status.*;

@Slf4j
public class TokenRingObserver {
    private final static String CSV_SEPARATOR = ",";

    @Getter
    private final int nodesNum;
    private final long framesToRegister;
    private BufferedWriter bw = null;
    private final List<Frame> frames = new ArrayList<>();
    private final AtomicLong frameCounter = new AtomicLong(0);
    private final AtomicBoolean aliveRingFlag;

    private final Object processorNotifier;

    public TokenRingObserver(int tryNumber, String fileName, int nodesNum, long framesToRegister,
                             Object processorNotifier, AtomicBoolean aliveRingFlag) {
        try {
            var tryDir = new File("src/main/resources/" + tryNumber);
            boolean tr = tryDir.mkdir();
            this.bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream("src/main/resources/" + tryNumber + "/" + fileName + ".csv"), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.nodesNum = nodesNum;
        this.framesToRegister = framesToRegister;
        this.processorNotifier = processorNotifier;
        this.aliveRingFlag = aliveRingFlag;
    }

    public boolean isAllDataRegistered() {
        return this.frameCounter.get() == this.framesToRegister;
    }

    public void registerFrame(Frame frame) {
        synchronized (this.frames) {
            frame.setStatus(RETURNED);
            frame.getTimeMarks().setReturned();
            long currentSize = this.frameCounter.incrementAndGet();
            log.info("Observer: registered returned frame with id: {}", frame.getUUID());
            this.frames.add(frame);

            if (currentSize == this.framesToRegister) {
                synchronized (this.processorNotifier) {
                    this.aliveRingFlag.set(false);
                    log.info("Observer: finished frame capturing, notifying processor");
                    this.processorNotifier.notifyAll();
                }
            }
        }
    }

    public void dump() {
        StringBuilder line = new StringBuilder();
        try {
            addColumnNames();
            for (Frame frame : this.frames) {
                var timeMarks = frame.getTimeMarks();
                line.append(timeMarks.getSent()).append(CSV_SEPARATOR);
                line.append(timeMarks.getReceived()).append(CSV_SEPARATOR);
                line.append(timeMarks.getReturned()).append(CSV_SEPARATOR);
                line.append(this.frames.size()).append(CSV_SEPARATOR);
                line.append(this.nodesNum);

                bw.write(line.toString());
                bw.newLine();

                line.setLength(0);
            }

            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addColumnNames() throws IOException {
        String line = "Sent" + CSV_SEPARATOR +
                "Received" + CSV_SEPARATOR +
                "Returned" + CSV_SEPARATOR +
                "Frames" + CSV_SEPARATOR +
                "Nodes";

        bw.write(line);
        bw.newLine();
    }
}
