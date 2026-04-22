/**
Test 4: Combination of Step Detection and Tilt Detection
Production Ready Code
Needs modification to enable MQTT to send to cloud
*/

#include <Wire.h>
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>


Adafruit_MPU6050 mpu;

// Calibration values
float ax_offset = 0.0;
float ay_offset = 0.0;
float az_offset = 0.0;
float gx_offset = 0.0;
float gy_offset = 0.0;
float gz_offset = 0.0;

// Step detection variables
int stepCount = 0;
float threshold = 1.5; // Adjust this threshold as needed
float lastPeak = 0;
bool isStep = false;

void setup() {
  Serial.begin(115200);
  while (!Serial);

  Wire.begin(21, 22);

  if (!mpu.begin()) {
    Serial.println("Failed to find MPU6050 chip");
    while (1) {
      delay(10);
    }
  }
  Serial.println("MPU6050 Found!");

  // Configure MPU6050
  mpu.setAccelerometerRange(MPU6050_RANGE_4_G);
  mpu.setGyroRange(MPU6050_RANGE_250_DEG);
  mpu.setFilterBandwidth(MPU6050_BAND_21_HZ);
  delay(100);

  // Perform Calibration
  calibrateMPU6050();
}

void loop() {
  sensors_event_t a, g, temp;
  mpu.getEvent(&a, &g, &temp);

  // Correct values with offsets
  float correctedAx = a.acceleration.x - ax_offset;
  float correctedAy = a.acceleration.y - ay_offset;
  float correctedAz = a.acceleration.z - az_offset;
  float correctedGx = g.gyro.x - gx_offset;
  float correctedGy = g.gyro.y - gy_offset;
  float correctedGz = g.gyro.z - gz_offset;


  // --- Step Detection (Code 1) ---
  float magnitude = sqrt(correctedAx * correctedAx + correctedAy * correctedAy);

  if (magnitude > lastPeak && magnitude > (9.81 + threshold)) {
    lastPeak = magnitude;
  } else if (magnitude < lastPeak - threshold) {
    if (!isStep) {
      stepCount++;
      isStep = true;
      Serial.print("Step Count: ");
      Serial.println(stepCount);
    }
    lastPeak = magnitude;
  } else if (magnitude > (9.81 - threshold) && magnitude < (9.81 + threshold)) {
    isStep = false;
  }
  delay(10); // Delay for step detection (adjust as needed)

  // --- Acceleration and Tilt Detection (Code 2) ---
  Serial.print("Accel X: ");
  Serial.print(correctedAx);
  Serial.print(" m/s^2, Y: ");
  Serial.print(correctedAy);
  Serial.print(" m/s^2, Z: ");
  Serial.print(correctedAz);
  Serial.println(" m/s^2");

  Serial.print("Gyro X: ");
  Serial.print(correctedGx);
  Serial.print(" rad/s, Y: ");
  Serial.print(correctedGy);
  Serial.print(" rad/s, Z: ");
  Serial.print(correctedGz);
  Serial.println(" rad/s");

  Serial.print("Temperature: ");
  Serial.print(temp.temperature);
  Serial.println(" °C");

  Serial.println("-----------------------------------");
  delay(1000); // Reduced delay for smoother step detection
}

// Function to Calibrate MPU6050 (Same as Code 2)
void calibrateMPU6050() {
  const int calibrationSamples = 500;
  float sumAx = 0, sumAy = 0, sumAz = 0;
  float sumGx = 0, sumGy = 0, sumGz = 0;

  Serial.println("Calibrating MPU6050...");

  for (int i = 0; i < calibrationSamples; i++) {
    sensors_event_t a, g, temp;
    mpu.getEvent(&a, &g, &temp);

    sumAx += a.acceleration.x;
    sumAy += a.acceleration.y;
    sumAz += a.acceleration.z;
    sumGx += g.gyro.x;
    sumGy += g.gyro.y;
    sumGz += g.gyro.z;

    delay(3);
  }

  ax_offset = sumAx / calibrationSamples;
  ay_offset = sumAy / calibrationSamples;
  az_offset = (sumAz / calibrationSamples) - 9.81; // Adjust for gravity
  gx_offset = sumGx / calibrationSamples;
  gy_offset = sumGy / calibrationSamples;
  gz_offset = sumGz / calibrationSamples;

  Serial.println("Calibration Complete");
  Serial.print("Accel Offsets: X=");
  Serial.print(ax_offset);
  Serial.print(", Y=");
  Serial.print(ay_offset);
  Serial.print(", Z=");
  Serial.println(az_offset);

  Serial.print("Gyro Offsets: X=");
  Serial.print(gx_offset);
  Serial.print(", Y=");
  Serial.print(gy_offset);
  Serial.print(", Z=");
  Serial.println(gz_offset);
}
