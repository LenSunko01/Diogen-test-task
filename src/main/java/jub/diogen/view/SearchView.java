package jub.diogen.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jub.diogen.storage.WordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringComponent
@Route(value = "")
@PageTitle("Diogen")
public class SearchView extends VerticalLayout {
    Grid<WordView> grid = new Grid<>(WordView.class);
    TextField filterText = new TextField();

    enum SearchOption {
        SHOW_DOCUMENTS_FOR_EACH_SEARCH_WORD,
        SHOW_DOCUMENTS_CONTAINING_ALL_WORDS;

        public static String prettyName(SearchOption option) {
            switch (option) {
                case SHOW_DOCUMENTS_CONTAINING_ALL_WORDS: return "Show documents containing all words";
                case SHOW_DOCUMENTS_FOR_EACH_SEARCH_WORD: return "Show documents for each search word";
                default: throw new IllegalArgumentException("Option not supported");
            }
        }
    }

    ComboBox<SearchOption> searchOptions = new ComboBox<>("Search option");

    private final WordStorage storage;

    @Autowired
    public SearchView(WordStorage storage) {
        this.storage = storage;
        addClassName("search-view");
        setSizeFull();
        configureGrid();
        configureFilterText();
        configureSearchOptions();
        add(filterText, searchOptions, getContent());
        updateList();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid);
        content.setFlexGrow(2, grid);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.addClassNames("document-grid");
        grid.setSizeFull();
        grid.setColumns("word");
        grid.addColumn("formattedDocumentsList").setHeader("Containing documents");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void configureFilterText() {
        filterText.setPlaceholder("Filter by words...");
        filterText.setClearButtonVisible(true);
        filterText.setWidthFull();
        filterText.setLabel("Search words");
        filterText.setHelperText("Format: word1 word2 word3");
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
    }

    private void configureSearchOptions() {
        searchOptions.setItems(SearchOption.SHOW_DOCUMENTS_CONTAINING_ALL_WORDS, SearchOption.SHOW_DOCUMENTS_FOR_EACH_SEARCH_WORD);
        searchOptions.setRenderer(new TextRenderer<>(SearchOption::prettyName));
        searchOptions.setItemLabelGenerator(SearchOption::prettyName);
        searchOptions.setValue(SearchOption.SHOW_DOCUMENTS_CONTAINING_ALL_WORDS);
        searchOptions.addValueChangeListener(e -> updateList());
        searchOptions.setWidthFull();
    }

    private List<String> parseFilterWords() {
        List<String> words = new ArrayList<>();
        String wordsString = filterText.getValue();
        if (wordsString != null && !wordsString.trim().isEmpty()) {
            words = Arrays.asList(wordsString.trim().split("\\s+"));
        }
        return words;
    }
    private void updateList() {
        if (searchOptions.getValue().equals(SearchOption.SHOW_DOCUMENTS_FOR_EACH_SEARCH_WORD)) {
            grid.setItems(storage.getDocumentsForWords(parseFilterWords())
                    .entrySet()
                    .stream()
                    .map(entry -> new WordView(entry.getKey(), entry.getValue()))
                    .toList()
            );
        } else {
            List<String> words = parseFilterWords();
            List<String> documents = storage.getDocumentsContainingAllWords(words);
            grid.setItems(words.stream().map(word -> new WordView(word, documents)).toList());
        }
    }
}