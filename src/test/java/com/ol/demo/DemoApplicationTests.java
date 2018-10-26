package com.ol.demo;

import com.ol.demo.controller.ItemController;
import com.ol.demo.persistence.entity.Item;
import com.ol.demo.util.dto.ItemOperationDTO;
import com.ol.demo.util.dto.ItemPartialUpdateDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(DemoApplicationTests.class);
    @Autowired
    private ItemController itemController;


    @Test
    public void contextLoads() {
        log.info("Context loaded");
    }

    @Test
    public void testFetchAll(){
        int expectedTotal = 3;
        ResponseEntity<List<Item>> responseEntity = itemController.fetchAll();
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedTotal, responseEntity.getBody().size());
    }

    @Test
    public void testFetchById(){
        int provided = 2;
        int notFound = 5;
        ResponseEntity<Item> responseFound = itemController.fetchById(provided);
        ResponseEntity<Item> responseNotFound = itemController.fetchById(notFound);
        assertEquals(200, responseFound.getStatusCodeValue());
        assertEquals(404, responseNotFound.getStatusCodeValue());
    }

    @Test
    public void testWithdrawAdd(){
        final double DELTA = 1e-15;
        int firstId = 1;
        int secondId = 2;
        int thirdId = 5;
        ItemOperationDTO firstOperation1 = new ItemOperationDTO(firstId, 10.0);
        ItemOperationDTO secondOperation1 = new ItemOperationDTO(firstId, 20.0);
        ItemOperationDTO thirdOperation1 = new ItemOperationDTO(firstId, -30.0);

        ResponseEntity<Item> firstResponse1 = itemController.addOrRemoveFromStock(firstOperation1);
        ResponseEntity<Item> secondResponse1 = itemController.addOrRemoveFromStock(secondOperation1);
        ResponseEntity<Item> thirdResponse1 = itemController.addOrRemoveFromStock(thirdOperation1);

        assertEquals(200, firstResponse1.getStatusCodeValue());
        assertEquals(200, secondResponse1.getStatusCodeValue());
        assertEquals(200, thirdResponse1.getStatusCodeValue());
        Item finalResult1 = thirdResponse1.getBody();
        assertEquals(0.0, finalResult1.getAmount().doubleValue(), DELTA);

        ItemOperationDTO firstOperation2 = new ItemOperationDTO(secondId, 10.0);
        ItemOperationDTO secondOperation2 = new ItemOperationDTO(secondId, 20.0);
        ItemOperationDTO thirdOperation2 = new ItemOperationDTO(secondId, -50.0);

        ResponseEntity<Item> firstResponse2 = itemController.addOrRemoveFromStock(firstOperation2);
        ResponseEntity<Item> secondResponse2 = itemController.addOrRemoveFromStock(secondOperation2);
        ResponseEntity<Item> thirdResponse2 = itemController.addOrRemoveFromStock(thirdOperation2);

        assertEquals(200, firstResponse2.getStatusCodeValue());
        assertEquals(200, secondResponse2.getStatusCodeValue());
        assertEquals(400, thirdResponse2.getStatusCodeValue());

        ItemOperationDTO operationDTO3 = new ItemOperationDTO(thirdId, 0.0);
        ResponseEntity<Item> responseEntity3 = itemController.addOrRemoveFromStock(operationDTO3);
        assertEquals(404, responseEntity3.getStatusCodeValue());
    }

    @Test
    public void testAddRemoveItem(){
        Item item1 = new Item("DR PEPPER"); //4
        Item item2 = new Item(5, "FANTA", 10.0, "U00FTA"); //5
        Item item3 = new Item(6, "FRESCA", 30.0, "U00FRS"); //6
        Item item4 = new Item(6, "FRESCA", 30.0, "U00FRS");
        ResponseEntity<Item> responseEntity1 = itemController.addItemToStock(item1);
        ResponseEntity<Item> responseEntity2 = itemController.addItemToStock(item2);
        ResponseEntity<Item> responseEntity3 = itemController.addItemToStock(item3);
        ResponseEntity<Item> responseEntity4 = itemController.addItemToStock(item4);
        assertEquals(201, responseEntity1.getStatusCodeValue());
        assertEquals(201, responseEntity2.getStatusCodeValue());
        assertEquals(201, responseEntity3.getStatusCodeValue());
        assertEquals(422, responseEntity4.getStatusCodeValue());
        ResponseEntity<Item> responseDelete1 = itemController.deleteItem(4);
        ResponseEntity<Item> responseDelete2 = itemController.deleteItem(5);
        ResponseEntity<Item> responseDelete3 = itemController.deleteItem(6);
        ResponseEntity<Item> responseDelete4 = itemController.deleteItem(7);
        assertEquals(200, responseDelete1.getStatusCodeValue());
        assertEquals(200, responseDelete2.getStatusCodeValue());
        assertEquals(200, responseDelete3.getStatusCodeValue());
        assertEquals(404, responseDelete4.getStatusCodeValue());
    }

    @Test
    public void testUpdatePartial(){
        ResponseEntity<Item> responseEntity = itemController.fetchById(1);
        assertEquals(200, responseEntity.getStatusCodeValue());
        Item item = responseEntity.getBody();
        assertNotNull(item);
        final String givenName = "TEST_NAME";
        String retrievedName = item.getName();
        assertNotSame(givenName, retrievedName);
        ItemPartialUpdateDTO itemPartialUpdateDTO = new ItemPartialUpdateDTO(1, givenName);
        ResponseEntity<Item> responseEntity1 = itemController.updatePartial(itemPartialUpdateDTO);
        assertEquals(200,responseEntity1.getStatusCodeValue());
        ResponseEntity<Item> responseEntity2 = itemController.fetchById(1);
        assertEquals(200, responseEntity2.getStatusCodeValue());
        assertNotNull(responseEntity2.getBody());
        String retrievedFinal = responseEntity2.getBody().getName();
        assertEquals(retrievedFinal, givenName);
    }

}
