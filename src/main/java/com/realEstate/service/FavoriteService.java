package com.realEstate.service;

import com.realEstate.model.Favorite;
import com.realEstate.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FavoriteService {
    private final FavoriteRepository repository = new FavoriteRepository();

    public void addFavorite(String userId, String propertyId, String status) throws IOException {
        String favoriteId = UUID.randomUUID().toString();
        Favorite favorite = new Favorite(favoriteId, userId, propertyId, status);
        repository.save(favorite);
    }

    public List<Favorite> getFavoritesByUser(String userId) throws IOException {
        return repository.findByUserId(userId);
    }

    public void updateFavoriteStatus(String userId, String propertyId, String status) throws IOException {
        repository.updateStatus(userId, propertyId, status);
    }

    public void removeFavorite(String userId, String propertyId) throws IOException {
        repository.delete(userId, propertyId);
    }
}

