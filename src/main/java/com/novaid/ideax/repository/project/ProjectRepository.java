package com.novaid.ideax.repository.project;

import com.novaid.ideax.entity.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStartup_Id(Long accountId);
//    List<Project> findByStatus(ProjectStatus status);
}
