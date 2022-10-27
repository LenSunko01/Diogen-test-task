package jub.diogen.controller;

import jub.diogen.storage.WordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    @Autowired
    private WordStorage wordStorage;

    @GetMapping(path="/all")
    public @ResponseBody List<String> searchAll(@RequestBody List<String> request) {
        return wordStorage.getDocumentsContainingAllWords(request);
    }

    @GetMapping(path="/any")
    public @ResponseBody Map<String, List<String>> searchAny(@RequestBody List<String> request) {
        return wordStorage.getDocumentsForWords(request);
    }
}
