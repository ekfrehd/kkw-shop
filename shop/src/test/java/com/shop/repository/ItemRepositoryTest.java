package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.shop.entity.QItem;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

@SpringBootTest //통합 테스트용 스프링 부트 제공
@TestPropertySource(locations="classpath:application-test.properties") //테스트용 h2 사용
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository; //생성자 자동 주입

    @Test //해당 메소드를 테스트 코드로 진행
    @DisplayName("상품 저장 테스트") // 테스트명 노출 (Ctrl + Shift + F10으로 실행)
    public void createItemTest() {
        Item item = new Item();  // 엔티티에 있는 Item을 item으로 생성
        item.setItemNm("테스트 상품");  // 상품명
        item.setPrice(10000);  // 가격
        item.setItemDetail("테스트 상품 상세 설명");  // 상세 설명
        item.setItemSellStatus(ItemSellStatus.SELL); // 상품 판매 상태
        item.setStockNumber(100);  // 재고 수량
        item.setRegTime(LocalDateTime.now()); // 생성 날짜
        item.setUpdateTime(LocalDateTime.now());  // 수정 날짜
        Item savedItem = itemRepository.save(item);  // itemRepository에 save 처리
        System.out.println(savedItem.toString());
        //Item(id=1, itemNm=테스트 상품, price=10000, stockNumber=100, itemDetail=테스트 상품 상세 설명, itemSellStatus=SELL)
    }

    public void createItemList(){
        for(int i=1;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100); item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    } // 테스트 코드용 상품 10개를 생성

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        this.createItemList();  // 위에 있는 64행에 있는 createItemList()를 선 실행 한다.
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1"); // 파라미터로 테스트 상품1을 찾는다.
        for(Item item : itemList){
            System.out.println(item.toString());
        }  // 조회 결과로 얻은 item 객체들을 출력한다.
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList(); // 위에 있는 64행에 있는 createItemList()를 선 실행 한다.
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList(); // 위에 있는 64행에 있는 createItemList()를 선 실행 한다.
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        // 데이터 베이스 저장된 가격은 10001~10010원임
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }
    @Test
    @DisplayName("nativeQuery 이용한 상품 조회 테스트")
    public void findByItemDetailNative(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @PersistenceContext // EntityManager 빈 주입
    EntityManager em; // 영속성 컨텍스를 사용

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); // JPAQueryFactory를 이용하여 쿼리 통적 생성
        QItem qItem = QItem.item;  //QueryDSL을 통해 쿼리를 생성하기 위해 플러그인을 통해 자동으로 생성된 QItem 객체 이용
        JPAQuery<Item> query  = queryFactory.selectFrom(qItem) // select from
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL)) // where 상품 상태 = 판매중
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%")) // 상품 상세 설명 = ""
                .orderBy(qItem.price.desc()); // 가격으로 내림 차순 정렬

        List<Item> itemList = query.fetch(); // 쿼리 결과를 리스트로 반환
        // List<T> fetch() : 조회 결과 리스트 반환
        // T fetchOne :  조회 대상이 1건인 경우 제네릭으로 지정한 타입 반환
        // T fetchFirst() :  조회 대상 중 1건만 반환
        // Long fetchCount() : 조회 대상 개수 반환
        // QueryResult<T> fetchResults() : 조회한 리스트와 전체 개수를 포함한 QueryResults 반환

        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){  // 테스트 상품 생성 1~5번은 SELL , 6~10은 SOLD_OUT
        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){

        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // 쿼리에 들어갈 조건을 만들어주는 빌더
        QItem item = QItem.item;  // Q도메인에 있는 item 객체를 활용
        String itemDetail = "테스트 상품 상세 설명";  // 조건 1 상세정보
        int price = 10003;  // 조건2 금액
        String itemSellStat = "SELL"; // 조건 3 SELL 상태

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));  // itemDetail = 테스트 상품 상세 설명
        booleanBuilder.and(item.price.gt(price)); // and price >= 10003
        System.out.println(ItemSellStatus.SELL);
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){ // 판매상태가 SELL 일때 만
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL)); // SELL 만 조건에 추가
        }

        Pageable pageable = PageRequest.of(0, 5);  // 페이징 처리를 위한 부분
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " + itemPagingResult. getTotalElements ());

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem: resultItemList){
            System.out.println(resultItem.toString());
        }
    }

}