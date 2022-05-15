package ring;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import node.Node;
import node.RingNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class TokenRingProcessor {
    private final int nodesNum;
    private final long framesToGeneratePerNode;

    @Getter
    private final TokenRingObserver observer;
    private final List<Node> nodes = new ArrayList<>();
    private final AtomicBoolean aliveRingFlag = new AtomicBoolean(true);
    private final Object processorNotifier;

    public TokenRingProcessor(int nodesNum, int framesToGeneratePerNode, String logFileName) {
        this.nodesNum = nodesNum;
        this.framesToGeneratePerNode = framesToGeneratePerNode;

        this.processorNotifier = new Object();
        this.observer = new TokenRingObserver(logFileName, nodesNum,
                framesToGeneratePerNode * (long) nodesNum, this.processorNotifier, aliveRingFlag);
        initNodes();
    }

    public void process() {
        // this.aliveRingFlag.set(true);
        this.nodes.stream().map(Thread::new).forEach(Thread::start);
        while (observer.isAllDataRegistered()) {
            synchronized (this.processorNotifier) {
               try {
                   this.processorNotifier.wait(100);
               } catch (InterruptedException e) {
                   log.error("Processor: failed to wait for capturing frames", e);
                   break;
               }
            }
        }
    }

    private void initNodes() {
        for (int i = 0; i < this.nodesNum; i++) {
            this.nodes.add(new RingNode(i, this.observer, this.aliveRingFlag, this.framesToGeneratePerNode));
        }

        for (int i = 0; i < nodesNum; i++) {
            this.nodes.get(i).setNext(this.nodes.get((i + 1) % this.nodesNum));
        }
    }
}
