package jub.diogen.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class WordStorageTests {
    @Test
    public void testDocumentsContainingAllWordsFoundCorrectly() {
        WordStorage wordStorage = new WordStorage();
        wordStorage.addEntry("hello", "document1");
        wordStorage.addEntry("hallo", "document1");
        wordStorage.addEntry("hola", "document1");
        wordStorage.addEntry("hallo", "document2");
        wordStorage.addEntry("hola", "document2");
        wordStorage.addEntry("hello", "document3");

        List<String> result =
                wordStorage.getDocumentsContainingAllWords(List.of("hello", "hallo"));

        Assertions.assertTrue(result.contains("document1"));
        Assertions.assertEquals(1, result.size());
    }
}
