package node;

import frame.Frame;
import lombok.extern.slf4j.Slf4j;
import ring.TokenRingObserver;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static frame.Status.*;

@Slf4j
public class RingNode implements Node {
    private static final int NODE_BUFFER_CAPACITY = 100;
    private static final int SLEEP_HANDLING = 5;
    private static final int POLLING_TIME = 2;

    private long framesToGenerate = 1;
    private final long nodeId;
    private final String nodeInfo;
    private Node nextNode;
    private final TokenRingObserver observer;
     private final BlockingQueue<Frame> framesToSend = new ArrayBlockingQueue<>(NODE_BUFFER_CAPACITY);
//    private final ConcurrentLinkedQueue<Frame> framesToSend = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean aliveRingFlag;

    public RingNode(long nodeId, TokenRingObserver observer, AtomicBoolean aliveRingFlag,
                    long framesToGenerate, boolean generate) {
        this.nodeId = nodeId;
        this.observer = observer;
        this.aliveRingFlag = aliveRingFlag;
        this.nodeInfo = "Node[" + nodeId + "]";
        this.framesToGenerate = framesToGenerate;
        if (generate) {
            initFrames();
        }
    }

    /*
    * Нода создает только framesToGenerate фреймов для посылки, если есть флаг generate
    * */
    private void initFrames() {
        for (int i = 0; i < framesToGenerate; i++) {
            long toId;
            do {
                toId = (int) ((observer.getNodesNum()) * Math.random());
            } while (toId == this.nodeId);

            Frame frame = Frame.createFrame(nodeId, toId);
            framesToSend.add(frame);
        }
    }

    @Override
    public void run() {
        while (this.aliveRingFlag.get()) {
            doWork();
        }
    }

    private void doWork() {
        Frame frame;
        if (framesToSend.size() != 0) {
            try {
                frame = this.framesToSend.poll(POLLING_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.info("{} Caught InterruptedException while getting frame", nodeInfo, e);
                throw new RuntimeException(e);
            }
//            frame = this.framesToSend.poll();

            if (frame != null) {
                sleepHandle();
                handleFrame(frame);
            }
        }
    }

    private void sleepHandle() {
        try {
            log.info("{} Handling frame, sleep {}", nodeInfo, SLEEP_HANDLING);
            Thread.sleep(SLEEP_HANDLING);
        } catch (InterruptedException e) {
            log.info("{} Caught InterruptedException while handling frame", nodeInfo, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleFrame(Frame frame) {
        if (frame.getFromId() == nodeId && frame.getStatus().isReceived()) {
            log.info("{} Registering returned frame with id: {}", nodeInfo, frame.getUUID());
            this.observer.registerFrame(frame);
            forward(frame);
        } else if (frame.getFromId() == nodeId && frame.getStatus().isFlying()) {
            log.info("{} Start sending frame", nodeInfo);
            frame.getTimeMarks().setSent();
            forward(frame);
        } else if (frame.getToId() == nodeId && frame.getStatus().isFlying()) {
            log.info("{} Frame received with id: {}", nodeInfo, frame.getUUID());
            frame.setStatus(RECEIVED);
            frame.getTimeMarks().setReceived();
            forward(frame);
        } else {
            forward(frame);
        }
    }

    @Override
    public void forward(Frame frame) {
        this.nextNode.receive(frame);
    }

    @Override
    public void receive(Frame frame) {
        boolean isAddedToBuffer = framesToSend.offer(frame);
        if (!isAddedToBuffer) {
            log.info("{} Buffer is overflowed, sending frame to the next node", nodeInfo);
            forward(frame);
        }
    }

    @Override
    public void setNext(Node node) {
        this.nextNode = node;
    }
}
