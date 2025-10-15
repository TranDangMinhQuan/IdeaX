package com.novaid.ideax.entity.project;

import com.novaid.ideax.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_files")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProjectFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nó thuộc về dự án nào?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Nó trỏ đến file nào trong kho?
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "file_storage_id", nullable = false)
    private FileStorage fileStorage;

    // Nó mang bối cảnh nghiệp vụ gì? (Pitch Deck, Video...)
    @Enumerated(EnumType.STRING)
    @Column(name = "project_file_type") // Đổi tên để tránh trùng với cột trong FileStorage
    private FileType type;
}