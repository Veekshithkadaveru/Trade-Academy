---
name: Trade Academy Phase 1
overview: Build a simple offline trading simulation app for Android using Kotlin and Jetpack Compose. Users read local news and tips, then trade virtual assets using a $10,000 demo wallet. Prices simulate locally via random walk. Features include 4 screens (Home, Market, News & Tips, Portfolio), 17 assets across 4 categories, Room persistence for wallet/holdings/trades, and purposeful animations. No backend, no API, no real money.
todos:
  - id: foundation
    content: Setup project with Compose, MVVM, Navigation, Room, and Gson
    status: pending
  - id: json-assets
    content: Create market.json, news.json, tips.json in assets/
    status: pending
    dependencies:
      - foundation
  - id: price-simulator
    content: Implement PriceSimulator.kt random walk tick function
    status: pending
    dependencies:
      - foundation
  - id: room-db
    content: Setup Room database with WalletEntity, HoldingEntity, TradeEntity, DAOs
    status: pending
    dependencies:
      - foundation
  - id: asset-repository
    content: Build AssetRepository.kt Gson parse for all 3 JSON files
    status: pending
    dependencies:
      - json-assets
  - id: market-viewmodel
    content: Create MarketViewModel with 30s coroutine tick and price StateFlow
    status: pending
    dependencies:
      - price-simulator
      - asset-repository
  - id: nav-skeleton
    content: NavHost + bottom nav bar with 4 tabs (Home, Market, News & Tips, Portfolio)
    status: pending
    dependencies:
      - foundation
  - id: market-screen
    content: Build MarketScreen with asset list, category tabs, live price rows
    status: pending
    dependencies:
      - market-viewmodel
      - nav-skeleton
  - id: buy-sell-sheet
    content: Build BuySellSheet with quantity input, buy/sell logic, confirm
    status: pending
    dependencies:
      - room-db
      - market-screen
  - id: portfolio-viewmodel
    content: Create PortfolioViewModel with holdings, P&L, buy/sell logic
    status: pending
    dependencies:
      - room-db
      - market-viewmodel
  - id: portfolio-screen
    content: Build PortfolioScreen with holdings list, P&L, cash, reset, trade history
    status: pending
    dependencies:
      - portfolio-viewmodel
      - buy-sell-sheet
      - nav-skeleton
  - id: content-viewmodel
    content: Create ContentViewModel for news and tips from JSON
    status: pending
    dependencies:
      - asset-repository
  - id: home-screen
    content: Build HomeScreen with balance card, portfolio value, quick nav
    status: pending
    dependencies:
      - portfolio-viewmodel
      - nav-skeleton
  - id: news-tips-screen
    content: Build NewsTipsScreen with two tabs (News and Tips) and cards
    status: pending
    dependencies:
      - content-viewmodel
      - nav-skeleton
  - id: animations
    content: Add all animations (count-up, price flash, scale press, sheet slide, confetti)
    status: pending
    dependencies:
      - home-screen
      - market-screen
      - buy-sell-sheet
      - portfolio-screen
  - id: edge-cases
    content: Edge case validation (insufficient funds, oversell)
    status: pending
    dependencies:
      - buy-sell-sheet
      - portfolio-viewmodel
  - id: polish
    content: Trade history display, QA on real device, visual polish pass
    status: pending
    dependencies:
      - animations
      - edge-cases
      - portfolio-screen
  - id: release-apk
    content: Final client APK build, signed release, acceptance criteria sign-off
    status: pending
    dependencies:
      - polish
---

# Trade Academy — Phase 1 Implementation Plan

> **Overview**: A simple offline trading simulation app for learning and practice. Users read local news and tips, then trade virtual assets using a $10,000 demo wallet. Prices simulate locally via random walk. No backend, no API, no real money. Platform: Android (API 26+). Tech: Kotlin · Jetpack Compose · MVVM · Room · Gson.

> **Delivery**: 2–3 Days (Vibe Coding Assisted)

