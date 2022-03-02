package com.parentalcontrol;

import com.parentalcontrol.exception.UnknownAgeRatingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.stream.Stream;

import static com.parentalcontrol.ParentalControlLevel.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ParentalControlLevelTest {
    @ParameterizedTest
    @ValueSource(strings = {"U", "PG", "12", "15", "18"})
    void shouldConvertKnownKeysToControlLevel(String value) {
        assertThat(ParentalControlLevel.toParentalControlLevel(value)).isNotNull();
    }

    @Test
    void shouldThrowExceptionForUnknownKey() {
        UnknownAgeRatingException thrown = assertThrows(UnknownAgeRatingException.class,
                () -> toParentalControlLevel("random"));

        assertThat(thrown.getMessage()).isEqualTo("Unknown Age Rating [random]");
    }

    @ParameterizedTest
    @MethodSource("suitableFor")
    void givenParentalControlLevelShouldBeSuitableFor(ParentalControlLevel source, EnumSet<ParentalControlLevel> targets) {
        targets.forEach(level -> assertTrue(source.isSuitableFor(level)));
    }

    @ParameterizedTest
    @MethodSource("notSuitableFor")
    void givenParentalControlLevelShouldNotBeSuitableFor(ParentalControlLevel source, EnumSet<ParentalControlLevel> targets) {
        targets.forEach(level -> assertFalse(source.isSuitableFor(level)));
    }

    private static Stream<Arguments> suitableFor() {
        return Stream.of(
                Arguments.of(U, EnumSet.of(U)),
                Arguments.of(PG, EnumSet.of(U, PG)),
                Arguments.of(TWELVE, EnumSet.of(U, PG, TWELVE)),
                Arguments.of(FIFTEEN, EnumSet.of(U, PG, TWELVE, FIFTEEN)),
                Arguments.of(EIGHTEEN, EnumSet.of(U, PG, TWELVE, FIFTEEN, EIGHTEEN))
        );
    }

    private static Stream<Arguments> notSuitableFor() {
        return Stream.of(
                Arguments.of(U, EnumSet.of(PG, TWELVE, FIFTEEN, EIGHTEEN)),
                Arguments.of(PG, EnumSet.of(TWELVE, FIFTEEN, EIGHTEEN)),
                Arguments.of(TWELVE, EnumSet.of(FIFTEEN,EIGHTEEN)),
                Arguments.of(FIFTEEN, EnumSet.of(EIGHTEEN))
        );
    }

}