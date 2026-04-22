import logging
from azure.digitaltwins.core import DigitalTwinsClient
from azure.identity import DefaultAzureCredential

# 🔹 Azure Digital Twin Setup
ADT_URL = "https://Cow-DigitalTwinModel.api.wcus.digitaltwins.azure.net"
credential = DefaultAzureCredential()
client = DigitalTwinsClient(ADT_URL, credential)


def main(event: dict):
    logging.info("📡 IoT data received")

    try:
        data = event

        cow_id = data.get("cowId", "co-01")

        # 🔹 Sensor Inputs
        ax = data.get("ax", 0)
        ay = data.get("ay", 0)
        az = data.get("az", 0)
        gx = data.get("gx", 0)
        gy = data.get("gy", 0)
        gz = data.get("gz", 0)

        # 🔹 Step count from ESP32 (per minute)
        new_steps = data.get("step_count", 0)

        # 🔹 Get current twin values
        twin = client.get_digital_twin(cow_id)

        current_steps = twin.get("step_count", 0)
        current_rumination = twin.get("rumination_time", 0)

        # ======================================
        # 🔹 FEATURE CALCULATIONS
        # ======================================

        # 🐄 Step accumulation
        step_count = current_steps + new_steps

        # 🐄 Lameness (instant calculation)
        accel_variation = abs(ax - ay) + abs(ay - az)
        gyro_variation = abs(gx) + abs(gy) + abs(gz)
        avg_lameness = round(accel_variation + gyro_variation, 2)

        # 🐄 Rumination (per minute detection → accumulate)
        small_movements = abs(ax) + abs(ay) + abs(az)
        chewing = 1 if 0.1 < small_movements < 0.5 else 0

        rumination_time = current_rumination + chewing

        # 🐄 Estrus (based on recent activity, NOT total)
        if new_steps > 25 and avg_lameness < 2:
            estrus_sign = "in_estrus"
        else:
            estrus_sign = "none"

        # ======================================
        # 🔹 UPDATE DIGITAL TWIN
        # ======================================

        patch = [
            {"op": "replace", "path": "/ax", "value": ax},
            {"op": "replace", "path": "/ay", "value": ay},
            {"op": "replace", "path": "/az", "value": az},
            {"op": "replace", "path": "/gx", "value": gx},
            {"op": "replace", "path": "/gy", "value": gy},
            {"op": "replace", "path": "/gz", "value": gz},
            {"op": "replace", "path": "/step_count", "value": step_count},
            {"op": "replace", "path": "/avg_lameness", "value": avg_lameness},
            {"op": "replace", "path": "/rumination_time", "value": rumination_time},
            {"op": "replace", "path": "/estrus_sign", "value": estrus_sign}
        ]

        client.update_digital_twin(cow_id, patch)

        logging.info(f"✅ Twin {cow_id} updated successfully")
        logging.info(
            f"Steps(total): {step_count}, New Steps: {new_steps}, "
            f"Lameness: {avg_lameness}, Rumination: {rumination_time}, Estrus: {estrus_sign}"
        )

    except Exception as e:
        logging.error(f"❌ Error processing IoT data: {str(e)}")