> **Asset Note**: All content is loaded from `assets/` — `market.json`, `news.json`, `tips.json`. No external API or internet required. App works fully in airplane mode.

## ✅ Project Status & Todos

### 🏗 Day 1 — Core Systems
- [ ] **1A: Foundation Setup** <!-- id: foundation -->
- [ ] **1B: JSON Assets** <!-- id: json-assets -->
- [ ] **1C: Price Simulator** <!-- id: price-simulator -->
- [ ] **1D: Room Database** <!-- id: room-db -->
- [ ] **1E: Asset Repository** <!-- id: asset-repository -->
- [ ] **1F: MarketViewModel** <!-- id: market-viewmodel -->
- [ ] **1G: Nav Skeleton** <!-- id: nav-skeleton -->
- [ ] **1H: Market Screen** <!-- id: market-screen -->

### 📱 Day 2 — All Screens
- [ ] **2A: BuySellSheet** <!-- id: buy-sell-sheet -->
- [ ] **2B: PortfolioViewModel** <!-- id: portfolio-viewmodel -->
- [ ] **2C: Portfolio Screen** <!-- id: portfolio-screen -->
- [ ] **2D: ContentViewModel** <!-- id: content-viewmodel -->
- [ ] **2E: Home Screen** <!-- id: home-screen -->
- [ ] **2F: News & Tips Screen** <!-- id: news-tips-screen -->

### 🎬 Day 3 — Animations & Polish
- [ ] **3A: Animations** <!-- id: animations -->
- [ ] **3B: Edge Case Validation** <!-- id: edge-cases -->
- [ ] **3C: Polish & QA** <!-- id: polish -->
- [ ] **3D: Release APK** <!-- id: release-apk -->

---

## 🏗 System Architecture

### 1. High-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI Layer                  │
│   (HomeScreen, MarketScreen, NewsTipsScreen, PortfolioScreen)│
├─────────────────────────────────────────────────────────────┤
│               ViewModel + StateFlow (MVVM)                  │
│   (MarketViewModel, PortfolioViewModel, ContentViewModel)   │
├─────────────────────────────────────────────────────────────┤
│                    Engine / Logic Layer                     │
│              (PriceSimulator, Buy/Sell Logic)               │
├─────────────────────────────────────────────────────────────┤
│                      Data Layer                             │
│   (Room — Wallet, Holdings, Trades | AssetRepository JSON)  │
└─────────────────────────────────────────────────────────────┘
```

### 2. Asset Categories & Volatility
```
Category      Volatility    Feel
─────────────────────────────────
Stocks        0.008–0.018  Moderate
Crypto        0.030–0.050  High
Forex         0.002–0.004   Low
Commodities   0.008–0.020   Moderate
```

### 3. Project File Structure
```
com.tradeacademy/
├── MainActivity.kt                   # NavHost, bottom nav bar
├── engine/
│   └── PriceSimulator.kt             # Random walk tick function
├── viewmodel/
│   ├── MarketViewModel.kt            # 30s coroutine tick, price StateFlow
│   ├── PortfolioViewModel.kt         # Holdings, P&L, buy/sell logic
│   └── ContentViewModel.kt           # News and tips from JSON
├── data/
│   ├── WalletEntity.kt               # Room — cash balance
│   ├── HoldingEntity.kt              # Room — asset holdings
│   ├── TradeEntity.kt                # Room — trade history
│   ├── AppDatabase.kt                # Room database
│   └── AssetRepository.kt            # Gson parse for market, news, tips
├── assets/
│   ├── market.json                   # 17 assets with base prices
│   ├── news.json                     # 10 news articles
│   └── tips.json                     # 10 trading tips
└── ui/
    ├── HomeScreen.kt                 # Balance card, portfolio value, quick nav
    ├── MarketScreen.kt               # Asset list, category tabs, live prices
    ├── BuySellSheet.kt               # Bottom sheet, quantity input, confirm
    ├── NewsTipsScreen.kt             # Two tabs — News and Tips
    ├── PortfolioScreen.kt            # Holdings, P&L, cash, reset, trade history
    └── theme/
        ├── Theme.kt
        ├── Color.kt
        └── Typography.kt
