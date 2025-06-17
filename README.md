# Activity Tracker Backend

一個基於 Spring Boot 的活動追蹤後端 API，提供用戶註冊、登入以及活動管理功能。

## 技術棧

- **Java**: 21
- **Spring Boot**: 3.5.0
- **Spring Security**: JWT 身份驗證
- **Spring Data JPA**: 數據持久化
- **Microsoft SQL Server**: 數據庫
- **Maven**: 依賴管理
- **Lombok**: 減少樣板代碼
- **JWT**: 基於 JSON Web Token 的身份驗證

## 功能特性

### 用戶管理
- 用戶註冊（包含密碼強度驗證）
- 用戶登入（JWT Token 驗證）
- 密碼加密存儲（BCrypt）

### 活動管理
- 創建活動記錄
- 按日期查詢活動
- 更新活動信息
- 刪除活動記錄
- 用戶權限控制

### 安全特性
- JWT Token 驗證
- 用戶權限隔離
- 密碼加密存儲
- API 端點保護

## 數據模型

### User 用戶
```java
- id: UUID (主鍵)
- email: String (唯一)
- passwordHash: String
- createdAt: LocalDateTime
- activities: List<Activity> (一對多關係)
```

### Activity 活動
```java
- id: UUID (主鍵)
- activityDate: LocalDate
- title: String
- category: String
- startTime: LocalTime
- endTime: LocalTime
- notes: String
- mood: Byte (1-5)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- user: User (多對一關係)
```

## API 端點

### 用戶相關 API
```
POST /api/users/register  - 用戶註冊
POST /api/users/login     - 用戶登入
```

### 活動相關 API
```
POST   /api/activities/create        - 創建活動
GET    /api/activities/byDate        - 按日期查詢活動
PUT    /api/activities/update/{id}   - 更新活動
DELETE /api/activities/delete/{id}   - 刪除活動
```

## 環境要求

- Java 21+
- Maven 3.6+
- Microsoft SQL Server
- IDE（推薦 IntelliJ IDEA 或 VS Code）

## 安裝與運行

### 1. clone項目
```bash
git clone https://github.com/d361120041/activity-tracker-backend.git
cd activity-tracker-backend
```

### 2. 數據庫配置
確保 SQL Server 運行在本地，並創建數據庫：
```sql
CREATE DATABASE activity_tracker_db;
```

### 3. 配置文件
在 `src/main/resources/application.properties` 中配置數據庫連接：
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=activity_tracker_db;encrypt=false
spring.datasource.username=sa
spring.datasource.password=P@ssw0rd
```

### 4. 運行應用
```bash
mvn spring-boot:run
```

應用將在 `http://localhost:8080` 啟動。

## API 使用示例

### 用戶註冊
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123!"
  }'
```

### 用戶登入
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123!"
  }'
```

### 創建活動
```bash
curl -X POST http://localhost:8080/api/activities/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "userId": "user-uuid",
    "activityDate": "2024-01-15",
    "title": "晨間運動",
    "category": "健身",
    "startTime": "07:00",
    "endTime": "08:00",
    "notes": "慢跑 30 分鐘",
    "mood": 4
  }'
```

### 查詢活動
```bash
curl -X GET "http://localhost:8080/api/activities/byDate?userId=<USER_UUID>&activityDate=2024-01-15" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## 驗證規則

### 用戶註冊
- Email：必須是有效的 email 格式
- 密碼：至少 8 個字符，包含大寫字母、小寫字母、數字和特殊符號

### 活動創建
- userId：必填
- activityDate：必填
- title：不能為空白
- category：不能為空白
- mood：1-5 之間的數值

## 測試

運行所有測試：
```bash
mvn test
```

運行特定測試類：
```bash
mvn test -Dtest=ActivityRepositoryTest
mvn test -Dtest=UserServiceTest
```

## 項目結構

```
src/
├── main/
│   ├── java/
│   │   └── activity_tracker_backend/
│   │       ├── config/          # 配置類
│   │       ├── controller/      # REST 控制器
│   │       ├── dto/            # 數據傳輸對象
│   │       ├── exception/      # 異常處理
│   │       ├── jwt/            # JWT 相關
│   │       ├── model/          # 實體類
│   │       ├── repository/     # 數據訪問層
│   │       └── service/        # 業務邏輯層
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── activity_tracker_backend/
            ├── repository/     # Repository 測試
            └── service/        # Service 測試
```

## 開發計劃

- [ ] 添加活動統計功能
- [ ] 實現活動導出功能
- [ ] 添加用戶個人資料管理
- [ ] 實現活動提醒功能
- [ ] 添加 API 文檔（Swagger）

## 貢獻指南

1. Fork 此項目
2. 創建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 創建 Pull Request

## 許可證

此項目為練習項目，僅供學習使用。
