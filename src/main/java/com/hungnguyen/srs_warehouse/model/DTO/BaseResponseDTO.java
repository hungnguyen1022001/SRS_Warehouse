package com.hungnguyen.srs_warehouse.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseResponseDTO<T> {
    private int status;
    private String message;
    private T data;
}