```

---

## 🚀 Detailed Implementation Roadmap

---

## Day 1: Core Systems

### 1A: Foundation Setup <!-- id: foundation -->
> **Goal**: Set up project with Compose, Navigation, Room, Gson, and MVVM foundation.

**Duration**: Morning of Day 1

**Files to create:**
| File | Description |
|------|-------------|
| `build.gradle.kts` (app) | Add Compose, Navigation, Room, Gson, Coroutines |
| `MainActivity.kt` | NavHost skeleton with 4 routes |
| `ui/theme/Theme.kt` | Dark theme for trading app |
| `ui/theme/Color.kt` | Green/red for gains/losses, neutral palette |

**Key Dependencies:**
```kotlin
// Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.01.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose:1.8.2")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Gson
implementation("com.google.code.gson:gson:2.10.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
```

**NavGraph Routes:**
```kotlin
sealed class Screen(val route: String) {
    object Home       : Screen("home")
    object Market     : Screen("market")
    object NewsTips   : Screen("news_tips")
    object Portfolio  : Screen("portfolio")
}
```

**Exit Criteria:**
- [ ] Project builds and runs on emulator
- [ ] Dark theme applied globally
- [ ] Bottom nav bar with 4 tabs visible
- [ ] Navigation between all 4 screens works

---

### 1B: JSON Assets <!-- id: json-assets -->
> **Goal**: Create all three JSON files in `assets/` with full content.

**Duration**: Morning Day 1

**Files to create:**
| File | Description |
|------|-------------|
| `assets/market.json` | 17 assets with id, name, category, basePrice, volatility |
| `assets/news.json` | 10 articles with id, category, headline, summary, body, tag, date |
| `assets/tips.json` | 10 tips with id, icon, difficulty, title, body |

**Market JSON Structure (17 assets):**
| Category | Assets |
|----------|--------|
| Stocks | Apple $213, Tesla $248, Amazon $198, NVIDIA $875, Microsoft $415 |
| Crypto | Bitcoin $82,500, Ethereum $3,180, Solana $148, BNB $412 |
| Forex | EUR/USD $1.08, GBP/USD $1.26, USD/JPY $151, AUD/USD $0.65 |
| Commodities | Gold $3,112, Silver $34, Crude Oil $82, Natural Gas $2.18 |

**Volatility by Category:**
- Stocks: 0.008–0.018
- Crypto: 0.030–0.050
- Forex: 0.002–0.004
- Commodities: 0.008–0.020

**Exit Criteria:**
- [ ] All 3 JSON files exist in `assets/`
- [ ] market.json has 17 assets with correct structure
- [ ] news.json has 10 articles
- [ ] tips.json has 10 tips

---

### 1C: Price Simulator <!-- id: price-simulator -->
> **Goal**: Implement random walk tick function for local price simulation.

**Duration**: Mid-morning Day 1

**Files to create:**
| File | Description |
|------|-------------|
| `engine/PriceSimulator.kt` | Random walk tick function |

**Full Implementation:**
```kotlin
import kotlin.random.Random

object PriceSimulator {
    /**
     * Runs every 30 seconds in MarketViewModel coroutine.
     * @param basePrice Current price before tick
     * @param volatility Category-specific (e.g. 0.03 for crypto)
     * @return New price after random walk step
     */
    fun simulateTick(basePrice: Double, volatility: Double): Double {
        val change = basePrice * volatility * Random.nextGaussian()
        return (basePrice + change).coerceAtLeast(0.01)
    }
}
```

**Exit Criteria:**
- [ ] Function returns valid positive price
- [ ] Price never drops below 0.01
- [ ] Higher volatility produces larger swings

---

### 1D: Room Database <!-- id: room-db -->
> **Goal**: Set up Room persistence for wallet, holdings, and trade history.

**Duration**: Afternoon Day 1

**Files to create:**
| File | Description |
|------|-------------|
| `data/WalletEntity.kt` | Room entity — cash balance |
| `data/HoldingEntity.kt` | Room entity — asset holdings |
| `data/TradeEntity.kt` | Room entity — trade history |
| `data/WalletDao.kt` | DAO for wallet CRUD |
| `data/HoldingDao.kt` | DAO for holdings CRUD |
| `data/TradeDao.kt` | DAO for trade history |
| `data/AppDatabase.kt` | Room database singleton |

**Entities:**
```kotlin
@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = 1,
    val cashBalance: Double = 10000.0
)

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey val assetId: String,
    val quantity: Double,
    val avgBuyPrice: Double
)

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetId: String,
    val type: String,       // BUY or SELL
    val quantity: Double,
    val price: Double,
    val timestamp: Long = System.currentTimeMillis()
)
```

**Exit Criteria:**
- [ ] All entities and DAOs compile
- [ ] Wallet starts with $10,000 on first run
- [ ] Database migrations handled for future schema changes

---

### 1E: Asset Repository <!-- id: asset-repository -->
> **Goal**: Parse all 3 JSON files via Gson and expose data to ViewModels.

**Duration**: Afternoon Day 1

**Files to create:**
| File | Description |
|------|-------------|
| `data/AssetRepository.kt` | Gson parse for market.json, news.json, tips.json |

**Data Classes (for Gson):**
```kotlin
data class Asset(
    val id: String,
    val name: String,
    val category: String,
    val basePrice: Double,
    val volatility: Double
)

