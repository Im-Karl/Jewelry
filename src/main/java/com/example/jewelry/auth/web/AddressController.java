package com.example.jewelry.auth.web;

import com.example.jewelry.shared.response.MessageResponse;
import com.example.jewelry.shared.security.SecurityUtil;
import com.example.jewelry.auth.dto.AddressRequest;
import com.example.jewelry.auth.dto.AddressResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getMyAddresses() {
        UUID userId = SecurityUtil.getCurrentUserId().orElseThrow();
        return ResponseEntity.ok(addressService.getMyAddresses(userId));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(@RequestBody @Valid AddressRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId().orElseThrow();
        return ResponseEntity.ok(addressService.createAddress(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable UUID id,
            @RequestBody @Valid AddressRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId().orElseThrow();
        return ResponseEntity.ok(addressService.updateAddress(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteAddress(@PathVariable UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId().orElseThrow();
        addressService.deleteAddress(userId, id);
        return ResponseEntity.ok(new MessageResponse("Đã xóa địa chỉ thành công"));
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId().orElseThrow();
        return ResponseEntity.ok(addressService.setDefaultAddress(userId, id));
    }
}