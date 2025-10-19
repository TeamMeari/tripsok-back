package com.tripsok_back.model.place;

import com.tripsok_back.support.BaseModifiableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Lob;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLACE_INTRO")
public class PlaceIntro extends BaseModifiableEntity {

    @Id
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "OPEN_DATE")
    private String openDate;

    @Column(name = "REST_DATE")
    private String restDate;

    @Column(name = "USE_TIME")
    private String useTime;

    // Full raw JSON snapshot of intro item for completeness
    @Lob
    @Column(name = "RAW_JSON")
    private String rawJson;

    @OneToOne
    @MapsId
    @JoinColumn(name = "ID")
    private Place place;
}
