package jub.diogen.view;

import java.util.List;

public class WordView {
    private final String word;

    private final String formattedDocumentsList;

    public WordView(String word, List<String> documentsList) {
        this.word = word;
        this.formattedDocumentsList = documentsList.isEmpty() ? "-" : String.join(", ", documentsList);
    }

    public String getWord() {
        return word;
    }

    public String getFormattedDocumentsList() {
        return formattedDocumentsList;
    }
}
