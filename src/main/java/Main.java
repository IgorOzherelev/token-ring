import ring.TokenRingProcessor;

public class Main {
    public static void main(String[] args) {
        TokenRingProcessor processor = new TokenRingProcessor(2, 1, "result21");
        processor.process();
        processor.getObserver().dump();
    }
}
