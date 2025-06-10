package com.mobile.bebankproject.service;

import com.mobile.bebankproject.model.DataMobile;
import com.mobile.bebankproject.model.TelcoProvider;
import com.mobile.bebankproject.dto.DataPackagePreview;

import java.util.List;

public interface DataMobileService {
    boolean purchaseDataPackage(String accountNumber, String phoneNumber, int packageId);
    DataPackagePreview previewPurchase(String accountNumber, String phoneNumber, int packageId);
    List<DataMobile> getFilteredPackages(TelcoProvider telcoProvider, Integer validDate, Integer quantity);

    DataMobile getDataPackageById(int id);
}
