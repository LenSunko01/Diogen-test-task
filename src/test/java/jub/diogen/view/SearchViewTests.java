package jub.diogen.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import jub.diogen.storage.WordStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class SearchViewTests {
    private WordStorage storage;
    private SearchView searchView;

    @BeforeEach
    public void setUp() {
        storage = new WordStorage();
        searchView = new SearchView(storage);
    }

    @Test
    public void filterTextIsEmpty_emptyGridShown() {
        searchView.filterText.setValue("");
        Grid<WordView> grid = searchView.grid;

        boolean gridHasElements = ((ListDataProvider<WordView>) grid.getDataProvider()).getItems().iterator().hasNext();

        Assertions.assertFalse(gridHasElements);
    }

    @Test
    public void filterTextContainsOnlySpaceCharacters_emptyGridShown() {
        searchView.filterText.setValue(" \t  ");
        Grid<WordView> grid = searchView.grid;

        boolean gridHasElements = ((ListDataProvider<WordView>) grid.getDataProvider()).getItems().iterator().hasNext();

        Assertions.assertFalse(gridHasElements);
    }

    @Test
    public void searchWordWithNoContainingDocuments_columnContainingDocumentsEmpty() {
        searchView.filterText.setValue("word");
        Grid<WordView> grid = searchView.grid;

        String containingDocumentsColumnValue =
                ((ListDataProvider<WordView>) grid.getDataProvider()).getItems().iterator().next().getFormattedDocumentsList();

        Assertions.assertEquals("-", containingDocumentsColumnValue);
    }

    @Test
    public void selectShowDocumentsContainingAllWordsOption_documentsContainingAllWordsShown() {
        storage.addEntry("word1", "document1");
        storage.addEntry("word2", "document1");
        storage.addEntry("word2", "document2");
        searchView.filterText.setValue("word1 word2");
        searchView.searchOptions.setValue(SearchView.SearchOption.SHOW_DOCUMENTS_CONTAINING_ALL_WORDS);
        Grid<WordView> grid = searchView.grid;

        List<WordView> words = List.copyOf(((ListDataProvider<WordView>) grid.getDataProvider()).getItems());

        Assertions.assertTrue(words.get(0).getWord().equals("word1")
                && words.get(0).getFormattedDocumentsList().equals("document1"));
        Assertions.assertTrue(words.get(1).getWord().equals("word2")
                && words.get(1).getFormattedDocumentsList().equals("document1"));
    }

    @Test
    public void selectShowDocumentsForEachSearchWordOption_documentsForEachSearchWordShown() {
        storage.addEntry("word1", "document1");
        storage.addEntry("word2", "document1");
        storage.addEntry("word2", "document2");
        searchView.filterText.setValue("word1 word2");
        searchView.searchOptions.setValue(SearchView.SearchOption.SHOW_DOCUMENTS_FOR_EACH_SEARCH_WORD);
        Grid<WordView> grid = searchView.grid;

        List<WordView> words = List.copyOf(((ListDataProvider<WordView>) grid.getDataProvider()).getItems());

        Assertions.assertTrue(words.get(0).getWord().equals("word1")
                && words.get(0).getFormattedDocumentsList().equals("document1"));
        Assertions.assertTrue(words.get(1).getWord().equals("word2")
                && words.get(1).getFormattedDocumentsList().equals("document1, document2"));
    }

    @Test
    public void extraSpacesInFilterText_wordsWithoutSpacesAreSearched() {
        searchView.filterText.setValue("   word1   word2     ");
        searchView.searchOptions.setValue(SearchView.SearchOption.SHOW_DOCUMENTS_FOR_EACH_SEARCH_WORD);
        Grid<WordView> grid = searchView.grid;

        List<WordView> words = List.copyOf(((ListDataProvider<WordView>) grid.getDataProvider()).getItems());

        Assertions.assertEquals("word1", words.get(0).getWord());
        Assertions.assertEquals("word2", words.get(1).getWord());
    }
}
