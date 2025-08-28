package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.requestor.id <> :userId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequestorIdNotWithItems(@Param("userId") Long userId);

}
