# WideLoc — Android-Based Self-Localization Using UWB (DW3000)

WideLoc is an Android application developed for a thesis project titled  
**"Pengembangan Interface Berbasis Android untuk Self-Localization Berbasis Ultra-Wideband (UWB)."**

The app communicates with UWB modules (Decawave/Quorvo DW3000 series) to perform **TWR (Two-Way Ranging)**, retrieve **CSI (Channel State Information)**, conduct tracking sessions, log device movement, and visualize self-localization results.

WideLoc functions as both a **research tool** and a **data collection platform** for studies related to UWB indoor positioning systems.

---

## 🚀 Features

### 🔹 UWB Functionalities
- Device discovery and status checking  
- Initiate and control **TWR ranging**  
- Retrieve **CSI** data  
- Parse custom DW3000 frames  
- Real-time distance visualization  
- Track UWB device movement  
- Measure:
  - Distance over time  
  - Latency  
  - Power consumption  

### 🔹 UI & App Features
- Clean Android UI (Kotlin + XML)
- Tracking session dashboard  
- History list & detail views  
- On-device storage for session data  
- Real-time logs & event display  
- Modular project structure  

---

## 🏗 Technology Stack

### **Android**
- Kotlin
- XML Layouts
- AndroidX AppCompat
- Material Design Components
- ViewModel (Android Architecture Components)
- RecyclerView + Custom Adapters

### **Architecture**
MVVM-like layered structure:
- `ui` → Activities, Fragments  
- `viewmodel` → UI logic & state handling  
- `data` → Models, repositories  
- `core.uwb` → DW3000 server communication + frame parser  

### **Communication**
- Custom protocol over socket for DW3000  
- TWR scheduling  
- Frame payload parsing  
- CSI request handling  

### **Utilities**
- Kotlin Coroutines  
- JSON parsing  
- Event listeners & callback system  

---

## 📁 Project Structure

```

WideLoc/
│── app/
│   ├── src/main/java/com/futureappdevelopment/wideloc/
│   │     ├── core/          # UWB engine, DW3000 Server, parsing
│   │     ├── data/          # Models, repositories
│   │     ├── ui/            # Activity, Fragment, Adapter
│   │     ├── viewmodel/     # ViewModels
│   │     └── util/          # Helpers, constants
│   ├── src/main/res/         # Layouts, drawables, xml
│   ├── build.gradle.kts
│── settings.gradle.kts
│── build.gradle.kts
│── gradle.properties
│── README.md

```

---

## 📡 UWB Module Integration

WideLoc communicates with a UWB module using a custom server class located in:

```

core/uwb/DW3000Server.kt

```

Main responsibilities:
- Socket communication  
- Sending TWR/CSI commands  
- Scheduling routines  
- Handling binary frames  
- Exposing callbacks to ViewModels  

Frame parsing is handled in:

```

core/uwb/frame/BaseFrameParser.kt

````

---

## 📊 Tracking Session Logging

Each tracking session records:
- Distances (with timestamp)
- Latency measurements
- Power consumption values
- Device movement history (coordinates)
- Session metadata

Example model:

```kotlin
data class TrackingSessionData(
    val sessionId: Int = 0,
    val date: LocalDateTime = LocalDateTime.now(),
    val recordedDistances: MutableList<DistancesWithTimestamp> = mutableListOf(),
    var deviceTrackingHistoryData: MutableList<DeviceTrackingHistoryData> = mutableListOf(),
    var latencies: MutableList<LatencyData> = mutableListOf(),
    var powerConsumptions: MutableList<PowerConsumptionData> = mutableListOf()
)
````

---

## 🔧 How to Build & Run

### Requirements

* Android Studio Ladybug or newer
* JDK 17 or compatible
* Real Android device (recommended)
* DW3000 UWB module + server firmware

### Steps

1. Clone or download the project
2. Open the folder in Android Studio
3. Allow Gradle to sync
4. Connect physical device
5. Build & run
6. Connect to DW3000 server (IP/Port config in app)

---

## 🧪 Testing Notes

Because UWB communication relies on real hardware:

* Unit tests cover model & parser logic
* Live TWR/CSI tests require physical DW3000 device
* Debug logs are available via Logcat

---

## 🧰 Troubleshooting

### Common Issues

| Issue           | Cause                   | Fix                                 |
| --------------- | ----------------------- | ----------------------------------- |
| `ECONNRESET`    | UWB server reset        | Restart server / check socket       |
| TWR stuck       | DW3000 scheduler halted | Re-init server connection           |
| No CSI data     | Firmware not configured | Reflash DW3000 firmware             |
| Invalid payload | Frame mismatch          | Verify frame index & payload length |

---

## 📄 License

This project was developed as academic research for an undergraduate thesis.
You may use or modify the code for research or educational purposes.

---