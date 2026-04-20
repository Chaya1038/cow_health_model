
import pickle
import pandas as pd
import json
import os
from azure.digitaltwins.core import DigitalTwinsClient
from azure.identity import DefaultAzureCredential
import pyttsx3  # For English TTS
import requests  # Optional: For triggering external notifications
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from azure.digitaltwins.core import DigitalTwinsClient
from azure.identity import DefaultAzureCredential
from azure.core.exceptions import HttpResponseError
import uvicorn
import logging
from typing import Dict, Any
import os

# --- VOICE NOTIFICATION FUNCTION ---
def notify_user(prediction):
    if prediction.lower() != "normal":
        # English voice notification
        engine = pyttsx3.init()
        engine.say(f"Alert! Cow health status is {prediction}")
        engine.runAndWait()

        # Kannada TTS using AI4Bharat API (for demo purposes)
        # Replace this with offline method if needed
        kannada_text = f"ಎಚ್ಚರಿಕೆ! ಹಸುವಿನ ಆರೋಗ್ಯ ಸ್ಥಿತಿ {prediction} ಆಗಿದೆ."
        print(f"🔊 Kannada Notification: {kannada_text}")
        
        # If you want to use IndicTTS model locally or from Hugging Face, insert code here
        # Example: Use requests to send Kannada TTS audio to mobile or system

        # Optional: Call an external API or FCM to push mobile notification
        # requests.post("https://your_notification_endpoint", json={"message": prediction})
    else:
        print("✅ Health status is normal. No notification triggered.")

# --- LOAD MODEL ---
with open('cow_health_model.pkl', 'rb') as f:
    model = pickle.load(f)

# --- CONNECT TO DIGITAL TWIN ---
adt_url = "https://Cow-DigitalTwinModel.api.wcus.digitaltwins.azure.net"
credential = DefaultAzureCredential()
client = DigitalTwinsClient(adt_url, credential)

cow_twin_id = "co-01"  # Replace with your actual Twin ID

# --- READ DATA FROM DIGITAL TWIN ---
twin = client.get_digital_twin(cow_twin_id)

required_keys = [
    'breed', 'rumination_time', 'avg_lameness', 'estrus_sign',
    'ax', 'ay', 'az', 'gx', 'gy', 'gz', 'step_count'
]

missing_keys = [key for key in required_keys if key not in twin]
if missing_keys:
    raise KeyError(f"The following keys are missing in the digital twin: {missing_keys}")

data = {
    'breed': twin['breed'],
    'rumination_time': twin['rumination_time'],
    'avg_lameness': twin['avg_lameness'],
    'estrus_sign': twin['estrus_sign'],
    'ax': twin['ax'],
    'ay': twin['ay'],
    'az': twin['az'],
    'gx': twin['gx'],
    'gy': twin['gy'],
    'gz': twin['gz'],
    'step_count': twin['step_count']
}
df = pd.DataFrame([data])

# --- MAKE PREDICTION ---
prediction = model.predict(df)[0]

# --- PATCH TO DIGITAL TWIN ---
patch = [
    {
        "op": "replace" if "predictionLabel" in twin else "add",
        "path": "/predictionLabel",
        "value": prediction
    }
]

try:
    client.update_digital_twin(cow_twin_id, patch)
    print(f"✅ Prediction '{prediction}' written to twin '{cow_twin_id}'")
    
    # 🔔 Trigger notification
    notify_user(prediction)

except Exception as e:
    print(f"❌ Failed to update twin: {e}")

# --- CONFIRM UPDATE ---
try:
    updated_twin = client.get_digital_twin(cow_twin_id)
    print("🔍 Current predictionLabel value:", updated_twin.get("predictionLabel", "Not found"))
    print("\n🧾 Full Twin JSON:")
    print(json.dumps(updated_twin, indent=2))

except Exception as e:
    print(f"❌ Failed to read updated twin: {e}")


# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = FastAPI(title="Cow Digital Twin Connector",
             description="API for interacting with cow digital twins",
             version="1.0.0")

# CORS Configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Azure ADT Setup
def initialize_adt_client():
    try:
        endpoint = os.getenv("ADT_ENDPOINT", "https://Cow-DigitalTwinModel.api.wcus.digitaltwins.azure.net")
        credential = DefaultAzureCredential()
        return DigitalTwinsClient(endpoint, credential)
    except Exception as e:
        logger.error(f"Failed to initialize ADT client: {str(e)}")
        raise

client = initialize_adt_client()

@app.get("/cow/{cow_id}", response_model=Dict[str, Any],
         summary="Get cow digital twin details",
         responses={
             200: {"description": "Successfully retrieved cow data"},
             404: {"description": "Cow not found"},
             500: {"description": "Internal server error"}
         })
async def get_cow_details(cow_id: str):
    """
    Retrieve digital twin data for a specific cow
    
    - **cow_id**: Unique identifier of the cow (e.g. co-01)
    """
    try:
        logger.info(f"Fetching digital twin for cow: {cow_id}")
        twin = client.get_digital_twin(cow_id)
        
        if not twin:
            logger.warning(f"Cow {cow_id} not found")
            raise HTTPException(status_code=404, detail="Cow not found")
            
        logger.info(f"Successfully retrieved data for cow {cow_id}")
        return {"cows": [twin]}
        
    except HttpResponseError as e:
        logger.error(f"Azure ADT error for cow {cow_id}: {str(e)}")
        if e.status_code == 404:
            raise HTTPException(status_code=404, detail="Cow not found")
        raise HTTPException(status_code=500, detail=str(e))
    except Exception as e:
        logger.error(f"Unexpected error for cow {cow_id}: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal server error")

@app.get("/health", include_in_schema=False)
async def health_check():
    """Endpoint for health checks"""
    return {"status": "healthy"}

if __name__ == "__main__":
    # Configuration for local development
    config = {
        "host": os.getenv("HOST", "0.0.0.0"),
        "port": int(os.getenv("PORT", 8000)),
        "reload": os.getenv("RELOAD", "true").lower() == "true",
        "workers": int(os.getenv("WORKERS", 1))
    }
    
    logger.info(f"Starting server on {config['host']}:{config['port']}")
    uvicorn.run(
        "digital_twin_connector:app",
        host=config["host"],
        port=config["port"],
        reload=config["reload"],
        workers=config["workers"]
    )