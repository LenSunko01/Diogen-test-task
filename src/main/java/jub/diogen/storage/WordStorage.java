package jub.diogen.storage;

import org.springframework.stereotype.Component;
import java.util.*;

/** In-memory storage of words and corresponding containing document names */
@Component
public class WordStorage {
    private final Map<String, List<String>> wordToDocuments = new HashMap<>();

    /** Adds document name to the list of document names containing given word */
    public void addEntry(String word, String document) {
        wordToDocuments.putIfAbsent(word, new ArrayList<>());
        wordToDocuments.get(word).add(document);
    }

    /** Returns list of document names containing given word */
    public List<String> getDocumentsForWord(String word) {
        return wordToDocuments.getOrDefault(word, new ArrayList<>());
    }

    /** Returns map with word as key and list of document names containing word as value */
    public Map<String, List<String>> getDocumentsForWords(List<String> words) {
        Map<String, List<String>> result = new HashMap<>();
        for (var word : words) {
            result.put(word, getDocumentsForWord(word));
        }
        return result;
    }

    /** Returns names of documents which contain all passed words */
    public List<String> getDocumentsContainingAllWords(List<String> words) {
        List<String> documentsNameIntersection = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (i == 0) {
                documentsNameIntersection.addAll(getDocumentsForWord(words.get(i)));
            } else {
                documentsNameIntersection.retainAll(getDocumentsForWord(words.get(i)));
            }
            if (documentsNameIntersection.isEmpty()) {
                break;
            }
        }
        return documentsNameIntersection;
    }

    public int size() {
        return wordToDocuments.size();
    }
}