package com.example.jewelry.auth.domain;


import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.auth.dto.AddressRequest;
import com.example.jewelry.auth.dto.AddressResponse;
import com.example.jewelry.auth.web.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponse> getMyAddresses(UUID userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse createAddress(UUID userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        List<Address> currentAddresses = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
        boolean isFirst = currentAddresses.isEmpty();
        boolean willBeDefault = request.isDefault() || isFirst;

        if (willBeDefault && !isFirst) {
            removeOldDefault(currentAddresses);
        }

        Address address = Address.builder()
                .user(user)
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .street(request.getStreet())
                .ward(request.getWard())
                .district(request.getDistrict())
                .city(request.getCity())
                .isDefault(willBeDefault)
                .build();

        return mapToDto(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request) {
        Address address = getAddressAndVerifyOwner(userId, addressId);

        if (request.isDefault() && !address.isDefault()) {
            List<Address> currentAddresses = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
            removeOldDefault(currentAddresses);
        }

        address.setRecipientName(request.getRecipientName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setStreet(request.getStreet());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        if (request.isDefault()) {
            address.setDefault(true);
        }

        return mapToDto(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        Address address = getAddressAndVerifyOwner(userId, addressId);

        if (address.isDefault()) {
            throw new RuntimeException("Không thể xóa địa chỉ mặc định. Vui lòng chọn địa chỉ khác làm mặc định trước!");
        }

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(UUID userId, UUID addressId) {
        Address address = getAddressAndVerifyOwner(userId, addressId);

        List<Address> currentAddresses = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
        removeOldDefault(currentAddresses);

        address.setDefault(true);
        return mapToDto(addressRepository.save(address));
    }

    // --- CÁC HÀM TIỆN ÍCH DÙNG CHUNG ---
    private Address getAddressAndVerifyOwner(UUID userId, UUID addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền truy cập địa chỉ này");
        }
        return address;
    }

    private void removeOldDefault(List<Address> addresses) {
        addresses.stream()
                .filter(Address::isDefault)
                .forEach(a -> {
                    a.setDefault(false);
                    addressRepository.save(a);
                });
    }

    private AddressResponse mapToDto(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .phoneNumber(address.getPhoneNumber())
                .street(address.getStreet())
                .ward(address.getWard())
                .district(address.getDistrict())
                .city(address.getCity())
                .isDefault(address.isDefault())
                .build();
    }
}