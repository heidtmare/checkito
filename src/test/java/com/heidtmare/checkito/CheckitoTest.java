package com.heidtmare.checkito;

import static com.heidtmare.checkito.Checkito.Null;
import static com.heidtmare.checkito.Checkito.blank;
import static com.heidtmare.checkito.Checkito.empty;
import static com.heidtmare.checkito.Checkito.greaterThan;
import static com.heidtmare.checkito.Checkito.greaterThanOrEqualTo;
import static com.heidtmare.checkito.Checkito.is;
import static com.heidtmare.checkito.Checkito.lessThan;
import static com.heidtmare.checkito.Checkito.lessThanOrEqualTo;
import static com.heidtmare.checkito.Checkito.not;
import static com.heidtmare.checkito.Checkito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.heidtmare.checkito.Checkito.All;
import com.heidtmare.checkito.Checkito.None;
import com.heidtmare.checkito.Checkito.Not;
import com.heidtmare.checkito.Checkito.Validation;
import com.heidtmare.checkito.Checkito.When;

/**
 *
 * @author cmheidt
 */
public class CheckitoTest {

    @Test
    public void testWhen() {
        When actual = when(null, Null());

        assertNotNull(actual);
        assertTrue(actual instanceof When);
    }

    @Test
    public void testWhen_then() {
        AtomicBoolean success = new AtomicBoolean(false);
        when(null, Null()).then(() -> {
            success.set(true);
        });

        assertTrue(success.get());

        when("NOT NULL", Null()).then(() -> {
            success.set(false);
        });

        assertTrue(success.get());
    }

    @Test
    public void testWhen_thenThrow() {

        try {
            when(null, Null()).thenThrow(() -> new Exception("MY MESSAGE"));
            fail();
        } catch (Throwable ex) {
            assertEquals("MY MESSAGE", ex.getMessage());
        }

        try {
            when("NOT NULL", Null()).then(() -> new Exception("MY MESSAGE"));
        } catch (Throwable ex) {
            fail();
        }
    }

    @Test
    public void testIs() {
        Validation expected = Null();
        Validation actual = is(expected);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testIs_VarArr() {
        Validation expected1 = Null();
        Validation expected2 = Null();
        Validation actual = is(expected1, expected2);

        assertNotNull(actual);
        assertTrue(actual instanceof All);
        assertTrue(actual.validate(null));
        assertFalse(actual.validate("NOT NULL"));
    }

    @Test
    public void testNot() {
        Validation expected = Null();
        Validation actual = not(expected);

        assertNotNull(actual);
        assertTrue(actual instanceof Not);
        assertTrue(actual.validate("NOT NULL"));
        assertFalse(actual.validate(null));
    }

    @Test
    public void testNot_VarArr() {
        Validation expected1 = Null();
        Validation expected2 = empty();
        Validation actual = not(expected1, expected2);

        assertNotNull(actual);
        assertTrue(actual instanceof None);
        assertTrue(actual.validate("NOT NULL AND NOT EMPTY"));
        assertFalse(actual.validate(null));
    }

    @Test
    public void testNull() {
        assertTrue(Null().validate(null));
        assertFalse(Null().validate("NOT NULL"));
    }

    @Test
    public void testEmpty() {
        assertTrue(empty().validate(""));
        assertFalse(empty().validate("NOT EMPTY"));
    }

    @Test
    public void testBlank() {
        assertTrue(blank().validate(" \n\t\r"));
        assertFalse(blank().validate("NOT BLANK"));
    }

    @Test
    public void testGreaterThan() {
        assertTrue(greaterThan(1).validate(2));
        assertFalse(greaterThan(2).validate(1));
    }

    @Test
    public void testGreaterThanOrEqualTo() {
        assertTrue(greaterThanOrEqualTo(2).validate(2));
        assertFalse(greaterThanOrEqualTo(2).validate(1));
    }

    @Test
    public void testLessThan() {
        assertTrue(lessThan(2).validate(1));
        assertFalse(lessThan(1).validate(2));
    }

    @Test
    public void testLessThanOrEqualTo() {
        assertTrue(lessThanOrEqualTo(2).validate(2));
        assertFalse(lessThanOrEqualTo(1).validate(2));
    }

}
