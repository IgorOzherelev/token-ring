import ring.TokenRingProcessor;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int maxFramesToGenerate = 30;
        int maxNodesNum = 20;
        int triesNum = 10;
        for (int k = 0; k < triesNum; k++) {
            for (int j = 2; j < maxNodesNum + 1; j++) {
                for (int i = 1; i < maxFramesToGenerate + 1; i++) {
                    TokenRingProcessor processor = new TokenRingProcessor(j, i, k,
                            "nodes"+ j + "gen" + i);
                    processor.process();
                    processor.getObserver().dump();
                }
            }
        }

//        TokenRingProcessor processor = new TokenRingProcessor(20, 30,
//                "nodes"+ 20 + "gen" + 30);
//        processor.process();
//        processor.getObserver().dump();
    }
}
