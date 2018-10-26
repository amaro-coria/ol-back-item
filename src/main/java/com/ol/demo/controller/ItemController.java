package com.ol.demo.controller;

import com.ol.demo.persistence.entity.Item;
import com.ol.demo.persistence.repository.ItemRepository;
import com.ol.demo.util.OLConstants;
import com.ol.demo.util.dto.ItemOperationDTO;
import com.ol.demo.util.dto.ItemPartialUpdateDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = OLConstants.API_PREFIX + OLConstants.API_ITEM)
@Api(value = "Items API", description = "Operations related to items and stock")
@CrossOrigin
public class ItemController {

    private ItemRepository itemRepository;
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    public ItemController(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    @ApiOperation(value = "Retrieves all records on database", response = Item.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful response"),
            @ApiResponse(code = 404, message = "No items found"),
            @ApiResponse(code = 500, message = "Problem with data source")
    })
    @GetMapping(value = OLConstants.API_ROOT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Item>> fetchAll(){
        List<Item> list = null;
        try {
            list = itemRepository.findAll();
        } catch (Exception e) {
            log.error("Error in fetchAll: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(list == null || list.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves specific record based on its ID", response = Item.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful response"),
            @ApiResponse(code = 404, message = "No item found"),
            @ApiResponse(code = 500, message = "Problem with data source")
    })
    @GetMapping(value = OLConstants.API_ITEM_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Item> fetchById(@PathVariable Integer id){
        try {
            Optional<Item> optionalItem = itemRepository.findById(id);
            if(!optionalItem.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Item item = optionalItem.get();
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in fetchById: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Add or remove specific quantity fro stock based on its amount. Positive amount, adds. Negative amount, withdraws", response = Item.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful response"),
            @ApiResponse(code = 404, message = "No item found"),
            @ApiResponse(code = 400, message = "Withdraw not possible because of lack of existence"),
            @ApiResponse(code = 500, message = "Problem with data source")
    })
    @PostMapping(value = OLConstants.API_ITEM_OPERATION_ADD_OR_REMOVE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Item> addOrRemoveFromStock(@RequestBody ItemOperationDTO itemOperationDTO){
        Optional<Item> relatedoptionalItem = itemRepository.findById(itemOperationDTO.getItemNo());
        if(!relatedoptionalItem.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Item relatedItem = relatedoptionalItem.get();
        double totalAmout = relatedItem.getAmount();
        if(itemOperationDTO.getAmount() < 0){
           double amountToWithdraw = Math.abs(itemOperationDTO.getAmount());
           if(amountToWithdraw > totalAmout){
               return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
           }
           log.info("Withdraw has been requested. Total before: {} , to withdraw: {}", totalAmout, amountToWithdraw);
           relatedItem.setAmount(totalAmout - amountToWithdraw);
        }else{
            log.info("Deposit has been requested. Total before: {}, to deposit: {}", totalAmout, itemOperationDTO.getAmount());
            relatedItem.setAmount(totalAmout + itemOperationDTO.getAmount());
        }
        try {
            Item itemUpdated = itemRepository.save(relatedItem);
            return new ResponseEntity<>(itemUpdated, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in addOrRemoveFromStock: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Adds new item to stock", response = Item.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successful response"),
            @ApiResponse(code = 422, message = "No item can be added. Duplicated inventory code"),
            @ApiResponse(code = 500, message = "Problem with data source")
    })
    @PostMapping(value = OLConstants.API_ROOT)
    public ResponseEntity<Item> addItemToStock(@RequestBody Item itemToAdd){
        if(itemToAdd.getItemNo() != null){
            itemToAdd.setItemNo(null);
        }
        try {
            Item itemSaved = itemRepository.save(itemToAdd);
            return new ResponseEntity<>(itemSaved, HttpStatus.CREATED);
        } catch(DataIntegrityViolationException de){
            log.error("Unique violated with item: {} with error message: {}",itemToAdd, de.getMessage());
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }catch (Exception e) {
            log.error("Error in addItemToStock: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Deletes item from stock", response = Item.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful response"),
            @ApiResponse(code = 404, message = "No item found"),
            @ApiResponse(code = 500, message = "Problem with data source")
    })
    @DeleteMapping(value = OLConstants.API_ITEM_BY_ID, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Item> deleteItem(@PathVariable Integer id){
        try {
            Optional<Item> optionalRelated = itemRepository.findById(id);
            if(!optionalRelated.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            itemRepository.delete(optionalRelated.get());
            return new ResponseEntity<>(optionalRelated.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in deleteItem: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Updates the name of related item", response = Item.class)
    @ApiResponses({
            @ApiResponse(code = 202, message = "Successful response"),
            @ApiResponse(code = 404, message = "No item found"),
            @ApiResponse(code = 500, message = "Problem with data source")
    })
    @PatchMapping(value = OLConstants.API_ROOT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Item> updatePartial(@RequestBody ItemPartialUpdateDTO itemPartialUpdateDTO){
        try {
            Optional<Item> optionalRelated = itemRepository.findById(itemPartialUpdateDTO.getItemNo());
            if(!optionalRelated.isPresent()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Item relatedItem = optionalRelated.get();
            relatedItem.setName(itemPartialUpdateDTO.getName());
            Item item = itemRepository.save(relatedItem);
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in updatePartial: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
