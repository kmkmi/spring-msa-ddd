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

    classDef clientLayer fill:#2196f3,stroke:#1976d2,stroke-width:2px,color:#ffffff
    classDef gatewayLayer fill:#9c27b0,stroke:#7b1fa2,stroke-width:2px,color:#ffffff
    classDef businessLayer fill:#4caf50,stroke:#388e3c,stroke-width:2px,color:#ffffff
    classDef dataLayer fill:#ff9800,stroke:#f57c00,stroke-width:2px,color:#ffffff
    classDef infraLayer fill:#e91e63,stroke:#c2185b,stroke-width:2px,color:#ffffff
    classDef dbLayer fill:#8bc34a,stroke:#689f38,stroke-width:2px,color:#ffffff
    classDef securityLayer fill:#ffc107,stroke:#ffa000,stroke-width:2px,color:#000000

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

    classDef eventSource fill:#2196f3,stroke:#1976d2,stroke-width:2px,color:#ffffff
    classDef collection fill:#4caf50,stroke:#388e3c,stroke-width:2px,color:#ffffff
    classDef messaging fill:#ff9800,stroke:#f57c00,stroke-width:2px,color:#ffffff
    classDef processing fill:#e91e63,stroke:#c2185b,stroke-width:2px,color:#ffffff
    classDef cache fill:#8bc34a,stroke:#689f38,stroke-width:2px,color:#ffffff
    classDef analytics fill:#ffc107,stroke:#ffa000,stroke-width:2px,color:#000000
    classDef anomaly fill:#f44336,stroke:#d32f2f,stroke-width:2px,color:#ffffff

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

    classDef adMetrics fill:#4caf50,stroke:#388e3c,stroke-width:2px,color:#ffffff
    classDef campaignMetrics fill:#ff9800,stroke:#f57c00,stroke-width:2px,color:#ffffff
    classDef hourlyMetrics fill:#e91e63,stroke:#c2185b,stroke-width:2px,color:#ffffff
    classDef dailyMetrics fill:#8bc34a,stroke:#689f38,stroke-width:2px,color:#ffffff

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

