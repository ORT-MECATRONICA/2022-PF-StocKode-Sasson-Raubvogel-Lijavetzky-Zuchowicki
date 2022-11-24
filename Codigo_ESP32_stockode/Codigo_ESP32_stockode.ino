/*
  Rui Santos
  Complete project details at our blog.
    - ESP32: https://RandomNerdTutorials.com/esp32-firebase-realtime-database/
    - ESP8266: https://RandomNerdTutorials.com/esp8266-nodemcu-firebase-realtime-database/
  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files.
  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
  Based in the RTDB Basic Example by Firebase-ESP-Client library by mobizt
  https://github.com/mobizt/Firebase-ESP-Client/blob/main/examples/RTDB/Basic/Basic.ino
*/

#include <Adafruit_NeoPixel.h>
#include <Arduino.h>
#if defined(ESP32)
#include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>

//Neopixel
#define PIN 13
#define interval 10000

unsigned long checkpoint = 0;
boolean flag = false;
int flagCheckpoint = 0;
int cuenta;

// When we setup the NeoPixel library, we tell it how many pixels, and which pin to use to send signals.
// Note that for older NeoPixel strips you might need to change the third parameter--see the strandtest
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(2, PIN, NEO_GRB + NEO_KHZ800);

//Provide the token generation process info.
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Insert your network credentials
#define WIFI_SSID "casacamargo2.4"
#define WIFI_PASSWORD "0052462857"

// Insert Firebase project API Key
#define API_KEY "AIzaSyBxsLXj2keRiI_sO3lI0XJOMqLpAmW6auU"

// Insert RTDB URLefine the RTDB URL */
#define DATABASE_URL "https://stockode-6db09-default-rtdb.firebaseio.com/"

//Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
int count = 0;
int intValue;
bool signupOK = false;

void setup() {
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Sign up */
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  }
  else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  pixels.begin(); // This initializes the NeoPixel library.
  pixels.clear();
  pixels.show();
}

void loop() {
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 1000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();

    if (Firebase.RTDB.getInt(&fbdo, "test/flag")) {
      if (fbdo.dataType() == "boolean") {
        flag = fbdo.intData();
        Serial.println(flag);
      }
    }
    else {
      Serial.print("Error consiguiendo el flag: ");
      Serial.println(fbdo.errorReason());
    }
  }

  if (flag == true) {
    if (Firebase.RTDB.getInt(&fbdo, "test/int")) {
      if (fbdo.dataType() == "int") {
        intValue = fbdo.intData();
        Serial.print("Producto numero: ");
        Serial.println(intValue);
      }
    }
    else {
      Serial.print("error consiguiendo el numero de producto: ");
      Serial.println(fbdo.errorReason());
    }

    if (flagCheckpoint == 0){
      checkpoint = millis();
      flagCheckpoint = 1;
    }
    pixels.setPixelColor(intValue, pixels.Color(50, 0, 0)); // Moderately bright green color.
    pixels.show(); // This sends the updated pixel color to the hardware.
    if ((millis() - checkpoint > interval)) {
      Serial.println("Actualizando flag");
      flag = false;
      flagCheckpoint = 0;
      pixels.clear();
      pixels.show();
      if (Firebase.RTDB.setInt(&fbdo, "test/flag", flag)) {
        Serial.println("PASSED");
        Serial.println("PATH: " + fbdo.dataPath());
        Serial.println("TYPE: " + fbdo.dataType());
      }
      else {
        Serial.print("Error actualizando el flag: ");
        Serial.println(fbdo.errorReason());
      }
    }
  }
}
