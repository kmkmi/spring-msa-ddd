# 광고 데이터 파이프라인 아키텍처

## 전체 시스템 아키텍처

```mermaid
graph TB
    %% 클라이언트 레이어
    subgraph "Client Layer"
        A[광고 클라이언트]
        B[웹 브라우저]
        C[모바일 앱]
    end

    %% API Gateway 레이어
    subgraph "API Gateway Layer"
        D[API Gateway<br/>:8080]
    end

    %% 비즈니스 서비스 레이어
    subgraph "Business Services"
        E[User Service<br/>:8082]
        F[Campaign Service<br/>:8081]
        G[Ad Service<br/>:8083]
        H[Publisher Service<br/>:8084]
    end

    %% 데이터 파이프라인 레이어
    subgraph "Data Pipeline"
        I[Data Collector Service<br/>:8085]
        J[Data Processor Service<br/>:8086]
    end

    %% 인프라 레이어
    subgraph "Infrastructure"
        K[Kafka<br/>:9092]
        L[Redis<br/>:6379]
        M[Consul<br/>:8500]
        N[Config Service<br/>:8888]
    end

    %% 데이터베이스 레이어
    subgraph "Databases"
        O[User DB<br/>:3310]
        P[Campaign DB<br/>:3308]
        Q[Ad DB<br/>:3307]
        R[Publisher DB<br/>:3309]
    end

    %% 보안 레이어
    subgraph "Security"
        S[JWKS Service<br/>:8090]
    end

    %% 연결
    A --> D
    B --> D
    C --> D
    
    D --> E
    D --> F
    D --> G
    D --> H
    
    %% 데이터 파이프라인 연결
    A --> I
    B --> I
    C --> I
    
    I --> K
    K --> J
    J --> L
    
    %% 서비스 디스커버리
    E --> M
    F --> M
    G --> M
    H --> M
    I --> M
    J --> M
    
    %% 설정 관리
    E --> N
    F --> N
    G --> N
    H --> N
    I --> N
    J --> N
    
    %% 데이터베이스 연결
    E --> O
    F --> P
    G --> Q
    H --> R
    
    %% 보안 연결
    D --> S
    E --> S
    F --> S
    G --> S
    H --> S

    %% 스타일
    classDef clientLayer fill:#e1f5fe
    classDef gatewayLayer fill:#f3e5f5
    classDef businessLayer fill:#e8f5e8
    classDef dataLayer fill:#fff3e0
    classDef infraLayer fill:#fce4ec
    classDef dbLayer fill:#f1f8e9
    classDef securityLayer fill:#fff8e1

    class A,B,C clientLayer
    class D gatewayLayer
    class E,F,G,H businessLayer
    class I,J dataLayer
    class K,L,M,N infraLayer
    class O,P,Q,R dbLayer
    class S securityLayer
```

## 데이터 파이프라인 상세 아키텍처

```mermaid
graph LR
    %% 이벤트 소스
    subgraph "Event Sources"
        A1[광고 노출 이벤트]
        A2[광고 클릭 이벤트]
        A3[광고 전환 이벤트]
    end

    %% 데이터 수집
    subgraph "Data Collection"
        B[Data Collector Service<br/>:8085]
    end

    %% 메시징
    subgraph "Message Queue"
        C[Kafka<br/>ad-events 토픽]
    end

    %% 데이터 처리
    subgraph "Data Processing"
        D[Data Processor Service<br/>:8086]
    end

    %% 캐시 및 메트릭
    subgraph "Cache & Metrics"
        E[Redis<br/>실시간 메트릭]
    end

    %% 분석 결과
    subgraph "Analytics"
        F[광고별 메트릭]
        G[캠페인별 메트릭]
        H[시간별 집계]
        I[일별 집계]
    end

    %% 이상 탐지
    subgraph "Anomaly Detection"
        J[CTR 이상 탐지]
        K[클릭 패턴 탐지]
    end

    %% 연결
    A1 --> B
    A2 --> B
    A3 --> B
    
    B --> C
    C --> D
    D --> E
    
    E --> F
    E --> G
    E --> H
    E --> I
    
    D --> J
    D --> K

    %% 스타일
    classDef eventSource fill:#e3f2fd
    classDef collection fill:#e8f5e8
    classDef messaging fill:#fff3e0
    classDef processing fill:#fce4ec
    classDef cache fill:#f1f8e9
    classDef analytics fill:#fff8e1
    classDef anomaly fill:#ffebee

    class A1,A2,A3 eventSource
    class B collection
    class C messaging
    class D processing
    class E cache
    class F,G,H,I analytics
    class J,K anomaly
```

