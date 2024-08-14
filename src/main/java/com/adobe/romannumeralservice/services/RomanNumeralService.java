package com.adobe.romannumeralservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for converting integers to Roman numerals.
 */
@Service
public class RomanNumeralService {

    private static final Logger logger = LoggerFactory.getLogger(RomanNumeralService.class);

    // Map containing integer values and their corresponding Roman numeral representations.
    private static final Map<Integer, String> romanNumerals = new LinkedHashMap<>();

    static {
        romanNumerals.put(1000, "M");
        romanNumerals.put(900, "CM");
        romanNumerals.put(500, "D");
        romanNumerals.put(400, "CD");
        romanNumerals.put(100, "C");
        romanNumerals.put(90, "XC");
        romanNumerals.put(50, "L");
        romanNumerals.put(40, "XL");
        romanNumerals.put(10, "X");
        romanNumerals.put(9, "IX");
        romanNumerals.put(5, "V");
        romanNumerals.put(4, "IV");
        romanNumerals.put(1, "I");
    }


    /**
     * Processes the request to convert a single number or a range of numbers to Roman numerals.
     *
     * @param query The query parameter representing a single number to be converted. This is mutually exclusive with 'min' and 'max'.
     * @param min The minimum value of the range to be converted to Roman numerals. This is used together with 'max'.
     * @param max The maximum value of the range to be converted to Roman numerals. This is used together with 'min'.
     * @return A map containing the input and output data, where the output is either a single Roman numeral or a list of conversions for the range.
     * @throws Exception if the input parameters are invalid or mutually exclusive.
     */
    public Map<String, Object> processRomanNumeralRequest(String query, String min, String max) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            if (query != null && min == null && max == null) {
                // Validate the query parameter and convert to Roman numeral
                validateInteger(query);
                int queryValue = Integer.parseInt(query);
                String roman = convertToRoman(queryValue);
                response.put("input", query);
                response.put("output", roman);
            } else if (query == null && min != null && max != null) {
                // Validate the range parameters and convert the range to Roman numerals
                validateRangeParams(min, max);
                int minValue = Integer.parseInt(min);
                int maxValue = Integer.parseInt(max);
                response.put("conversions", convertRangeToRoman(minValue, maxValue));
            } else {
                // Handle cases where parameters are invalid or mutually exclusive
                throw new IllegalArgumentException("Invalid parameters. Please provide either 'query' or both 'min' and 'max'.");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error: " + e.getMessage());
        }


        return response;
    }

    /**
     * Converts an integer to its corresponding Roman numeral.
     *
     * @param number The integer to convert (must be between 1 and 3999).
     * @return The Roman numeral representation of the integer.
     * @throws IllegalArgumentException if the number is out of the valid range (1-3999).
     */
    private String convertToRoman(int number) {
        logger.info("Converting number: {}", number);
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, String> entry : romanNumerals.entrySet()) {
            while (number >= entry.getKey()) {
                result.append(entry.getValue());
                number -= entry.getKey();
            }
        }
        logger.info("Converted number to Roman numeral: {}", result);
        return result.toString();
    }


    /**
     * Converts a range of integers to their corresponding Roman numerals in parallel.
     *
     * @param min The minimum value of the range to convert.
     * @param max The maximum value of the range to convert.
     * @return A list of maps, each containing an "input" integer and its corresponding "output" Roman numeral.
     */
    private List<Map<String, String>> convertRangeToRoman(int min, int max) {
        List<CompletableFuture<Map<String, String>>> futures = new ArrayList<>();

        // Process each number in the range in parallel using CompletableFuture
        for (int i = min; i <= max; i++) {
            final int number = i;
            futures.add(CompletableFuture.supplyAsync(() -> {
                Map<String, String> conversion = new LinkedHashMap<>();
                conversion.put("input", String.valueOf(number));
                conversion.put("output", convertToRoman(number));
                return conversion;
            }));
        }

        // Wait for all futures to complete, then collect and sort the results
        return futures
          .stream()
          .map(CompletableFuture::join)  // Wait for each task to complete
          .sorted(Comparator.comparing(map -> Integer.parseInt(map.get("input"))))  // Sort by input value
          .collect(Collectors.toList());
    }

    /**
     * This method validates the provided query parameter by checking if it is a valid integer.
     *
     * @param queryValue The value of the query parameter to be validated.
     * @throws IllegalArgumentException if the query parameter is not a valid integer. The exception message is customized
     *         to indicate that the error occurred in the query parameter.
     */
    private void validateQueryParameter(String queryValue) throws IllegalArgumentException{
        try {
            validateInteger(queryValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid query parameter: " + e.getMessage());
        }
    }

    /**
     * This method validates the provided range parameters, ensuring that both are valid integers
     * and that the minimum value is less than the maximum value.
     *
     * @param minValue The string representing the minimum value of the range.
     * @param maxValue The string representing the maximum value of the range.
     * @throws IllegalArgumentException if either parameter is not a valid integer or if minValue is not less than maxValue.
     */
    private void validateRangeParams(String minValue, String maxValue) throws IllegalArgumentException {
        // Validate minValue and maxValue individually
        validateInteger(minValue);
        validateInteger(maxValue);

        int min = Integer.parseInt(minValue);
        int max = Integer.parseInt(maxValue);

        // Ensure min is less than max
        if (min >= max) {
            throw new IllegalArgumentException("Invalid range: 'min' should be less than 'max'.");
        }
    }


    /**
     * This method validates whether the provided input is a valid positive integer within a specific range.
     *
     * @param input The input string to be validated.
     * @throws IllegalArgumentException if the input is not a valid integer or falls outside the allowed range.
     */
    private void validateInteger(String input) throws IllegalArgumentException {
        // Check if the input matches the pattern for a valid positive integer
        if (!input.matches("^(0|[1-9]\\d*)$")) {
            throw new IllegalArgumentException("Invalid input");
        }

        // Parse the input and check if the number is within the valid range
        int number = Integer.parseInt(input);
        if (number < 1 || number > 3999) {
            throw new IllegalArgumentException("Number out of range. must be between 1 and 3999");
        }
    }
}
