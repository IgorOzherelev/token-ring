package node;

import frame.Frame;

public interface Node extends Runnable {
    void sendFrame(Frame frame);
    void receiveFrame(Frame frame);
}
