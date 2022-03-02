package com.parentalcontrol;

import com.parentalcontrol.exception.UnknownTitleException;
import com.parentalcontrol.exception.AuthorisationFailureException;
import com.thirdparty.TechnicalFailureException;
import com.thirdparty.ThirdPartyTitleCatalogueClient;
import com.thirdparty.TitleNotFoundException;
import com.thirdparty.model.Title;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.parentalcontrol.ParentalControlLevel.EIGHTEEN;
import static com.parentalcontrol.ParentalControlLevel.FIFTEEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParentalControlServiceImplTest {
    @Mock
    private ThirdPartyTitleCatalogueClient client;

    @InjectMocks
    private ParentalControlServiceImpl service;

    @Test
    void givenTitleNotFoundShouldThrowUnknownTitleException() throws TitleNotFoundException, TechnicalFailureException {
        String titleId = UUID.randomUUID().toString();
        when(client.getTitle(titleId)).thenThrow(TitleNotFoundException.class);

        UnknownTitleException thrown = assertThrows(UnknownTitleException.class, () -> service.isAuthorised(EIGHTEEN, titleId));

        assertThat(thrown.getMessage()).isEqualTo(String.format("Title Id Unknown [%s]", titleId));
    }

    @Test
    void givenParentalControlLevelIsNullShouldFailAuthorisation() {
        AuthorisationFailureException thrown = assertThrows(AuthorisationFailureException.class, () -> service.isAuthorised(null, "titleId"));

        assertThat(thrown.getMessage()).isEqualTo("ParentalControlLevel cannot be null");
    }

    @Test
    void givenClientThrowsTechnicalFailureExceptionShouldReturnFalse() throws TitleNotFoundException, TechnicalFailureException, UnknownTitleException {
        String titleId = UUID.randomUUID().toString();
        when(client.getTitle(titleId)).thenThrow(TechnicalFailureException.class);

        assertFalse(service.isAuthorised(EIGHTEEN, titleId));
    }

    @Test
    void givenTitleAgeRatingIsLowerThanRequestParentalControlLevelShouldAuthorisedTitle() throws TitleNotFoundException, TechnicalFailureException, UnknownTitleException {
        String titleId = UUID.randomUUID().toString();
        String ageRating = "12";

        Title title = mock(Title.class);
        when(title.getAgeRating()).thenReturn(ageRating);

        when(client.getTitle(titleId)).thenReturn(title);

        assertTrue(service.isAuthorised(FIFTEEN, titleId));
    }

    @Test
    void givenTitleAgeRatingIsHigherThanRequestParentalControlLevelShouldNotAuthorisedTitle() throws TitleNotFoundException, TechnicalFailureException, UnknownTitleException {
        String titleId = UUID.randomUUID().toString();
        String ageRating = "18";

        Title title = mock(Title.class);
        when(title.getAgeRating()).thenReturn(ageRating);

        when(client.getTitle(titleId)).thenReturn(title);

        assertFalse(service.isAuthorised(FIFTEEN, titleId));
    }

    @Test
    void givenTitleAgeRatingIsUnknownShouldFailAuthorisation() throws TitleNotFoundException, TechnicalFailureException {
        String titleId = UUID.randomUUID().toString();
        String ageRating = UUID.randomUUID().toString();

        Title title = mock(Title.class);
        when(title.getAgeRating()).thenReturn(ageRating);

        when(client.getTitle(titleId)).thenReturn(title);

        AuthorisationFailureException thrown = assertThrows(AuthorisationFailureException.class, () -> service.isAuthorised(EIGHTEEN, titleId));

        assertThat(thrown.getMessage()).isEqualTo(String.format("Parental control level [%s] is unknown", ageRating));
    }
}