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
    private final long framesToGenerate;

    @Getter
    private final TokenRingObserver observer;
    private final List<Node> nodes = new ArrayList<>();
    private final AtomicBoolean aliveRingFlag = new AtomicBoolean(true);
    private final Object processorNotifier;

    public TokenRingProcessor(int nodesNum, int framesToGenerate, int tryNumber, String logFileName) {
        this.nodesNum = nodesNum;
        this.framesToGenerate = framesToGenerate;

        this.processorNotifier = new Object();
        this.observer = new TokenRingObserver(tryNumber, logFileName, nodesNum,
                framesToGenerate, this.processorNotifier, aliveRingFlag);
        initNodes();
    }

    public void process() throws InterruptedException {
        List<Thread> threads = this.nodes.stream().map(Thread::new).toList();
        threads.forEach(Thread::start);
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
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private void initNodes() {
        for (int i = 0; i < this.nodesNum - 1; i++) {
            this.nodes.add(new RingNode(i, this.observer, this.aliveRingFlag, this.framesToGenerate, false));
        }

        this.nodes.add(new RingNode(nodesNum - 1, this.observer, this.aliveRingFlag, this.framesToGenerate, true));

        for (int i = 0; i < nodesNum; i++) {
            this.nodes.get(i).setNext(this.nodes.get((i + 1) % this.nodesNum));
        }
    }
}
