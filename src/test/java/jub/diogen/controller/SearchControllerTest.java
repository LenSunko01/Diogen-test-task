package jub.diogen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jub.diogen.storage.WordStorage;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(SearchController.class)
public class SearchControllerTest {
    @MockBean
    private WordStorage wordStorage;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void queryAllEndpoint_returnsCorrectResult() throws Exception {
        when(wordStorage.getDocumentsContainingAllWords(anyList()))
                .thenReturn(List.of("document1", "document2"));
        mockMvc.perform(MockMvcRequestBuilders.get("/all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(List.of("word1", "word2")))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(asJsonString(List.of("document1", "document2"))));
    }

    @Test
    public void queryAnyEndpoint_returnsCorrectResult() throws Exception {
        when(wordStorage.getDocumentsForWords(anyList()))
                .thenReturn(Map.of("word1", List.of("document1"), "word2", List.of("document2")));
        mockMvc.perform(MockMvcRequestBuilders.get("/any")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(List.of("word1", "word2")))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(Map.of("word1", List.of("document1"), "word2", List.of("document2")))));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
