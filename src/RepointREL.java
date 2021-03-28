import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class RepointREL {
    static final Path translationsDoc = Path.of("MP69Equivalents.txt");
    static Map<Integer, Integer> addressMap;

//    private static final Map<BigInteger, BigInteger> instructionMap = new HashMap<>();

    static final String relToTranslate = "root7/dll/actmanDLL.rel";
    static final String translatedRel = "root7/dll/actmanDL7.rel";

    public static void main(String[] args) throws Exception {
        addressMap = readTranslationsDocToMap();
        byte[] translatedBytes = translateFile();
        if (translatedBytes != null) {
            writeFile(translatedBytes);
        }
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

        translateCode(inputRelBuffer, outputBuffer);
        return translateAddressTable(inputRelBuffer, outputBuffer);
    }

    private static void translateCode(ByteBuffer inputRelBuffer, ByteBuffer outputBuffer) {
        while (inputRelBuffer.hasRemaining()) { //bytebuffer breaks if this is a do-while loop, dunno why, cba fixing
            int nextInt = inputRelBuffer.getInt();

            if (nextInt == 0x3c8000b0) {
                int nextNextInt = inputRelBuffer.getInt();
                if (nextNextInt == 0x38840006) {
                    nextInt = 0x3c800093;
                }
                System.out.println("did the thing");
                outputBuffer.putInt(nextInt);
                outputBuffer.putInt(nextNextInt);
                continue;
            }

            outputBuffer.putInt(nextInt);
            if (nextInt == 0xcb00)
                break;

        }
    }

    private static byte[] translateAddressTable(ByteBuffer inputRelBuffer, ByteBuffer outputBuffer) {
        Set<Integer> unknowns = new HashSet<>();
        while (inputRelBuffer.hasRemaining()) {
            int nextInt = inputRelBuffer.getInt() - 0x80000000;


            if (nextInt > 0 && nextInt < 0x17fffff) {
                if (addressMap.containsKey(nextInt)) {
                    nextInt = addressMap.get(nextInt);
                } else {
                    unknowns.add(nextInt);
                }
            }

//            System.out.printf("%08X\n", nextInt + 0x80000000);
            outputBuffer.putInt(nextInt + 0x80000000);
        }

        if (unknowns.size() == 0) {
            System.out.println("Translation Successful");
            return outputBuffer.array();
        } else {
            System.out.println("Unknown Addresses:");
            unknowns.forEach(u -> System.out.printf("%08X\n", u + 0x80000000));
            return null;
        }
    }

    private static void writeFile(byte[] translatedBytes) throws IOException {
        FileOutputStream newFileOutputStream = new FileOutputStream(translatedRel);
        newFileOutputStream.write(translatedBytes);
        newFileOutputStream.flush();
        newFileOutputStream.close();
    }
}
