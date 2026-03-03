package com.example.jewelry.auth.domain;

import com.example.jewelry.auth.dto.UpdateUserRequestDto;
import com.example.jewelry.auth.dto.UserDto;
import com.example.jewelry.auth.web.UserService;
import com.example.jewelry.consultation.service.FengShuiCalculator;
import com.example.jewelry.consultation.service.ZodiacCalculator;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.storage.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService; // Để upload ảnh
    private final FengShuiCalculator fengShuiCalculator;
    private final ZodiacCalculator zodiacCalculator;

    @Override
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto updateProfile(UUID userId, UpdateUserRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName());
        }
        if (StringUtils.hasText(request.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        // 3. Xử lý Upload Avatar (Nếu có gửi file)
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = fileStorageService.storeFile(request.getAvatar());
            user.setAvatarUrl(avatarUrl);
        }

        // 4. Xử lý Ngày sinh & Tính lại Phong Thủy (Killer Feature)
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());

            LocalDate birthDate = request.getBirthDate();
            // Tự động tính lại mệnh & cung
            String element = fengShuiCalculator.calculateElement(birthDate.getYear());
            String zodiac = zodiacCalculator.calculateZodiac(birthDate.getDayOfMonth(),birthDate.getMonthValue());

            user.setFengShuiElement(element);
            user.setZodiacSign(zodiac);
        }

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user,UserDto.class)).toList();
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
//        userRepository.delete(user);

        user.setDeleted(true);
    }

}