data class NewsResponse(val articles: List<Article>)
data class Article(val id: Int, val category: String, val headline: String, 
    val summary: String, val body: String, val tag: String, val date: String)

data class TipsResponse(val tips: List<Tip>)
data class Tip(val id: Int, val icon: String, val difficulty: String, 
    val title: String, val body: String)
```

**Exit Criteria:**
- [ ] All 17 assets load from market.json
- [ ] All 10 news articles load from news.json
- [ ] All 10 tips load from tips.json
- [ ] Repository exposes Flow or suspend functions for UI

---

### 1F: MarketViewModel <!-- id: market-viewmodel -->
> **Goal**: 30-second coroutine tick loop, price StateFlow, integrate PriceSimulator.

**Duration**: Late afternoon Day 1

**Files to create:**
| File | Description |
|------|-------------|
| `viewmodel/MarketViewModel.kt` | Coroutine tick every 30s, StateFlow<Map<String, Double>> for live prices |

**State Model:**
```kotlin
data class MarketUiState(
    val assets: List<Asset> = emptyList(),
    val livePrices: Map<String, Double> = emptyMap(),
    val selectedCategory: String? = null
)
```

**Tick Logic:**
- On init: load assets from AssetRepository, seed livePrices from basePrice
- Every 30 seconds: for each asset, call `PriceSimulator.simulateTick(currentPrice, asset.volatility)`
- Emit new livePrices via StateFlow

**Exit Criteria:**
- [ ] Prices tick every 30 seconds
- [ ] All 17 assets show simulated prices
- [ ] StateFlow emits to UI via collectAsState()

---

### 1G: Nav Skeleton <!-- id: nav-skeleton -->
> **Goal**: NavHost with 4 routes and bottom navigation bar.

**Duration**: Evening Day 1

**Files to create:**
| File | Description |
|------|-------------|
| `MainActivity.kt` | Scaffold with BottomNavBar, NavHost |

**Layout:**
```
┌──────────────────────────────┐
│                              │
│     [Screen Content]          │
│                              │
├──────────────────────────────┤
│  Home | Market | News | Portfolio │
└──────────────────────────────┘
```

**Exit Criteria:**
- [ ] Bottom nav shows 4 tabs
- [ ] Tapping each tab navigates correctly
- [ ] Selected tab highlighted

---

### 1H: Market Screen <!-- id: market-screen -->
> **Goal**: Asset list with category tabs and live price rows.

**Duration**: Evening Day 1

**Files to create:**
| File | Description |
|------|-------------|
| `ui/MarketScreen.kt` | LazyColumn of asset rows, category filter tabs |

**Layout:**
```
┌──────────────────────────────┐
│  [Stocks][Crypto][Forex][Commodities]  │
│  ───────────────────────────  │
│  Apple        $213.45   ▲    │
│  Tesla        $248.12   ▼    │
│  Amazon       $198.67   ▲    │
│  ...                          │
└──────────────────────────────┘
```

**Row Content:** Asset name, live price, up/down indicator (green/red)

**Exit Criteria:**
- [ ] All 17 assets display with live prices
- [ ] Category tabs filter the list
- [ ] Tapping a row opens BuySellSheet (placeholder for Day 2)

---

## Day 2: All Screens

### 2A: BuySellSheet <!-- id: buy-sell-sheet -->
> **Goal**: Modal bottom sheet with quantity input, buy/sell buttons, confirm flow.

**Duration**: Morning Day 2

**Files to create:**
| File | Description |
|------|-------------|
| `ui/BuySellSheet.kt` | ModalBottomSheet, quantity TextField, Buy/Sell buttons |

**Layout:**
```
┌──────────────────────────────┐
│  Buy / Sell — Apple           │
│  Price: $213.45               │
│  [ Quantity: ____ ]           │
│  [ BUY ]  [ SELL ]            │
│  [ CONFIRM ]                  │
└──────────────────────────────┘
```

**Logic:**
- Buy: deduct cash, add/update holding (weighted avg), insert TradeEntity
- Sell: add cash, reduce holding, insert TradeEntity
- Validate: cannot buy more than cash allows; cannot sell more than held

**Exit Criteria:**
- [ ] Sheet slides up from bottom
- [ ] Buy deducts correct cash, adds holding
- [ ] Sell adds correct cash, reduces holding
- [ ] Weighted average buy price tracked per asset

---

### 2B: PortfolioViewModel <!-- id: portfolio-viewmodel -->
> **Goal**: Holdings, P&L calculation, buy/sell orchestration, reset logic.

**Duration**: Morning Day 2

**Files to create:**
| File | Description |
|------|-------------|
| `viewmodel/PortfolioViewModel.kt` | StateFlow for holdings, cash, P&L, trade history |

**State Model:**
```kotlin
data class PortfolioUiState(
    val cashBalance: Double = 10000.0,
    val holdings: List<HoldingWithPrice> = emptyList(),
    val portfolioValue: Double = 0.0,
    val tradeHistory: List<TradeEntity> = emptyList()
)

