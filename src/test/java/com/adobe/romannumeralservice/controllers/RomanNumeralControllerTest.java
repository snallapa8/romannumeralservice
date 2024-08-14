package com.adobe.romannumeralservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RomanNumeralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to the Roman Numeral Converter API.<br>" +
                        "Use /romannumeral?query=INPUT_NUMBER to convert a specific number to a Roman numeral.<br>" +
                        "Alternatively, use /romannumeral?min=INPUT_NUMBER&max=INPUT_NUMBER to convert a range of numbers."));
    }

    @Test
    public void testGetRomanNumeral() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/romannumeral")
                .param("query", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.input").value("1"))
                .andExpect(jsonPath("$.output").value("I"));
    }

    @Test
    public void testGetRomanNumeralRange() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/romannumeral")
                .param("min", "1")
                .param("max", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversions[0].input").value("1"))
                .andExpect(jsonPath("$.conversions[0].output").value("I"))
                .andExpect(jsonPath("$.conversions[1].input").value("2"))
                .andExpect(jsonPath("$.conversions[1].output").value("II"))
                .andExpect(jsonPath("$.conversions[2].input").value("3"))
                .andExpect(jsonPath("$.conversions[2].output").value("III"))
                .andExpect(jsonPath("$.conversions[3].input").value("4"))
                .andExpect(jsonPath("$.conversions[3].output").value("IV"))
                .andExpect(jsonPath("$.conversions[4].input").value("5"))
                .andExpect(jsonPath("$.conversions[4].output").value("V"));
    }

    @Test
    public void testGetRomanNumeralInvalidParameters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/romannumeral")
                .param("query", "1")
                .param("min", "5")
                .param("max", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Invalid parameters. Please provide either 'query' or both 'min' and 'max'."));

        mockMvc.perform(MockMvcRequestBuilders.get("/romannumeral")
                .param("min", "5")
                .param("max", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Invalid range: 'min' should be less than 'max'."));

        mockMvc.perform(MockMvcRequestBuilders.get("/romannumeral"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Invalid parameters. Please provide either 'query' or both 'min' and 'max'."));
    }
}