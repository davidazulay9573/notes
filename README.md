# Notes App

A simple Notes application built with an Android client and a server.

## Project Structure

```
notes/
├── notes-android/  # Android app source code
└── notes-server/   # Server source code
```

## Prerequisites

Make sure you have the following installed on your system:
- [Android Studio](https://developer.android.com/studio) (for the Android app)

## Running the Project

### 1. Clone the Repository

Open a terminal and run:

```bash
git clone https://github.com/davidazulay9573/notes.git
cd notes
```

### 2. Run the Server with Docker Compose

Navigate to the server directory and start the server:

```bash
cd notes-server
sudo docker-compose up --build
```

- The server will be accessible at `http://<your-ip-address>:3001`.

### 3. Run the Android App

1. Open Android Studio.
2. Select `Open an Existing Project`.
3. Navigate to `notes/notes-android` and open it.
4. Open `res/xml/network_security_config.xml` and edit the following line with your IP address:

   ```xml
   <domain includeSubdomains="true">172.31.138.115</domain>
   ```

5. Open `java/com/example/notes/network/api/RetrofitClient.kt`.
  and edit this line with your ip address
   ``` 
     private const val BASE_URL = "http://172.31.138.115:3001/api"
   ```
6. Build and run the app by clicking the `Run` button on MainActivity class.