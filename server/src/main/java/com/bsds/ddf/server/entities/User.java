package com.bsds.ddf.server.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userid;

    @Column
    private String email;

    @Column
    private String password;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private String userLocation;
}
