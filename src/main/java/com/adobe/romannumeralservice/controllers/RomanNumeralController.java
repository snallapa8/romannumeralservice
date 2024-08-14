package com.adobe.romannumeralservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adobe.romannumeralservice.services.RomanNumeralService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller class to handle HTTP requests for converting integers to Roman numerals.
 */
@RestController
public class RomanNumeralController {

    private static final Logger logger = LoggerFactory.getLogger(RomanNumeralController.class);

    @Autowired
    private RomanNumeralService romanNumeralService;

    /**
     * Displays a welcome message with instructions.
     *
     * @return A string with usage instructions.
     */
    @GetMapping("/")
    public String home() {
        return "Welcome to the Roman Numeral Converter API.<br>" +
               "Use /romannumeral?query=INPUT_NUMBER to convert a specific number to a Roman numeral.<br>" +
               "Alternatively, use /romannumeral?min=INPUT_NUMBER&max=INPUT_NUMBER to convert a range of numbers.";
    }

    /**
     * REST endpoint to convert a number or a range of numbers to Roman numerals.
     *
     * This method handles GET requests to the "/romannumeral" endpoint. It accepts either a single query parameter
     * for converting a specific number to Roman numerals or a range defined by 'min' and 'max' parameters to convert
     * a range of numbers to Roman numerals. The method delegates the processing to the RomanNumeralService.
     *
     * @param query Optional query parameter representing a single number to be converted to Roman numerals.
     * @param min Optional query parameter representing the minimum value of the range to be converted to Roman numerals.
     * @param max Optional query parameter representing the maximum value of the range to be converted to Roman numerals.
     * @return A ResponseEntity containing the conversion results or an error message with the appropriate HTTP status.
     */
    @GetMapping("/romannumeral")
    public ResponseEntity<?> getRomanNumeral(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String min,
        @RequestParam(required = false) String max) {

        logger.info("Received request - query: {}, min: {}, max: {}", query, min, max);
        Map<String, Object> response;
        try {
            response = romanNumeralService.processRomanNumeralRequest(query, min, max);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
