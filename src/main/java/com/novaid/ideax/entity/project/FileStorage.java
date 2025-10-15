package com.novaid.ideax.entity.project;

import com.novaid.ideax.entity.auth.Account;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Table(name = "file_storage")
public class FileStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fileName; // Tên file duy nhất trên server (uuid.png)

    @Column(nullable = false)
    private String originalFileName; // Tên file gốc

    @Column(nullable = false)
    private String filePath; // Đường dẫn truy cập (/logos/uuid.png)

    private String fileType; // Kiểu MIME (image/png)

    private long fileSize; // Kích thước (bytes)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private Account uploader;

    @CreationTimestamp
    private LocalDateTime createdAt;
}