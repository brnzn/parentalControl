package com.parentalcontrol;

import com.parentalcontrol.exception.AuthorisationFailureException;
import com.parentalcontrol.exception.UnknownAgeRatingException;
import com.parentalcontrol.exception.UnknownTitleException;
import com.thirdparty.TechnicalFailureException;
import com.thirdparty.ThirdPartyTitleCatalogueClient;
import com.thirdparty.TitleNotFoundException;
import com.thirdparty.model.Title;

public class ParentalControlServiceImpl implements ParentalControlService {
    private final ThirdPartyTitleCatalogueClient titleCatalogueClient;

    public ParentalControlServiceImpl(ThirdPartyTitleCatalogueClient titleCatalogueClient) {
        this.titleCatalogueClient = titleCatalogueClient;
    }

    @Override
    public boolean isAuthorised(ParentalControlLevel level, String titleId) throws UnknownTitleException {
        if(level == null) {
            throw new AuthorisationFailureException("ParentalControlLevel cannot be null");
        }

        try {
            Title title = titleCatalogueClient.getTitle(titleId);
            return compare(level, title.getAgeRating());
        } catch (TitleNotFoundException e) {
            throw new UnknownTitleException(String.format("Title Id Unknown [%s]", titleId));
        } catch (TechnicalFailureException e) {
            return false;
        }
    }

    private boolean compare(ParentalControlLevel level, String ageRating) {
        try {
            return level.isSuitableFor(ParentalControlLevel.toParentalControlLevel(ageRating));
        } catch (UnknownAgeRatingException e) {
            throw new AuthorisationFailureException(String.format("Parental control level [%s] is unknown", ageRating));
        }
    }
}
