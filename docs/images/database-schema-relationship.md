# 데이터베이스 스키마 관계

```mermaid
erDiagram
    PUBLISHERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar company_name
        varchar phone_number
        text description
        enum status
        enum publisher_type
        decimal balance
        decimal total_spent
        varchar website_url
        varchar contact_address
        datetime created_at
        datetime updated_at
    }
    
    USERS {
        bigint id PK
        varchar email UK
        varchar name
        varchar password
        enum status
        datetime created_at
        datetime updated_at
    }
    
    CAMPAIGNS {
        bigint id PK
        varchar name
        text description
        bigint publisher_id FK
        enum status
        enum campaign_type
        decimal budget_amount
        decimal daily_budget
        datetime start_date
        datetime end_date
        varchar target_audience
        varchar target_locations
        datetime created_at
        datetime updated_at
    }
    
    ADVERTISEMENTS {
        bigint id PK
        bigint campaign_id FK
        varchar title
        text description
        varchar image_url
        varchar video_url
        varchar landing_page_url
        enum ad_type
        enum status
        decimal bid_amount
        decimal daily_budget
        decimal total_spent
        bigint impressions
        bigint clicks
        decimal ctr
        datetime start_date
        datetime end_date
        datetime created_at
        datetime updated_at
    }
    
    PUBLISHERS ||--o{ CAMPAIGNS : "has"
    CAMPAIGNS ||--o{ ADVERTISEMENTS : "contains"
} 