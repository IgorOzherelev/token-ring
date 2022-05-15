import ring.TokenRingProcessor;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int maxFramesToGeneratePerNode = 20;
        int maxNodesNum = 20;
        for (int j = 3; j < maxNodesNum; j++) {
            for (int i = 1; i < maxFramesToGeneratePerNode; i++) {
                TokenRingProcessor processor = new TokenRingProcessor(j, i,
                        "nodes"+ j + "gen" + i);
                processor.process();
                processor.getObserver().dump();
            }
        }
    }
}
