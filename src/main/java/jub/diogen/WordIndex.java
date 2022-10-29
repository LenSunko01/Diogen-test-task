package jub.diogen;

import jub.diogen.constants.Constants;
import jub.diogen.storage.WordStorage;
import jub.diogen.utils.EnvironmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

/**
   Extracts words from all documents in a folder and saves information about which words belong to which documents
   to a storage.
*/
@Component
public class WordIndex {
    private static final Logger logger
            = LoggerFactory.getLogger(WordIndex.class);

    private final WordStorage wordStorage;
    private final EnvironmentUtil environmentUtil;
    @Autowired
    public WordIndex(WordStorage wordStorage, EnvironmentUtil environmentUtil) {
        this.wordStorage = wordStorage;
        this.environmentUtil = environmentUtil;
        indexWords();
    }

    /** Collects and saves information about which words belong to which documents */
    private void indexWords() {
        logger.info("Started indexing words...");
        Path directory;
        try {
            directory = getDirectory();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Stream<Path> stream = Files.list(directory)) {
            stream.filter(file -> !Files.isDirectory(file)).forEach(this::indexWordsInFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to traverse through directory " + directory, e);
        }
        logger.info("Finished indexing words");
    }

    /**
       Returns path to the folder containing set of texts to be processed.
       The path is taken from environmental variable if it is set.
       If variable is not set, the path to example folder in resources is used.
     */
    private Path getDirectory() throws FileNotFoundException {
        String directoryPath = environmentUtil.getVariable(Constants.DOCUMENTS_DIRECTORY_ENV_VARIABLE_NAME);
        if (directoryPath == null) {
            directoryPath = ResourceUtils.getFile("classpath:example").getPath();
        }
        Path directory = Path.of(directoryPath);
        if (!Files.exists(directory)) {
            throw new FileNotFoundException("Directory not found: " + directory);
        }
        return directory;
    }

    /** Extracts words from a single file and saves them in storage with file name as containing document name */
    private void indexWordsInFile(Path file) {
        String filename = com.google.common.io.Files.getNameWithoutExtension(file.getFileName().toString());
        try (Stream<String> stream = Files.lines(file)) {
                stream.flatMap(line -> Arrays.stream(line.strip().split("\\s+"))).distinct()
                    .forEach(word -> { if (!word.isEmpty()) { wordStorage.addEntry(word, filename); } });
        } catch (IOException e) {
            throw new RuntimeException("Failed to read words in file " + file, e);
        }
    }
}
