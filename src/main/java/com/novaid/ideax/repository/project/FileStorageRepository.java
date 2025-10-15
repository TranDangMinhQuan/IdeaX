package com.novaid.ideax.repository.project;


import com.novaid.ideax.entity.project.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {
    // Spring Data JPA sẽ tự động cung cấp các phương thức CRUD cho bạn
}
