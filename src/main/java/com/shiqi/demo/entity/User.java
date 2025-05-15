package com.shiqi.demo.entity;

import lombok.Data;
import lombok.ToString;
import java.io.Serializable;

@Data
@ToString
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
}