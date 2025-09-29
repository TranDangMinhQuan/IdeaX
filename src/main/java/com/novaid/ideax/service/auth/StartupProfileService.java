package com.novaid.ideax.service.auth;

import com.novaid.ideax.dto.account.StartupProfileUpdateDTO;
import com.novaid.ideax.entity.auth.StartupProfile;

public interface StartupProfileService {
    StartupProfile updateProfile(Long accountId, StartupProfileUpdateDTO dto);
    StartupProfile getProfile(Long accountId);
    StartupProfile uploadProfilePicture(Long accountId, org.springframework.web.multipart.MultipartFile file, jakarta.servlet.http.HttpServletRequest request);
}
