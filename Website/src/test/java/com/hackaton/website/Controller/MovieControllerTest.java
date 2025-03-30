package com.hackaton.website.Controller;

import com.hackaton.website.Entity.Movie;
import com.hackaton.website.Service.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Test
    void testListMovies() throws Exception {
        Mockito.when(movieService.readMoviesFromCSV()).thenReturn(Collections.singletonList(
            new Movie(1L, "Test Movie", "Action", "Action,Adventure", 8.5, 1000, 2022, 8.7)
        ));

        mockMvc.perform(get("/movies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Test Movie"));
    }
}