data class HoldingWithPrice(
    val assetId: String,
    val quantity: Double,
    val avgBuyPrice: Double,
    val currentPrice: Double,
    val unrealisedPnL: Double  // (currentPrice - avgBuyPrice) * quantity
)
```

**P&L Formula:** `unrealisedPnL = (currentPrice - avgBuyPrice) * quantity`

**Reset:** Set wallet to $10,000, clear all holdings, clear trade history

**Exit Criteria:**
- [ ] P&L updates correctly on every price tick
- [ ] Buy/sell logic delegates to Room DAOs
- [ ] Reset returns to $10,000 and clears all

---

### 2C: Portfolio Screen <!-- id: portfolio-screen -->
> **Goal**: Holdings list with P&L, cash balance, reset button, trade history.

**Duration**: Afternoon Day 2

**Files to create:**
| File | Description |
|------|-------------|
| `ui/PortfolioScreen.kt` | Holdings LazyColumn, cash card, reset button, trade history section |

**Layout:**
```
┌──────────────────────────────┐
│  Cash: $7,234.50             │
│  Portfolio Value: $12,450.00 │
│  ───────────────────────────  │
│  Holdings:                    │
│  Apple  0.5 @ $213  +$12.50  │
│  BTC    0.01 @ $82.5K +$180  │
│  ───────────────────────────  │
│  Trade History:               │
│  Bought 0.5 Apple @ $213     │
│  Sold 0.1 BTC @ $82,400      │
│  ───────────────────────────  │
│  [ RESET WALLET ]             │
└──────────────────────────────┘
```

**Exit Criteria:**
- [ ] Holdings display with P&L (green/red)
- [ ] Cash balance shown
- [ ] Reset button clears wallet and holdings
- [ ] Trade history displays recent trades

---

### 2D: ContentViewModel <!-- id: content-viewmodel -->
> **Goal**: Load news articles and tips from AssetRepository.

**Duration**: Afternoon Day 2

**Files to create:**
| File | Description |
|------|-------------|
| `viewmodel/ContentViewModel.kt` | StateFlow for articles and tips |

**Exit Criteria:**
- [ ] All 10 news articles available to UI
- [ ] All 10 tips available to UI

---

### 2E: Home Screen <!-- id: home-screen -->
> **Goal**: Balance card, portfolio value, quick nav to all sections.

**Duration**: Evening Day 2

**Files to create:**
| File | Description |
|------|-------------|
| `ui/HomeScreen.kt` | Balance card, portfolio value, quick nav buttons |

**Layout:**
```
┌──────────────────────────────┐
│  Trade Academy               │
│  ───────────────────────────  │
│  Cash: $7,234.50              │
│  Portfolio: $12,450.00        │
│  Total: $19,684.50            │
│  ───────────────────────────  │
│  [ Market ] [ News ] [ Portfolio ] │
└──────────────────────────────┘
```

**Exit Criteria:**
- [ ] Wallet balance and portfolio value display
- [ ] Quick nav buttons navigate to each screen

---

### 2F: News & Tips Screen <!-- id: news-tips-screen -->
> **Goal**: Two tabs — News and Tips — with card layout.

**Duration**: Evening Day 2

**Files to create:**
| File | Description |
|------|-------------|
| `ui/NewsTipsScreen.kt` | TabRow (News | Tips), LazyColumn of cards |

**News Card:** Headline, summary, tag, date
**Tips Card:** Icon, title, difficulty, body

**Exit Criteria:**
- [ ] Both tabs display correctly
- [ ] All 10 news articles show as cards
- [ ] All 10 tips show as cards

---

## Day 3: Animations & Polish

### 3A: Animations <!-- id: animations -->
> **Goal**: Add purposeful animations per PRD spec. No over-engineering.

**Duration**: Morning Day 3

**Animation Specs:**
| Screen | Animation | Spec |
|--------|-----------|------|
| Home | Portfolio value count-up on load | Animatable tween 600ms |
| Market | Price colour flash green/red on tick | animateColorAsState 300ms |
| Market | Row tap scale press | 1.0 → 0.97 spring |
| Buy/Sell | Sheet slide up | ModalBottomSheet default |
| Trade confirm | Checkmark + confetti | Canvas 40 particles |
| Portfolio | P&L green/red colour | animateColorAsState |
| Screen transitions | Slide left/right | tween 280ms |

**Exit Criteria:**
- [ ] Portfolio value counts up on Home load
- [ ] Price flash on Market tick
- [ ] Row scale on tap
- [ ] Confetti on trade confirm
- [ ] P&L colours animate
- [ ] Screen transitions smooth

---

### 3B: Edge Case Validation <!-- id: edge-cases -->
> **Goal**: Insufficient funds, oversell validation.

**Duration**: Mid-morning Day 3

**Validations:**
| Case | Behaviour |
|------|-----------|
| Buy with insufficient cash | Disable buy or show error toast |
| Sell more than held | Disable sell or show error toast |
| Zero quantity | Disable confirm |
| Negative quantity | Reject input |

**Exit Criteria:**
- [ ] Cannot buy more than available cash
- [ ] Cannot sell more than held quantity
- [ ] User sees clear feedback on validation failure

---

### 3C: Polish & QA <!-- id: polish -->
> **Goal**: Trade history display, QA on real device, visual polish.

**Duration**: Afternoon Day 3

**QA Checklist:**
| Test | Method |
|------|--------|
| All 17 assets load from JSON | Launch app, check Market |
| Prices tick every 30 seconds | Wait and observe |
| Buy deducts cash, adds holding | Execute buy, check Portfolio |
| Sell adds cash, reduces holding | Execute sell, check Portfolio |
| Insufficient funds blocked | Try buy exceeding cash |
| Oversell blocked | Try sell exceeding quantity |
| P&L updates on tick | Hold asset, wait for tick |
| News and tips load | Check News & Tips screen |
| Wallet reset works | Reset, verify $10,000 and empty holdings |
| Airplane mode | Enable airplane mode, use app |
| No crashes | 10-minute normal use on device |

**Exit Criteria:**
- [ ] All acceptance criteria from PRD met
- [ ] App works fully in airplane mode
- [ ] No crashes across normal use

---

### 3D: Release APK <!-- id: release-apk -->
> **Goal**: Final signed APK build for client delivery.

**Duration**: End of Day 3

**Release Tasks:**
| Task | Description |
|------|-------------|
| Signing Config | Add keystore to build.gradle.kts |
| Version | versionCode=1, versionName="1.0.0" |
| Build | ./gradlew assembleRelease |
| Smoke Test | Install APK, run full flow |

**Exit Criteria:**
- [ ] Signed APK installs without error
- [ ] All 4 screens functional
- [ ] Buy/sell/reset work end-to-end
- [ ] No debug overlays in release

---

## 📊 Timeline Summary

```
Day 1 AM    ████████████  1A Foundation + 1B JSON Assets + 1C Price Simulator
Day 1 PM    ████████████  1D Room DB + 1E Asset Repository + 1F MarketViewModel
Day 1 EVE   ████████████  1G Nav Skeleton + 1H Market Screen

