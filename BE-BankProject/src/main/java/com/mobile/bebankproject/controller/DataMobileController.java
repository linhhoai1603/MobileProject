package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.model.DataMobile;
import com.mobile.bebankproject.service.DataMobileService;
import com.mobile.bebankproject.dto.DataPackagePreview;
import com.mobile.bebankproject.dto.DataPackageRequest;
import com.mobile.bebankproject.dto.DataPackageFilterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data-mobile")
public class DataMobileController {

    @Autowired
    private DataMobileService dataMobileService;

    /**
     * Get all available data packages filtered by valid date and quantity
     * @param request The filter request containing provider, validDate and quantity
     * @return List of available data packages
     */
    @PostMapping("/packages")
    public ResponseEntity<List<DataMobile>> getFilteredPackages(@RequestBody DataPackageFilterRequest request) {
        try {
            List<DataMobile> packages = dataMobileService.getFilteredPackages(
                request.getProvider(),
                request.getValidDate(),
                request.getQuantity()
            );
            return ResponseEntity.ok(packages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Preview a data package purchase
     * @param request The preview request containing account number, phone number and package ID
     * @return Preview details including OTP
     */
    @PostMapping("/preview")
    public ResponseEntity<DataPackagePreview> previewPurchase(@RequestBody DataPackageRequest request) {
        try {
            DataPackagePreview preview = dataMobileService.previewPurchase(
                request.getAccountNumber(),
                request.getPhoneNumber(),
                request.getPackageId()
            );
            return ResponseEntity.ok(preview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Purchase a data package
     * @param request The ID of the data package to purchase
     * @return Success status
     */
    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseDataPackage(@RequestBody DataPackageRequest request) {
        try {
            boolean success = dataMobileService.purchaseDataPackage(request.getAccountNumber(), request.getPhoneNumber(), request.getPackageId());
            if (success) {
                return ResponseEntity.ok("Purchase successful");
            } else {
                return ResponseEntity.badRequest().body("Purchase failed");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
