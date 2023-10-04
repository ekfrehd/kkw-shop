package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;

//ItemRepository 인터페이스 명 블럭설정 우클릭 Go to -> Test로 테스트 코드 생성
public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    // JpaRepository<엔티티 타입 클래스, 기본키 타입>
    // <S extends T> save(S entity) : 엔티티 저장 및 수정
    // void delete(T entity) : 엔티티 삭제
    // conut() : 엔티티 총 개수 반환
    // Iterable<T> findAll() : 모든 엔티티 조회
    // QuerydslPredicateExecutor<Item>, ItemRepositoryCustom 인터페이스 상속 추가

    List<Item> findByItemNm(String itemNm);
    // 상품명 itemNm 으로 데이터를 조회
    // by 뒤에 필드명인 ItemNm을 메소드의 이름에 붙여줌
    // 엔티티명은 생략가능 findItembyItemNm을 findByItemNm로 메소드명을 만들어 줌
    // 매개 변수로 검색할 때 상품명을 변수를 넘겨 준다.

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    // 상품을 상품명과 상품 상세설명을 or 조건으로 조회하는 쿼리 메서드

    List<Item> findByPriceLessThan(Integer price);
    // price로 넘어온 값보다 작은 상품 데이터를 조회하는 쿼리 메소드

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    // OrderBy Price Desc ( price 값을 적용하고 오름차순 정렬 진행)

    @Query("select i from Item i where i.itemDetail like " +
            "%:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);
    // 조건이 복잡해질 때 사용하는 @Query 어노테이션 (JPQL)
    // from Item i : Item 테이블을 i로 별칭 등록
    // select i : 찾는 필드는 i로 모든 값을 지정
    // where i.itemDetail : 조건은 상품 상세 설명 like
    // @Param("itemDetail") 매개값으로 들어온 값이 들어가도록 설정 -> "%:itemDetail%

    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
    // 네이티브 쿼리용 : 기존 쿼리를 그대로 활용할 수 있다. value= "sql문" nativeQuery = true
    // sql 쿼리 고수가 사용하는 방법

}