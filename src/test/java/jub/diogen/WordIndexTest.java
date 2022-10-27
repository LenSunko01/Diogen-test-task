package jub.diogen;

import jub.diogen.storage.WordStorage;
import jub.diogen.utils.EnvironmentUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertThrows;

public class WordIndexTest {
    private WordStorage storage;

    @BeforeEach
    public void setUp() {
        storage = new WordStorage();
    }

    @Test
    public void directoryDoesNotExist_assertThrowsRuntimeException() {
        String wordsDirectory = "some_non_existent_path";
        String expectedMessage = "Directory not found: " + wordsDirectory;

        Exception exception = assertThrows(RuntimeException.class, () ->
            new WordIndex(storage, testEnvironment(wordsDirectory)));
        String actualMessage = exception.getCause().getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void simpleTestWordsInFile_assertParsesWordsCorrectly(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("file1.txt");
        Files.write(file, "word1 word2".getBytes());

        new WordIndex(storage, testEnvironment(directory.toString()));

        Assertions.assertEquals("file1", storage.getDocumentsForWord("word1").get(0));
        Assertions.assertEquals("file1", storage.getDocumentsForWord("word2").get(0));
    }

    @Test
    public void wordPresentInMultipleFiles_assertFoundWordInAllFiles(@TempDir Path directory) throws IOException {
        Path file1 = directory.resolve("file1.txt");
        Files.write(file1, "word1 word2".getBytes());
        Path file2 = directory.resolve("file2.txt");
        Files.write(file2, "word3   word1 word3".getBytes());
        Path file3 = directory.resolve("file3.txt");
        Files.write(file3, "word3".getBytes());

        new WordIndex(storage, testEnvironment(directory.toString()));

        Assertions.assertTrue(storage.getDocumentsForWord("word1").contains("file1"));
        Assertions.assertTrue(storage.getDocumentsForWord("word1").contains("file2"));
        Assertions.assertFalse(storage.getDocumentsForWord("word1").contains("file3"));
    }

    @Test
    public void multipleSpaceAndNewLineSymbolsBetweenWords_assertParsesWordsCorrectly(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("file1.txt");
        Files.write(file, "\n   word1\t\n word2 \n word3\t  \n".getBytes());

        new WordIndex(storage, testEnvironment(directory.toString()));

        Assertions.assertEquals("file1", storage.getDocumentsForWord("word1").get(0));
        Assertions.assertEquals("file1", storage.getDocumentsForWord("word2").get(0));
        Assertions.assertEquals("file1", storage.getDocumentsForWord("word3").get(0));
    }

    @Test
    public void noWordsInFile_assertNoWordsParsed(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("file1.txt");
        Files.write(file, "  \t  \n\t  ".getBytes());

        new WordIndex(storage, testEnvironment(directory.toString()));

        Assertions.assertEquals(0, storage.size());
    }

    @Test
    public void manyFilesManyWords_assertAllWordsFoundCorrectly(@TempDir Path directory) throws IOException {
        Path file1 = directory.resolve("file1.txt");
        Files.write(file1, " word1   word2  \t  \n word2      word6  word7".getBytes());
        Path file2 = directory.resolve("file2.txt");
        Files.write(file2, " word3  \n word2 word4".getBytes());
        Path file3 = directory.resolve("file3.txt");
        Files.write(file3, "word8 word8 word5   word6".getBytes());

        new WordIndex(storage, testEnvironment(directory.toString()));

        Assertions.assertEquals(8, storage.size());
    }

    private EnvironmentUtil testEnvironment(String variableValue) {
        return new EnvironmentUtil() {
            @Override
            public String getVariable(String name) {
                return variableValue;
            }
        };
    }
}
