package com.financial.api.service.impl;

import com.financial.api.dto.mapper.TransactionTypeMapper;
import com.financial.api.dto.request.TransactionTypeCreateRequest;
import com.financial.api.dto.request.TransactionTypeUpdateRequest;
import com.financial.api.dto.response.TransactionTypeResponse;
import com.financial.api.entity.TransactionType;
import com.financial.api.entity.User;
import com.financial.api.repository.TransactionTypeRepository;
import com.financial.api.service.TransactionTypeService;
import com.financial.api.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TransactionTypeServiceImpl implements TransactionTypeService {
    private final TransactionTypeRepository transactionTypeRepository;
    private final TransactionTypeMapper transactionTypeMapper;
    private final AuthenticationUtil authenticationUtil;


    @Override
    public Page<TransactionTypeResponse> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();

        // Get system + user's own
        Page<TransactionType> categories = transactionTypeRepository
                .findByUserOrIsSystemTrueAndIsDeletedFalse(currentUser, pageable);

        return categories.map(transactionTypeMapper::toResponse);
    }

    @Override
    public TransactionTypeResponse createTransactionType(TransactionTypeCreateRequest request) {
        User currentUser =  getCurrentUser();

        if (transactionTypeRepository.existsByUserAndNameIgnoreCaseAndIsDeletedFalse(currentUser, request.getName())) {
            throw new IllegalArgumentException("You already have a transactionType with name: " + request.getName());
        }

        TransactionType transactionType = transactionTypeMapper.toEntity(request);
        transactionType.setUser(currentUser);
        transactionType.setIsSystem(false);

        TransactionType savedTransactionType = transactionTypeRepository.save(transactionType);
        return transactionTypeMapper.toResponse(savedTransactionType);
    }

    @Override
    public TransactionTypeResponse updateTransactionType(Long id, TransactionTypeUpdateRequest request) {
        User currentUser = getCurrentUser();

        TransactionType existing = transactionTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TransactionType not found with ID: " + id));

        // Validate user can update this transactionType
        validateUpdatePermission(existing, currentUser);

        if (!existing.getName().equals(request.getName()) &&
                transactionTypeRepository.existsByUserAndNameIgnoreCaseAndIsDeletedFalse(currentUser, request.getName())) {
            throw new IllegalArgumentException("You already have a transactionType with code: " + request.getName());
        }

        // Update fields
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());

        TransactionType updatedTransactionType = transactionTypeRepository.save(existing);
        return transactionTypeMapper.toResponse(updatedTransactionType);
    }

    @Override
    public TransactionTypeResponse getTransactionTypeById(Long id) {
        TransactionType transactionType = transactionTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TransactionType not found with ID: " + id));

        if (transactionType.getIsDeleted()) {
            throw new NoSuchElementException("TransactionType not found with ID: " + id);
        }

        return transactionTypeMapper.toResponse(transactionType);
    }

    @Override
    public void deleteTransactionType(Long id) {
        User currentUser = getCurrentUser();

        TransactionType transactionType = transactionTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TransactionType not found with ID: " + id));

        // Validate user can delete this transactionType
        validateUpdatePermission(transactionType, currentUser);

        // Soft delete
        transactionType.setIsDeleted(true);
        transactionTypeRepository.save(transactionType);
    }

    /**
     * Get the currently authenticated user
     */
    private User getCurrentUser() {
        return authenticationUtil.getCurrentUser();
    }

    /**
     * Validate if the current user can update the given transactionType
     */
    private void validateUpdatePermission(TransactionType transactionType, User currentUser) {
        // Cannot update system currencies
        if (transactionType.getIsSystem()) {
            throw new AccessDeniedException("Cannot modify system currencies");
        }

        // Can only update own currencies
        if (transactionType.getUser() == null || !transactionType.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own currencies");
        }
    }

}
