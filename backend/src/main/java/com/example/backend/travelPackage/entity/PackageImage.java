package com.example.backend.travelPackage.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Getter @Setter
@Entity
@Table(name = "package_image")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PackageImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 파일명, 확장명, 이미지 사이즈를 1개의 컬럼으로 설정했습니다.
     * "https://.../images/malacca_main.jpg" 이런식으로 한번에 저장하면 편리할거 같아서
     */
    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private TravelPackage travelPackage;
}