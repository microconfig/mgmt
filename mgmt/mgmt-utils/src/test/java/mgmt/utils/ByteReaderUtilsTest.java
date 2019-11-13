package mgmt.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.function.ObjIntConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ByteReaderUtilsTest {
    @Test
    public void readChunkedTestOnZipInputStream() throws IOException {
        byte[] content = new byte[5 * 1024 * 1024];
        new Random(42).nextBytes(content);
        byte[] zip = zipWithOneEntry(content);

        ChunksCollector collector = new ChunksCollector();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))) {
            zis.getNextEntry();
            ByteReaderUtils.readChunked(zis, collector);
        }

        assertArrayEquals(content, collector.collected());
    }

    @Test
    public void readChunkedTestOnSlowStreamMock() throws IOException {
        InputStream mock = mock(InputStream.class);
        byte[] expectedContent = new byte[] {1, 2};
        when(mock.read(any()))
                .thenAnswer(writingToBuffer(new byte[]{1}))
                .thenAnswer(writingToBuffer(new byte[]{2}))
                .thenReturn(-1);
        when(mock.read(any(), anyInt(), anyInt())).thenThrow(new RuntimeException("Not mocked"));

        ChunksCollector collector = new ChunksCollector();
        ByteReaderUtils.readChunked(mock, collector);

        assertArrayEquals(expectedContent, collector.collected());
    }

    private static byte[] zipWithOneEntry(byte[] content) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("entry"));
            zos.write(content);
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    private static Answer<Integer> writingToBuffer(byte[] content) {
        final byte[] contentCopy = content.clone();
        return iom -> {
            final byte[] buffer = iom.getArgument(0);
            ByteBuffer.wrap(buffer).put(contentCopy);
            return contentCopy.length;
        };
    }

    private static class ChunksCollector implements ObjIntConsumer<byte[]> {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        @Override
        public void accept(byte[] bytes, int len) {
            baos.write(bytes, 0, len);
        }

        public byte[] collected() {
            return baos.toByteArray();
        }
    }
}