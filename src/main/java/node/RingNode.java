package node;

import frame.Frame;
import ring.TokenRingObserver;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RingNode implements Node {
    private static final int NODE_BUFFER_CAPACITY = 20;

    private final long nodeId;
    private final RingNode nextNode;
    private final TokenRingObserver observer;
    private final BlockingQueue<Frame> framesToSend = new ArrayBlockingQueue<>(NODE_BUFFER_CAPACITY);

    public RingNode(long nodeId, RingNode nextNode, TokenRingObserver observer) {
        this.nodeId = nodeId;
        this.nextNode = nextNode;
        this.observer = observer;
    }

    @Override
    public void run() {

    }

    @Override
    public void sendFrame(Frame frame) {
        // TODO
        nextNode.receiveFrame(frame);
    }

    @Override
    public void receiveFrame(Frame frame) {
        // TODO
        this.framesToSend.add(frame);
    }
}
