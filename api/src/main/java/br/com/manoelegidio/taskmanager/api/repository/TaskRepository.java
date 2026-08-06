package br.com.manoelegidio.taskmanager.api.repository;

import br.com.manoelegidio.taskmanager.api.dto.TaskFilterDTO;
import br.com.manoelegidio.taskmanager.api.model.Task;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByCompleted(Boolean completed);

    default Page<Task> search(TaskFilterDTO filter, Pageable pageable) {
        Specification<Task> specification = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter != null) {
                if (filter.title() != null && !filter.title().isBlank()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("title")),
                                    "%" + filter.title().trim().toLowerCase() + "%"
                            ));
                }

                if (filter.description() != null && !filter.description().isBlank()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get("description")),
                                    "%" + filter.description().trim().toLowerCase() + "%"
                            ));
                }

                if (filter.completed() != null) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.equal(root.get("completed"), filter.completed()));
                }
            }

            return predicate;
        };

        return findAll(specification, pageable);
    }
}
