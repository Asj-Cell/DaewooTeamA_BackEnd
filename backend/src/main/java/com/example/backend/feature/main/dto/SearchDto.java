package com.example.backend.feature.main.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 첫번쨰 메인 페이지
 * where art you staying ? 밑에 담기는 정보
 * 체크인 체크아웃 날짜 , 룸 게스트는 프론트에서 만든다고 가정하고 도시명("호텔이름, 도시이름" ) 만 넣었음
 */
@Getter
@AllArgsConstructor
public class SearchDto {

    private Long id;
    private String displayName;

}