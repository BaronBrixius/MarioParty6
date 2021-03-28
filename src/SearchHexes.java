import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class SearchHexes {
    public static void main(String[] args) throws IOException {
        search("C:\\Users\\Max\\Desktop\\MarioParty69Java\\root6", new short[]{0x3c, 0x80, 0, 0xfff, 0x38, 0x84, 0, 6});
//        System.out.println("\n");
//        search("C:\\Users\\Max\\Desktop\\MarioParty69Java\\root6", new short[]{0x48, 0, 0, 1, 0x38, 0x60, 0x00});
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
                    byte value = -1;
                    for (int i = 0; i < fileSize; i++) {
                        if (streak == search.length) {
                            System.out.println(f.getAbsolutePath());
                            System.out.printf("%02X\n", value);
                            streak = 0;
//                            break;
                        }
                        byte nextByte = buffer.get();

                        if (search[streak] == 0xfff) {  //wildcard
                            streak++;
                            value = nextByte;
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

