package com.novaid.ideax.service.investor.nda.impl;


import com.novaid.ideax.dto.investor.nda.NdaAgreementDTO;
import com.novaid.ideax.dto.investor.nda.NdaTemplateDTO;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.investor.nda.NdaAgreement;
import com.novaid.ideax.entity.investor.nda.NdaTemplate;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.investor.nda.NdaAgreementRepository;
import com.novaid.ideax.repository.investor.nda.NdaTemplateRepository;
import com.novaid.ideax.service.investor.nda.NdaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NdaServiceImpl implements NdaService {

    private final NdaTemplateRepository templateRepo;
    private final NdaAgreementRepository agreementRepo;
    private final AccountRepository accountRepo;

    @Override
    public NdaTemplateDTO uploadTemplate(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/nda");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            NdaTemplate template = NdaTemplate.builder()
                    .fileUrl("/uploads/nda/" + fileName)
                    .uploadedAt(LocalDateTime.now())
                    .build();
            return mapToTemplateDTO(templateRepo.save(template));
        } catch (Exception e) {
            throw new RuntimeException("Upload NDA failed: " + e.getMessage());
        }
    }

    @Override
    public List<NdaTemplateDTO> getTemplates() {
        return templateRepo.findAll().stream()
                .map(this::mapToTemplateDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NdaAgreementDTO signNda(Long userId, Long ndaTemplateId) {
        return agreementRepo.findByUserIdAndNdaTemplateId(userId, ndaTemplateId)
                .map(existing -> {
                    if (!existing.isSigned()) {
                        existing.setSigned(true);
                        existing.setSignedAt(LocalDateTime.now());
                        agreementRepo.save(existing);
                    }
                    return mapToAgreementDTO(existing);
                })
                .orElseGet(() -> {
                    Account user = accountRepo.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    NdaTemplate template = templateRepo.findById(ndaTemplateId)
                            .orElseThrow(() -> new RuntimeException("Template not found"));

                    NdaAgreement newAgreement = NdaAgreement.builder()
                            .user(user)
                            .ndaTemplate(template)
                            .signed(true)
                            .signedAt(LocalDateTime.now())
                            .build();

                    return mapToAgreementDTO(agreementRepo.save(newAgreement));
                });
    }

    @Override
    public boolean hasSigned(Long userId, Long ndaTemplateId) {
        return agreementRepo.findByUserIdAndNdaTemplateId(userId, ndaTemplateId)
                .map(NdaAgreement::isSigned)
                .orElse(false);
    }

    private NdaTemplateDTO mapToTemplateDTO(NdaTemplate t) {
        return NdaTemplateDTO.builder()
                .id(t.getId())
                .fileUrl(t.getFileUrl())
                .uploadedAt(t.getUploadedAt())
                .build();
    }

    private NdaAgreementDTO mapToAgreementDTO(NdaAgreement nda) {
        return NdaAgreementDTO.builder()
                .id(nda.getId())
                .signed(nda.isSigned())
                .signedAt(nda.getSignedAt())
                .userId(nda.getUser().getId())
                .ndaTemplateId(nda.getNdaTemplate().getId())
                .build();
    }
}