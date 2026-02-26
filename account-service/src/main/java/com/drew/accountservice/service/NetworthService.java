package com.drew.accountservice.service;

import com.drew.accountservice.dto.NetworthOutputDto;

public interface NetworthService {
    NetworthOutputDto getUserNetworth(String keycloakUserId);
}