## 데이터 플로우 시퀀스

```mermaid
sequenceDiagram
    participant Client as 광고 클라이언트
    participant DC as Data Collector
    participant Kafka as Kafka
    participant DP as Data Processor
    participant Redis as Redis
    participant Alert as 이상 탐지

    Client->>DC: 광고 이벤트 전송
    Note over DC: 이벤트 검증 및 전처리
    
    DC->>Redis: 실시간 메트릭 업데이트
    DC->>Kafka: 이벤트 전송
    
    Kafka->>DP: 이벤트 소비
    Note over DP: 스트림 처리 및 분석
    
    DP->>Redis: 집계 메트릭 업데이트
    DP->>Redis: 시간별/일별 집계
    DP->>Alert: 이상 탐지
    
    Note over Alert: CTR > 10% 또는<br/>세션당 클릭 > 10회
    
    DP->>Kafka: 메트릭 이벤트 발행
```

## Redis 메트릭 구조

```mermaid
graph TD
    subgraph "Redis Metrics Structure"
        A[ad:metrics:1:impressions]
        B[ad:metrics:1:clicks]
        C[ad:metrics:1:conversions]
        D[ad:metrics:1:ctr]
        E[ad:metrics:1:cpc]
        F[ad:metrics:1:cpm]
        
        G[campaign:metrics:1:impressions]
        H[campaign:metrics:1:clicks]
        I[campaign:metrics:1:conversions]
        J[campaign:metrics:1:ctr]
        
        K[hourly:metrics:1:14:impressions]
        L[hourly:metrics:1:14:clicks]
        
        M[daily:metrics:1:2024-01-15:impressions]
        N[daily:metrics:1:2024-01-15:clicks]
    end

    %% 광고별 메트릭
    A --> D
    B --> D
    C --> E
    A --> F
    
    %% 캠페인별 메트릭
    G --> J
    H --> J
    I --> J
    
    %% 시간별 집계
    K --> L
    
    %% 일별 집계
    M --> N

    %% 스타일
    classDef adMetrics fill:#e8f5e8
    classDef campaignMetrics fill:#fff3e0
    classDef hourlyMetrics fill:#fce4ec
    classDef dailyMetrics fill:#f1f8e9

    class A,B,C,D,E,F adMetrics
    class G,H,I,J campaignMetrics
    class K,L hourlyMetrics
    class M,N dailyMetrics
```

## 기술 스택

### 데이터 파이프라인 기술 스택

| 컴포넌트 | 기술 | 역할 |
|---------|------|------|
| **Data Collector** | Spring Boot + Kafka | 이벤트 수집 및 전송 |
| **Data Processor** | Spring Boot + Kafka | 스트림 처리 및 분석 |
| **Message Queue** | Apache Kafka | 이벤트 스트리밍 |
| **Cache** | Redis | 실시간 메트릭 저장 |
| **Service Discovery** | Consul | 서비스 디스커버리 |
| **Configuration** | Spring Cloud Config | 중앙화된 설정 관리 |

### 성능 지표

- **처리량**: 초당 10,000+ 이벤트 처리
- **지연시간**: 평균 50ms 이하
- **가용성**: 99.9% 이상
- **확장성**: 수평 확장 지원

### 모니터링 지표

- **서비스 헬스**: `/actuator/health`
- **메트릭**: Redis 키 기반 실시간 모니터링
- **알림**: 이상 탐지 시 자동 알림
- **로그**: 구조화된 로그 및 추적 