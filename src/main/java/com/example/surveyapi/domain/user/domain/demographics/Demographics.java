package com.example.surveyapi.domain.user.domain.demographics;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class Demographics extends BaseEntity {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_id", nullable = false)
    private User user;

    @Column(name = "birth_date", nullable = false)
    private LocalDateTime birthDate;

    @Column(name = "gender", nullable = false)
    private Gender gender;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address", nullable = false, columnDefinition = "jsonb")
    private Address address;

    private Demographics(
        User user, LocalDateTime birthDate,
        Gender gender, Address address
    ) {
        this.user = user;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
    }

    public static Demographics create(
        User user, LocalDateTime birthDate,
        Gender gender, Address address
    ) {
        Demographics demographics = new Demographics(
            user, birthDate,
            gender, address);
        user.setDemographics(demographics);
        return demographics;
    }

}
