package com.jtk.ps.api.model;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "tutorials")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tutorial {
    @Id
    @Column(name = "id")
    private long id;
  
    @Column(name = "title")
    private String title;
  
    @Column(name = "description")
    private String description;
  
    @Column(name = "published")
    private boolean published;
}
