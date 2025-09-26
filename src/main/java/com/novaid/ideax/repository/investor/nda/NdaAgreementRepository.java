package com.novaid.ideax.repository.investor.nda;
import com.novaid.ideax.entity.investor.nda.NdaAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NdaAgreementRepository extends JpaRepository<NdaAgreement, Long> {
    Optional<NdaAgreement> findByUserIdAndNdaTemplateId(Long userId, Long ndaTemplateId);
}