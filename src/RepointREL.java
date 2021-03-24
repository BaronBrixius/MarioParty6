import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class RepointREL {
    static final Path translationsDoc = Path.of("MP69Equivalents.txt");
    static Map<Integer, Integer> mapMp7ToMp6;

    static final String relToTranslate = "safdll.rel";
    static final String translatedRel = "safdll7.rel";

    public static void main(String[] args) throws Exception {
        mapMp7ToMp6 = readTranslationsDocToMap();
        byte[] translatedBytes = translateFile();
        writeFile(translatedBytes);
    }

    private static Map<Integer, Integer> readTranslationsDocToMap() throws IOException {
        Map<Integer, Integer> map = new TreeMap<>();
        for (String line : Files.readAllLines(translationsDoc)) {
            if (line.contains("reference"))    //column headers
                continue;
            if (line.length() < 16)
                break;

            Integer mp6 = Integer.parseInt(line.substring(1, 8), 16);
            Integer mp7 = Integer.parseInt(line.substring(10, 17), 16);
            map.put(mp7, mp6);
        }
        return map;
    }

    private static byte[] translateFile() throws IOException {
        byte[] relBytes = Files.readAllBytes(Path.of(relToTranslate));
        ByteBuffer inputRelBuffer = ByteBuffer.wrap(relBytes);

        byte[] translatedBytes = new byte[relBytes.length];
        ByteBuffer outputBuffer = ByteBuffer.wrap(translatedBytes);

        while (inputRelBuffer.hasRemaining()) {
            int nextInt = inputRelBuffer.getInt() - 0x80000000;

            if (mapMp7ToMp6.containsKey(nextInt)) {
                nextInt = mapMp7ToMp6.get(nextInt);
            }

//            System.out.printf("%08X\n", nextInt + 0x80000000);
            outputBuffer.putInt(nextInt + 0x80000000);
        }
        return translatedBytes;
    }

    private static void writeFile(byte[] translatedBytes) throws IOException {
        FileOutputStream newFileOutputStream = new FileOutputStream(translatedRel);
        newFileOutputStream.write(translatedBytes);
        newFileOutputStream.flush();
        newFileOutputStream.close();
    }
}
