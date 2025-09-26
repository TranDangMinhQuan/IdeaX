package com.novaid.ideax.entity.investor.meet;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.investor.nda.NdaAgreement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meeting_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomCode; // mã phòng Jitsi
    private String topic;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Account createdBy; // Investor tạo

    private String recordUrl; // link record lưu từ Jitsi


}
