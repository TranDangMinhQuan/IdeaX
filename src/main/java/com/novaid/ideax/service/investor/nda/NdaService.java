package com.novaid.ideax.service.investor.nda;
import com.novaid.ideax.dto.investor.nda.NdaAgreementDTO;
import com.novaid.ideax.dto.investor.nda.NdaTemplateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NdaService {
    NdaTemplateDTO uploadTemplate(MultipartFile file);
    List<NdaTemplateDTO> getTemplates();

    NdaAgreementDTO signNda(Long userId, Long ndaTemplateId);
    boolean hasSigned(Long userId, Long ndaTemplateId);
}