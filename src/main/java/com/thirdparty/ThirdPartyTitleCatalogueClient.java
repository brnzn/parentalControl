package com.thirdparty;

import com.thirdparty.model.Title;

public interface ThirdPartyTitleCatalogueClient {
    Title getTitle(String titleId) throws TitleNotFoundException, TechnicalFailureException;
}
