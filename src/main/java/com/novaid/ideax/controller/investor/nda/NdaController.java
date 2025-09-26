package com.novaid.ideax.controller.investor.nda;


import com.novaid.ideax.dto.investor.nda.NdaAgreementDTO;

import com.novaid.ideax.dto.investor.nda.NdaTemplateDTO;
import com.novaid.ideax.service.investor.nda.NdaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
@RequestMapping("/api/nda")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class NdaController {

    private final NdaService ndaService;

    // ✅ Admin upload file NDA
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public NdaTemplateDTO uploadTemplate(@RequestParam("file") MultipartFile file) {
        return ndaService.uploadTemplate(file);
    }

    // ✅ Xem tất cả NDA templates (ai cũng xem được)
    @GetMapping("/templates")
    public List<NdaTemplateDTO> getTemplates() {
        return ndaService.getTemplates();
    }

    // ✅ User ký NDA
    @PreAuthorize("hasAnyRole('INVESTOR','STARTUP')")
    @PostMapping("/sign")
    public NdaAgreementDTO signNda(@RequestParam Long userId,
                                   @RequestParam Long ndaTemplateId) {
        return ndaService.signNda(userId, ndaTemplateId);
    }

    // ✅ Check user đã ký chưa
    @PreAuthorize("hasAnyRole('INVESTOR','STARTUP','ADMIN')")
    @GetMapping("/check")
    public boolean hasSigned(@RequestParam Long userId,
                             @RequestParam Long ndaTemplateId) {
        return ndaService.hasSigned(userId, ndaTemplateId);
    }
}