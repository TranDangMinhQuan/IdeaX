package com.novaid.ideax.repository.startup;

import com.novaid.ideax.entity.startup.Project;
import com.novaid.ideax.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStartup_Id(Long accountId);
    List<Project> findByStatus(ProjectStatus status);
}
