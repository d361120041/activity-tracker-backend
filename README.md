# Activity Tracker Backend

一個基於 Spring Boot 的活動追蹤後端 API，提供用戶註冊、登入以及活動管理功能，支援 JWT 身份驗證和 Redis 緩存。

## 🚀 技術棧

- **Java**: 21
- **Spring Boot**: 3.5.0
- **Spring Security**: JWT 身份驗證
- **Spring Data JPA**: 數據持久化
- **Microsoft SQL Server**: 數據庫
- **Redis**: 緩存與 Refresh Token 存儲
- **Maven**: 依賴管理
- **Lombok**: 減少樣板代碼
- **JWT (JJWT)**: 基於 JSON Web Token 的身份驗證

## ✨ 功能特性

### 用戶管理
- 用戶註冊（包含密碼強度驗證）
- 用戶登入（JWT Token 驗證）
- 密碼加密存儲（BCrypt）
- Refresh Token 機制
- 用戶登出功能

### 活動管理
- 創建活動記錄
- 按日期查詢活動
- 按日期範圍查詢活動
- 更新活動信息
- 刪除活動記錄
- 用戶權限控制

### 安全特性
- JWT Access Token 和 Refresh Token 機制
- 用戶權限隔離
- 密碼加密存儲
- API 端點保護
- CORS 配置
- 全局異常處理

## 📊 數據模型

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

## 🛠 API 端點

### 用戶相關 API
| 方法 | 端點 | 描述 | 需要驗證 |
|------|------|------|----------|
| POST | `/api/users/register` | 用戶註冊 | ❌ |
| POST | `/api/users/login` | 用戶登入 | ❌ |
| POST | `/api/users/logout` | 用戶登出 | ❌ |
| POST | `/api/users/refresh-token` | 刷新 Token | ❌ |

### 活動相關 API
| 方法 | 端點 | 描述 | 需要驗證 |
|------|------|------|----------|
| POST | `/api/activities/create` | 創建活動 | ✅ |
| GET | `/api/activities/byDate` | 按日期查詢活動 | ✅ |
| PUT | `/api/activities/update/{id}` | 更新活動 | ✅ |
| DELETE | `/api/activities/delete/{id}` | 刪除活動 | ✅ |

## 🔧 環境要求

- **Java**: 21+
- **Maven**: 3.6+
- **Microsoft SQL Server**: 2019+
- **Redis**: 6.0+
- **IDE**: 推薦 IntelliJ IDEA 或 VS Code

## 🚀 安裝與運行

### 1. Clone 項目
```bash
git clone https://github.com/d361120041/activity-tracker-backend.git
cd activity-tracker-backend
```

### 2. 數據庫配置
確保 SQL Server 運行在本地，並創建數據庫：
```sql
CREATE DATABASE activity_tracker_db;
```

### 3. Redis 配置
確保 Redis 服務運行在本地：
```bash
# Ubuntu/Debian
sudo apt install redis-server
sudo systemctl start redis-server

# macOS (使用 Homebrew)
brew install redis
brew services start redis

# Windows
# 下載並安裝 Redis for Windows
```

### 4. 環境變量設定
創建 `.env` 文件或設定環境變量：
```bash
# 生產環境
export DB_PASSWORD=your_database_password
export JWT_SECRET=your_jwt_secret_key_at_least_256_bits
```

### 5. 配置文件
開發環境配置已在 `application.properties` 中設定。
生產環境請使用 `application-prod.properties`。

### 6. 運行應用
```bash
# 開發環境
mvn spring-boot:run

# 生產環境
mvn spring-boot:run -Dspring.profiles.active=prod
```

應用將在開發環境的 `http://localhost:8080` 或生產環境的 `http://localhost:80` 啟動。

## 📝 API 使用示例

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
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
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
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### 刷新 Token
```bash
curl -X POST http://localhost:8080/api/users/refresh-token \
  -H "Content-Type: application/json" \
  --cookie "refreshToken=<REFRESH_TOKEN>"
```

## ✅ 驗證規則

### 用戶註冊
- **Email**: 必須是有效的 email 格式
- **密碼**: 至少 8 個字符，包含大寫字母、小寫字母、數字和特殊符號 (@$!%*?&)

### 活動創建
- **userId**: 必填，有效的 UUID
- **activityDate**: 必填，有效的日期格式
- **title**: 不能為空白
- **category**: 不能為空白
- **startTime** & **endTime**: 必填，有效的時間格式
- **mood**: 1-5 之間的數值

## 🔐 JWT Token 配置

### Token 過期時間
- **Access Token**: 5 分鐘 (300,000 ms)
- **Refresh Token**: 7 天 (604,800,000 ms)

### Token 使用流程
1. 用戶登入獲得 Access Token 和 Refresh Token
2. Refresh Token 存儲在 HttpOnly Cookie 中
3. Access Token 用於 API 請求驗證
4. Access Token 過期時使用 Refresh Token 刷新
5. 登出時清除所有 Token

## 🧪 測試

### 運行所有測試
```bash
mvn test
```

### 運行特定測試類
```bash
mvn test -Dtest=ActivityRepositoryTest
mvn test -Dtest=UserServiceTest
```

### 測試覆蓋率
```bash
mvn jacoco:report
```

## 📁 項目結構

```
src/
├── main/
│   ├── java/
│   │   └── activity_tracker_backend/
│   │       ├── config/          # 配置類 (CORS, Security, WebMvc)
│   │       ├── controller/      # REST 控制器
│   │       ├── dto/            # 數據傳輸對象
│   │       ├── exception/      # 全局異常處理
│   │       ├── jwt/            # JWT 工具類和攔截器
│   │       ├── model/          # 實體類
│   │       ├── repository/     # 數據訪問層
│   │       └── service/        # 業務邏輯層
│   └── resources/
│       ├── application.properties       # 開發環境配置
│       └── application-prod.properties  # 生產環境配置
└── test/
    └── java/
        └── activity_tracker_backend/
            ├── repository/     # Repository 測試
            └── service/        # Service 測試
```

## 🔄 開發計劃

- [ ] 添加活動統計功能
- [ ] 實現活動導出功能 (CSV, PDF)
- [ ] 添加用戶個人資料管理
- [ ] 實現活動提醒功能
- [ ] 添加 API 文檔 (Swagger/OpenAPI)
- [ ] 實現活動分類管理
- [ ] 添加數據分析和圖表功能
- [ ] 實現多語言支援

## 🐛 常見問題

### Q: 如何解決 Redis 連接問題？
A: 確保 Redis 服務正在運行，並檢查配置文件中的 Redis 連接設定。

### Q: JWT Token 過期怎麼辦？
A: 使用 `/api/users/refresh-token` 端點刷新 Access Token。

### Q: 如何修改 Token 過期時間？
A: 在 `application.properties` 中修改 `jwt.access-token-expiration-ms` 和 `jwt.refresh-token-expiration-ms` 值。

### 代碼規範
- 使用 Java 21 語法特性
- 遵循 Spring Boot 最佳實踐
- 編寫單元測試
- 添加適當的文檔註釋

## 📄 許可證

此項目為練習項目，僅供學習使用。

---

**注意**: 此項目使用了 Spring Boot 3.5.0 和 Java 21，請確保您的開發環境支援這些版本。
