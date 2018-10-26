package com.ol.demo.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "ITEM")
public class Item implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "ITEM_NO")
    private Integer itemNo;
    @NonNull
    @Column(name = "NAME")
    private String name;
    @Column(name = "AMOUNT")
    private Double amount;
    @Column(name = "INVENTORY_CODE", unique = true)
    private String inventoryCode;

    @PrePersist
    private void check(){
        if(this.inventoryCode == null || this.inventoryCode.isEmpty()){
            this.inventoryCode = UUID.randomUUID().toString();
        }
        if(this.amount == null){
            this.amount = 0.0;
        }
    }

}