Day 2 AM    ████████████  2A BuySellSheet + 2B PortfolioViewModel
Day 2 PM    ████████████  2C Portfolio Screen + 2D ContentViewModel
Day 2 EVE   ████████████  2E Home Screen + 2F News & Tips Screen

Day 3 AM    ████████████  3A Animations + 3B Edge Cases
Day 3 PM    ████████████  3C Polish & QA + 3D Release APK
```

**Total Duration: 3 Days**

---

## 🎯 Success Metrics (Acceptance Criteria)

| Criterion | Target |
|-----------|--------|
| All 17 assets load from JSON | ✅ |
| Prices tick every 30 seconds | ✅ |
| Buy deducts cash, adds holding | ✅ |
| Sell adds cash, reduces holding | ✅ |
| Cannot buy more than cash | ✅ |
| Cannot sell more than held | ✅ |
| P&L updates on every tick | ✅ |
| All 10 news + 10 tips load | ✅ |
| Wallet reset returns to $10,000 | ✅ |
| App works in airplane mode | ✅ |
| No crashes on real device | ✅ |

---

## ⚠️ Risk Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| JSON parse failure at startup | Low | High | Try/catch, show error state, validate JSON structure |
| Price simulation too volatile | Medium | Medium | Tune volatility per category, clamp extreme moves |
| Room migration breaks data | Low | High | Version database, test migrations |
| Buy/sell race condition | Medium | High | Use suspend functions, single source of truth in Room |
| Confetti animation jank | Low | Low | Limit to 40 particles, use Canvas not heavy composables |

---

## 📁 Asset Checklist

Before starting, ensure these files exist in `app/src/main/assets/`:

| File | Content |
|------|---------|
| `market.json` | 17 assets with id, name, category, basePrice, volatility |
| `news.json` | 10 articles with full structure |
| `tips.json` | 10 tips with full structure |

---

*Document Version: 1.0*
*Last Updated: March 2026*
*Project: Trade Academy — Kotlin + Jetpack Compose*
