package com.ol.demo.util.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ItemPartialUpdateDTO implements Serializable {

    private Integer itemNo;
    private String name;

}