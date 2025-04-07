package com.adobe.aem.guides.wknd.core.utils;

import com.adobe.aem.guides.wknd.core.models.WarrantyModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Warrantyutil {
    private static final String ALPHA_NUMERIC = "[a-zA-Z0-9]+";
    private static final String DATE_FORMAT = "^\\d{4}-\\d{2}-\\d{2}$";

    public static void validation(WarrantyModel warrantyModel) throws ParseException, IllegalArgumentException {

        String productId = warrantyModel.getProductId();
        String modelId = warrantyModel.getModelId();
        String startDate = warrantyModel.getStartDate();
        String endDate = warrantyModel.getEndDate();
        int dealerId = warrantyModel.getDealerId();

        if (productId == null || productId.length() != 10 || !productId.matches(ALPHA_NUMERIC)) {
            throw new IllegalArgumentException("Invalid productId");
        }

        if (modelId == null || modelId.length() != 10 || !modelId.matches(ALPHA_NUMERIC)) {
            throw new IllegalArgumentException("Invalid modelId");
        }
        if (!startDate.matches(DATE_FORMAT)) {
            throw new IllegalArgumentException("Invalid startDate format");
        }
        if (!endDate.matches(DATE_FORMAT)) {
            throw new IllegalArgumentException("Invalid endDate format");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        Date start = sdf.parse(startDate);
        Date end = sdf.parse(endDate);

        if (end.before(start)) {
            throw new IllegalArgumentException("endDate is before startDate");
        }

        if (dealerId < 1000 || dealerId > 9999) {
            throw new IllegalArgumentException("dealerId must be a 4-digit positive number");
        }
    }
}
