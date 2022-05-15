package node;

import frame.Frame;

public interface Node extends Runnable {
    void handleFrame(Frame frame);
    void forward(Frame frame);
    void receive(Frame frame);
    void setNext(Node node);
}
