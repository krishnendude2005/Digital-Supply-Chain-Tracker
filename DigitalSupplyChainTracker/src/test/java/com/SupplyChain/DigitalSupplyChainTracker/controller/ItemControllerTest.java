package com.SupplyChain.DigitalSupplyChainTracker.controller;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.AddItemRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ItemUpdateRequest;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void getItems_Supplier_Success() throws Exception {
        UserEntity supplier = buildUser("supplier1@example.com", Role.SUPPLIER);

        Item item1 = buildItem("Laptop", "Electronics", supplier);
        Item item2 = buildItem("Mouse", "Electronics", supplier);

        when(itemService.getAllItems(any())).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .with(authentication(auth("supplier1@example.com", "SUPPLIER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getItems_Admin_Success() throws Exception {
        UserEntity admin = buildUser("admin@example.com", Role.ADMIN);

        Item item1 = buildItem("Laptop", "Electronics", admin);
        Item item2 = buildItem("Mouse", "Electronics", admin);

        when(itemService.getAllItems(any())).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .with(authentication(auth("admin@example.com", "ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getItems_Transporter_ReturnsEmpty() throws Exception {
        when(itemService.getAllItems(any())).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .with(authentication(auth("transporter@example.com", "TRANSPORTER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addItem_Success() throws Exception {
        AddItemRequest addItemRequest = new AddItemRequest();
        addItemRequest.setName("Laptop");
        addItemRequest.setCategory("Electronics");

        UserEntity supplier = buildUser("supplier1@example.com", Role.SUPPLIER);
        Item savedItem = buildItem("Laptop", "Electronics", supplier);

        when(itemService.addItem(any(AddItemRequest.class))).thenReturn(savedItem);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest))
                        .with(authentication(auth("supplier1@example.com", "SUPPLIER"))))
                .andExpect(status().isCreated());
    }

    @Test
    void addItem_AsAdmin_Success() throws Exception {
        AddItemRequest addItemRequest = new AddItemRequest();
        addItemRequest.setName("Laptop");
        addItemRequest.setCategory("Electronics");

        UserEntity admin = buildUser("admin@gmail.com", Role.ADMIN);
        Item savedItem = buildItem("Laptop", "Electronics", admin);

        when(itemService.addItem(any(AddItemRequest.class))).thenReturn(savedItem);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest))
                        .with(authentication(auth("admin@gmail.com", "ADMIN"))))
                .andExpect(status().isCreated());
    }

    @Test
    void updateItem_Success() throws Exception {
        UserEntity supplier = buildUser("supplier1@example.com", Role.SUPPLIER);
        UUID itemId = UUID.randomUUID();

        ItemUpdateRequest updateRequest = new ItemUpdateRequest();
        updateRequest.setName("Updated Laptop");
        updateRequest.setCategory("Updated Electronics");
        updateRequest.setSupplierEmail("supplier1@example.com");

        Item updatedItem = Item.builder()
                .id(1L)
                .itemId(itemId)
                .name("Updated Laptop")
                .category("Updated Electronics")
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(itemService.updateItem(any(ItemUpdateRequest.class), eq(itemId))).thenReturn(updatedItem);

        mockMvc.perform(put("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(authentication(auth("supplier1@example.com", "SUPPLIER"))))
                .andExpect(status().isOk());
    }



    @Test
    void deleteItem_Success() throws Exception {
        UUID itemId = UUID.randomUUID();

        doNothing().when(itemService).deleteItemByItemId(itemId);

        mockMvc.perform(delete("/items")
                        .param("itemId", itemId.toString())
                        .with(authentication(auth("supplier1@example.com", "SUPPLIER"))))
                .andExpect(status().isNoContent());
        // If your controller actually returns a message body like "item deleted successfully",
        // change this to:
        // .andExpect(status().isOk())
        // .andExpect(content().string("item deleted successfully"));
    }

    @Test
    void searchItems_Success() throws Exception {
        UserEntity supplier = buildUser("supplier1@example.com", Role.SUPPLIER);

        Item item1 = buildItem("Laptop", "Electronics", supplier);
        Item item2 = buildItem("Phone", "Electronics", supplier);

        when(itemService.searchedItem("Electronics")).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items/item")
                        .param("category", "Electronics")
                        .with(authentication(auth("supplier1@example.com", "SUPPLIER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getItemMetrics_Success() throws Exception {
        UserEntity supplier = buildUser("supplier1@example.com", Role.SUPPLIER);
        Item savedItem = buildItem("Laptop", "Electronics", supplier);

        UUID itemId = savedItem.getItemId();
        when(itemService.getItemByItemId(itemId)).thenReturn(savedItem);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .with(authentication(auth("supplier1@example.com", "SUPPLIER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.category", is("Electronics")));
    }

    private UsernamePasswordAuthenticationToken auth(String email, String role) {
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    private UserEntity buildUser(String email, Role role) {
        return UserEntity.builder()
                .id(1L)
                .userId(UUID.randomUUID())
                .name(role.name() + " User")
                .email(email)
                .password("encodedPassword")
                .role(role)
                .build();
    }

    private Item buildItem(String name, String category, UserEntity supplier) {
        return Item.builder()
                .id(1L)
                .itemId(UUID.randomUUID())
                .name(name)
                .category(category)
                .supplier(supplier)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

