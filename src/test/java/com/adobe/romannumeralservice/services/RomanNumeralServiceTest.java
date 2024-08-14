package com.adobe.romannumeralservice.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class RomanNumeralServiceTest {

    @InjectMocks
    private RomanNumeralService romanNumeralService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConvertToRomanNumeralQueryWithZero() {
        Exception exception = assertThrows(Exception.class, () -> {
            romanNumeralService.processRomanNumeralRequest("0", null, null);
        });
        assertEquals("Error: Number out of range. must be between 1 and 3999", exception.getMessage());
    }

     @Test
    public void testConvertToRomanNumeralQueryWithArithemeticOperations() {
        Exception exception = assertThrows(Exception.class, () -> {
            romanNumeralService.processRomanNumeralRequest("1+1", null, null);
        });
        assertEquals("Error: Invalid input", exception.getMessage());
    }


    @Test
    public void testConvertToRomanNumeralQueryWithNegativeNumber() {
        Exception exception = assertThrows(Exception.class, () -> {
            romanNumeralService.processRomanNumeralRequest("-5", null, null);
        });
        assertEquals("Error: Invalid input", exception.getMessage());
    }

    @Test
    public void testConvertToRomanNumeralRangeWithBothZeros() {
        Exception exception = assertThrows(Exception.class, () -> {
            romanNumeralService.processRomanNumeralRequest(null, "0", "0");
        });
        assertEquals("Error: Number out of range. must be between 1 and 3999", exception.getMessage());
    }

    @Test
    public void testConvertToRomanNumeralRangeWithOneZero() {
        Exception exception = assertThrows(Exception.class, () -> {
            romanNumeralService.processRomanNumeralRequest(null, "0", "4");
        });
        assertEquals("Error: Number out of range. must be between 1 and 3999", exception.getMessage());
    }

    @Test
    public void testConvertToRomanNumeralRangeWithNegativeNumber() {
        Exception exception = assertThrows(Exception.class, () -> {
            romanNumeralService.processRomanNumeralRequest(null, "-5", "4");
        });
        assertEquals("Error: Invalid input", exception.getMessage());
    }

    @Test
    public void testConvertToRomanNumeralWithLargeNumber() {
        Exception exception = assertThrows(Exception.class, () -> {
            romanNumeralService.processRomanNumeralRequest("4000", null, null);
        });
        assertEquals("Error: Number out of range. must be between 1 and 3999", exception.getMessage());
    }

    @SneakyThrows
    @Test
    public void testConvertToRomanNumeralWithBoundaryValues() {
        assertEquals("I", romanNumeralService.processRomanNumeralRequest("1", null, null).get("output"));
        assertEquals("MMMCMXCIX", romanNumeralService.processRomanNumeralRequest("3999", null, null).get("output"));
    }

    @Test
    public void testConvertToRomanNumeralWithComplexNumbers() {
        assertEquals("CDXLIV", romanNumeralService.processRomanNumeralRequest("444", null, null).get("output"));
        assertEquals("CMXCIX", romanNumeralService.processRomanNumeralRequest("999", null, null).get("output"));
        assertEquals("MMXXIV", romanNumeralService.processRomanNumeralRequest("2024", null, null).get("output"));
    }
}
