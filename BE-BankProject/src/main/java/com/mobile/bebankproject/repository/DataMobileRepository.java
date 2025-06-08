package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.DataMobile;
import com.mobile.bebankproject.model.TelcoProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataMobileRepository extends JpaRepository<DataMobile, Integer> {
    DataMobile getDataMobileById(int id);
}