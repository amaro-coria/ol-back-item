package com.ol.demo.util.dto;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public class ItemOperationDTO implements Serializable {
    @NonNull
    private Integer itemNo;
    @NonNull
    private Double amount;

}