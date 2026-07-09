
package com.SupplyChain.DigitalSupplyChainTracker.service;

import com.SupplyChain.DigitalSupplyChainTracker.dto.request.AddItemRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.request.ItemUpdateRequest;
import com.SupplyChain.DigitalSupplyChainTracker.dto.response.ItemResponse;
import com.SupplyChain.DigitalSupplyChainTracker.entity.Item;
import com.SupplyChain.DigitalSupplyChainTracker.entity.UserEntity;
import com.SupplyChain.DigitalSupplyChainTracker.entity.enums.Role;
import com.SupplyChain.DigitalSupplyChainTracker.exception.ResourceNotFoundException;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ItemRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.ShipmentRepo;
import com.SupplyChain.DigitalSupplyChainTracker.repository.UserRepo;
import com.SupplyChain.DigitalSupplyChainTracker.service.Impl.ItemServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepo itemRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ShipmentRepo shipmentRepo;

    @InjectMocks
    private ItemServiceImpl itemService;

    private UserEntity adminUser;
    private UserEntity supplierUser;
    private UserEntity transporterUser;
    private Item item;
    private AddItemRequest addItemRequest;
    private ItemUpdateRequest updateItemRequest;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();

        adminUser = UserEntity.builder()
                .id(1L)
                .userId(UUID.randomUUID())
                .name("Admin User")
                .email("admin@gmail.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        supplierUser = UserEntity.builder()
                .id(2L)
                .userId(UUID.randomUUID())
                .name("Supplier User")
                .email("supplier1@example.com")
                .password("encodedPassword")
                .role(Role.SUPPLIER)
                .build();

        transporterUser = UserEntity.builder()
                .id(3L)
                .userId(UUID.randomUUID())
                .name("Transporter User")
                .email("transporter1@example.com")
                .password("encodedPassword")
                .role(Role.TRANSPORTER)
                .build();

        item = Item.builder()
                .id(1L)
                .itemId(itemId)
                .name("Test Item")
                .category("Electronics")
                .supplier(supplierUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        addItemRequest = new AddItemRequest();
        addItemRequest.setName("Test Item");
        addItemRequest.setCategory("Electronics");

        updateItemRequest = new ItemUpdateRequest();
        updateItemRequest.setName("Updated Test Item");
        updateItemRequest.setCategory("Updated Electronics");
        updateItemRequest.setSupplierEmail("admin@gmail.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllItems_Admin_Success() {
        List<Item> items = List.of(item);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("admin@gmail.com", null, authorities);

        when(itemRepo.findAll()).thenReturn(items);

        List<ItemResponse> result = itemService.getAllItems(authentication);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getName());
        assertEquals("Electronics", result.get(0).getCategory());
        verify(itemRepo).findAll();
    }

    @Test
    void getAllItems_Supplier_Success() {
        List<Item> items = List.of(item);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_SUPPLIER"));
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("supplier1@example.com", null, authorities);

        when(itemRepo.findBySupplier_EmailIgnoreCase("supplier1@example.com")).thenReturn(items);

        List<ItemResponse> result = itemService.getAllItems(authentication);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepo).findBySupplier_EmailIgnoreCase("supplier1@example.com");
    }

    @Test
    void getAllItems_Unauthorized_ReturnsEmpty() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_TRANSPORTER"));
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("transporter1@example.com", null, authorities);

        List<ItemResponse> result = itemService.getAllItems(authentication);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(itemRepo, never()).findAll();
        verify(itemRepo, never()).findBySupplier_EmailIgnoreCase(anyString());
    }

    @Test
    void addItem_Success() {
        setAuthentication(supplierUser, Role.SUPPLIER);

        when(userRepo.findByEmail("supplier1@example.com")).thenReturn(Optional.of(supplierUser));
        when(itemRepo.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemResponse result = itemService.addItem(addItemRequest);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        assertEquals("Electronics", result.getCategory());
        assertEquals(supplierUser.getEmail(), result.getSupplierEmail());

        verify(userRepo).findByEmail("supplier1@example.com");
        verify(itemRepo).save(any(Item.class));
    }

    @Test
    void addItem_UserNotFound_ThrowsException() {
        setAuthentication(supplierUser, Role.SUPPLIER);

        when(userRepo.findByEmail("supplier1@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.addItem(addItemRequest));

        verify(userRepo).findByEmail("supplier1@example.com");
        verify(itemRepo, never()).save(any());
    }

    @Test
    void updateItem_Success() {
        when(itemRepo.findByItemId(itemId)).thenReturn(Optional.of(item));
        when(userRepo.findByEmail("admin@gmail.com")).thenReturn(Optional.of(adminUser));
        when(itemRepo.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemResponse result = itemService.updateItem(updateItemRequest, itemId);

        assertNotNull(result);
        assertEquals("Updated Test Item", result.getName());
        assertEquals("Updated Electronics", result.getCategory());
        assertEquals(adminUser.getEmail(), result.getSupplierEmail());

        verify(itemRepo).findByItemId(itemId);
        verify(userRepo).findByEmail("admin@gmail.com");
        verify(itemRepo).save(any(Item.class));
    }

    @Test
    void updateItem_ItemNotFound_ThrowsException() {
        when(itemRepo.findByItemId(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.updateItem(updateItemRequest, itemId));

        verify(itemRepo).findByItemId(itemId);
        verify(userRepo, never()).findByEmail(anyString());
        verify(itemRepo, never()).save(any());
    }

    @Test
    void updateItem_SupplierNotFound_ThrowsException() {
        when(itemRepo.findByItemId(itemId)).thenReturn(Optional.of(item));
        when(userRepo.findByEmail("admin@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.updateItem(updateItemRequest, itemId));

        verify(itemRepo).findByItemId(itemId);
        verify(userRepo).findByEmail("admin@gmail.com");
        verify(itemRepo, never()).save(any());
    }

    @Test
    void deleteItemByItemId_Success() {
        when(itemRepo.existsByItemId(itemId)).thenReturn(true);
        when(shipmentRepo.existsByItem_ItemId(itemId)).thenReturn(false);
        doNothing().when(itemRepo).deleteByItemId(itemId);

        itemService.deleteItemByItemId(itemId);

        verify(itemRepo).existsByItemId(itemId);
        verify(shipmentRepo).existsByItem_ItemId(itemId);
        verify(itemRepo).deleteByItemId(itemId);
    }

    @Test
    void deleteItemByItemId_NotFound_ThrowsException() {
        when(itemRepo.existsByItemId(itemId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItemByItemId(itemId));

        verify(itemRepo).existsByItemId(itemId);
        verify(itemRepo, never()).deleteByItemId(any());
    }

    @Test
    void getItemByItemId_AdminAccess_Success() {
        setAuthentication(adminUser, Role.ADMIN);

        when(userRepo.findByEmail("admin@gmail.com")).thenReturn(Optional.of(adminUser));
        when(itemRepo.findByItemId(itemId)).thenReturn(Optional.of(item));

        ItemResponse result = itemService.getItemByItemId(itemId);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());

        verify(userRepo).findByEmail("admin@gmail.com");
        verify(itemRepo).findByItemId(itemId);
    }

    @Test
    void getItemByItemId_SupplierAccess_Success() {
        setAuthentication(supplierUser, Role.SUPPLIER);

        when(userRepo.findByEmail("supplier1@example.com")).thenReturn(Optional.of(supplierUser));
        when(itemRepo.findBySupplier_EmailIgnoreCaseAndItemId("supplier1@example.com", itemId))
                .thenReturn(Optional.of(item));

        ItemResponse result = itemService.getItemByItemId(itemId);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());

        verify(userRepo).findByEmail("supplier1@example.com");
        verify(itemRepo).findBySupplier_EmailIgnoreCaseAndItemId("supplier1@example.com", itemId);
    }

    @Test
    void getItemByItemId_ItemNotFound_ThrowsException() {
        setAuthentication(adminUser, Role.ADMIN);

        when(userRepo.findByEmail("admin@gmail.com")).thenReturn(Optional.of(adminUser));
        when(itemRepo.findByItemId(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemByItemId(itemId));

        verify(userRepo).findByEmail("admin@gmail.com");
        verify(itemRepo).findByItemId(itemId);
    }

    @Test
    void searchedItem_Admin_Success() {
        setAuthentication(adminUser, Role.ADMIN);

        when(userRepo.findByEmail("admin@gmail.com")).thenReturn(Optional.of(adminUser));
        when(itemRepo.findByCategoryIgnoreCase("Electronics")).thenReturn(List.of(item));

        List<ItemResponse> result = itemService.searchedItem("Electronics");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategory());

        verify(userRepo).findByEmail("admin@gmail.com");
        verify(itemRepo).findByCategoryIgnoreCase("Electronics");
    }

    @Test
    void searchedItem_Supplier_Success() {
        setAuthentication(supplierUser, Role.SUPPLIER);

        when(userRepo.findByEmail("supplier1@example.com")).thenReturn(Optional.of(supplierUser));
        when(itemRepo.findByCategoryIgnoreCaseAndSupplier_EmailIgnoreCase("Electronics", "supplier1@example.com"))
                .thenReturn(List.of(item));

        List<ItemResponse> result = itemService.searchedItem("Electronics");

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepo).findByEmail("supplier1@example.com");
        verify(itemRepo).findByCategoryIgnoreCaseAndSupplier_EmailIgnoreCase("Electronics", "supplier1@example.com");
    }

    @Test
    void searchedItem_Unauthorized_ReturnsEmpty() {
        setAuthentication(transporterUser, Role.TRANSPORTER);

        when(userRepo.findByEmail("transporter1@example.com"))
                .thenReturn(Optional.of(transporterUser));

        List<ItemResponse> result = itemService.searchedItem("Electronics");

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userRepo).findByEmail("transporter1@example.com");
        verify(itemRepo, never()).findByCategoryIgnoreCase(anyString());
        verify(itemRepo, never()).findByCategoryIgnoreCaseAndSupplier_EmailIgnoreCase(anyString(), anyString());
    }

    private void setAuthentication(UserEntity user, Role role) {
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}

