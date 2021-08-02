import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class SearchHexes {
    public static void main(String[] args) throws IOException {
//        search("C:\\Users\\Max\\Desktop\\MarioParty67Java\\root6", new short[]{0x80,0x8,7,0x28});
//        System.out.println("\n");
//        search("C:\\Users\\Max\\Desktop\\MarioParty67Java\\root6", new short[]{'i','t','e','m','h','o','o','k','_','R'});
        search("C:\\Users\\Max\\Desktop\\MarioParty67Java\\root7", new short[]{0x80,0x04,0x2e,0xd8});
//        System.out.println("\n");
//        search("C:\\Users\\Max\\Desktop\\MarioParty67Java\\root7", new short[]{0x38,0x60,0,0x6a,0x48,0,0,1});
//        search("C:\\Users\\Max\\Desktop\\MarioParty67Java\\root6", new short[]{'i','t','e','m','h','o','o','k'});
    }

    private static void search(String path, short[] search) throws IOException {
        File root = new File(path);

        for (File directory : root.listFiles()) {
            for (File f : directory.listFiles()) {
                try (RandomAccessFile aFile = new RandomAccessFile(f.getPath(), "r")) {

                    FileChannel inChannel = aFile.getChannel();
                    long fileSize = inChannel.size();

                    ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
                    inChannel.read(buffer);
                    buffer.flip();

                    int streak = 0;
//                    short wildCard = 0xfff;
                    for (int i = 0; i < fileSize; i++) {
                        if (streak == search.length) {
                            System.out.println(f.getAbsolutePath());
                            System.out.println(i);
//                            if (wildCard != 0xfff)
//                                System.out.printf("%02X\n", wildCard);
                            streak = 0;
                            break;      //comment this out to get multiple hits per file
                        }
                        byte nextByte = buffer.get();

                        if (search[streak] == 0xfff) {  //wildcard
                            streak++;
//                            wildCard = nextByte;
                            continue;
                        }

                        if (nextByte == (byte) search[streak]) {
                            streak++;
                        } else {
                            streak = 0;
                        }
                    }

                    inChannel.close();
                } catch (BufferUnderflowException ignore) {
                    System.out.println("oof");
                }
            }
        }
    }
}

