package com.gamelink.gamelinkapi.models;

import com.gamelink.gamelinkapi.enums.GameTime;
import com.gamelink.gamelinkapi.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class UserProfile extends BaseModel {
    @NotBlank
    private String name;

    @OneToOne
    @NotNull
    private User user;

    @NotBlank
    @Column(length = 160)
    private String bio;

    @NotNull
    private LocalDate entryDate = LocalDate.now();
    private LocalDate birthdayDate;

    private List<GameTime> gameTimes = List.of();

    @NotNull
    private Gender gender;
}
