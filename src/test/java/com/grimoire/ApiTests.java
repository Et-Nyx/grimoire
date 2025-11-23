package com.grimoire;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grimoire.common.model.CharacterSheet;
import com.grimoire.common.model.Attributes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetSheet() throws Exception {
        CharacterSheet sheet = new CharacterSheet();
        sheet.setId("api-test-char");
        sheet.setName("API Tester");
        sheet.setSystem("Tormenta20");
        sheet.setAttributes(new Attributes(10, 10, 10, 10, 10, 10));

        String json = objectMapper.writeValueAsString(sheet);

        // Create
        mockMvc.perform(post("/sheet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("API Tester"));

        // Get
        mockMvc.perform(get("/sheet/api-test-char"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.system").value("Tormenta20"));
    }
}
