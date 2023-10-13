import java.io.Closeable;
import java.io.IOException;

public class BlackHole implements AutoCloseable {

    public int write(String string){return 1;}

    @Override
    public void close() throws Exception {}

    public String read() throws IOException {
        return "read successfully";
    }

    public static void main(String[] args) {
        try (BlackHole b = new BlackHole()) {
            int writeCounter = 0;
            for (int i = 0; i < 10; i++)
                writeCounter += b.write("245");
            System.out.println( "245 was written " + writeCounter + " times and " + b.read());
        } catch (IOException i) {
            System.out.println("Unable to read BlackHole b");
        } catch (Exception e) {
            System.out.println("Exception thrown when instantiating BlackHole b");
        }
    }
}
